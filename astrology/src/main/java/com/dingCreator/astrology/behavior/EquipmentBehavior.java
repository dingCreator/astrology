package com.dingCreator.astrology.behavior;

import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.dto.equipment.EquipmentBarDTO;
import com.dingCreator.astrology.entity.EquipmentBelongTo;
import com.dingCreator.astrology.enums.BelongToEnum;
import com.dingCreator.astrology.enums.equipment.EquipmentEnum;
import com.dingCreator.astrology.enums.equipment.EquipmentTypeEnum;
import com.dingCreator.astrology.enums.exception.EquipmentExceptionEnum;
import com.dingCreator.astrology.response.PageResponse;
import com.dingCreator.astrology.service.EquipmentBelongToService;
import com.dingCreator.astrology.util.EquipmentUtil;
import com.dingCreator.astrology.util.PageUtil;
import com.dingCreator.astrology.vo.EquipmentGroupVO;
import com.dingCreator.astrology.vo.EquipmentVO;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author ding
 * @date 2024/4/15
 */
public class EquipmentBehavior {

    private final EquipmentBelongToService equipmentBelongToService = EquipmentBelongToService.getInstance();

    /**
     * 穿装备
     */
    public void equip(long playerId, long id) {
        EquipmentBelongTo equipmentBelongTo = equipmentBelongToService.getById(id);
        EquipmentUtil.validate(playerId, equipmentBelongTo);
        EquipmentUtil.updateEquipment(playerId, equipmentBelongTo, true);
    }

    /**
     * 卸下装备
     *
     * @param playerId 玩家ID
     */
    public void remove(long playerId, EquipmentTypeEnum equipmentTypeEnum) {
        equipmentTypeEnum.getRemove().accept(playerId);
    }

    /**
     * 获取所有装备
     *
     * @param playerId 玩家ID
     * @return 装备概览
     */
    public PageResponse<EquipmentGroupVO> listEquipmentGroup(long playerId, int pageIndex, int pageSize) {
        // 校验一下有没有创建角色
        PlayerCache.getPlayerById(playerId);
        List<EquipmentGroupVO> list = equipmentBelongToService.listGroupByBelongToId(BelongToEnum.PLAYER.getBelongTo(), playerId);
        return PageUtil.buildPage(list, pageIndex, pageSize);
    }

    /**
     * 查询装备背包
     *
     * @param playerId      玩家ID
     * @param equipmentName 装备名称
     * @return 装备背包
     */
    public PageResponse<EquipmentVO> listPlayerEquipmentByName(long playerId, String equipmentName, int pageIndex, int pageSize) {
        PlayerCache.getPlayerById(playerId);
        EquipmentEnum equipmentEnum = EquipmentEnum.getByName(equipmentName);

        List<EquipmentBelongTo> equipmentBelongToList = equipmentBelongToService.getByBelongToIdAndEquipmentId(
                BelongToEnum.PLAYER.getBelongTo(), playerId, equipmentEnum.getId());
        if (Objects.isNull(equipmentBelongToList) || equipmentBelongToList.size() == 0) {
            throw EquipmentExceptionEnum.DONT_HAVE_EQUIPMENT.getException();
        }

        List<EquipmentVO> equipmentVoList = equipmentBelongToList.stream().map(equipmentBelongTo -> {
            String propStr = equipmentEnum.getProp().stream().map(prop -> {
                StringBuilder builder = new StringBuilder();
                if (prop.getProp().getVal() != 0) {
                    builder.append(prop.getEquipmentPropertiesTypeEnum().getNameCh());
                    if (prop.getProp().getVal() > 0) {
                        builder.append("+");
                    }
                    builder.append(prop.getProp().getVal()).append(Constants.BLANK);
                }
                if (prop.getProp().getRate() != 0) {
                    builder.append(prop.getEquipmentPropertiesTypeEnum().getNameCh());
                    if (prop.getProp().getRate() > 0) {
                        builder.append("+");
                    }
                    builder.append(prop.getProp().getRate() * 100);
                    builder.append("%").append(Constants.BLANK);
                }
                return builder;
            }).reduce((builder1, builder2) -> builder1.append("\n").append(builder2))
                    .orElse(new StringBuilder()).toString();

            EquipmentVO equipmentVO = new EquipmentVO();
            equipmentVO.setRank(equipmentEnum.getEquipmentRankEnum());
            equipmentVO.setId(equipmentBelongTo.getId());
            equipmentVO.setEquipmentName(equipmentEnum.getName());
            equipmentVO.setLimitJob(equipmentEnum.getLimitJob());
            equipmentVO.setLimitLevel(equipmentEnum.getLimitLevel());
            equipmentVO.setEquipmentProperty(propStr);
            equipmentVO.setLevel(equipmentBelongTo.getLevel());
            equipmentVO.setEquip(equipmentBelongTo.getEquip());
            return equipmentVO;
        }).collect(Collectors.toList());
        return PageUtil.buildPage(equipmentVoList, pageIndex, pageSize);
    }

    /**
     * 查询装备栏
     *
     * @param playerId 玩家ID
     * @return 装备栏
     */
    public EquipmentBarDTO getEquipmentBar(long playerId) {
        return PlayerCache.getPlayerById(playerId).getEquipmentBarDTO();
    }

    /**
     * 获取所有装备
     *
     * @param pageIndex 页码
     * @param pageSize  页码大小
     * @return 装备
     */
    public PageResponse<EquipmentEnum> queryPage(int pageIndex, int pageSize) {
        EquipmentEnum[] array = EquipmentEnum.values();
        return PageUtil.buildPage(array, pageIndex, pageSize);
    }

    /**
     * 赠送装备
     *
     * @param equipmentId 装备ID
     * @param playerId    玩家ID
     */
    public void sendEquipment(long equipmentId, long playerId) {
        if (Objects.isNull(EquipmentEnum.getById(equipmentId))) {
            throw EquipmentExceptionEnum.EQUIPMENT_NOT_EXIST.getException();
        }
        // 校验玩家ID
        PlayerCache.getPlayerById(playerId);
        EquipmentBelongTo belongTo = new EquipmentBelongTo();
        belongTo.setBelongTo(BelongToEnum.PLAYER.getBelongTo());
        belongTo.setBelongToId(playerId);
        belongTo.setEquipmentId(equipmentId);
        belongTo.setEquip(false);
        belongTo.setLevel(1);
        equipmentBelongToService.addBelongTo(belongTo);
    }

    private static class Holder {
        private static final EquipmentBehavior BEHAVIOR = new EquipmentBehavior();
    }

    private EquipmentBehavior() {

    }

    public static EquipmentBehavior getInstance() {
        return EquipmentBehavior.Holder.BEHAVIOR;
    }
}
