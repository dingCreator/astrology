package com.dingCreator.astrology.behavior;

import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.dto.equipment.EquipmentBarDTO;
import com.dingCreator.astrology.entity.EquipmentBelongTo;
import com.dingCreator.astrology.enums.BelongToEnum;
import com.dingCreator.astrology.enums.equipment.EquipmentEnum;
import com.dingCreator.astrology.enums.exception.EquipmentExceptionEnum;
import com.dingCreator.astrology.service.EquipmentBelongToService;
import com.dingCreator.astrology.util.EquipmentUtil;
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

    /**
     * 穿装备
     */
    public void equip(long playerId, long id) {
        EquipmentBelongTo equipmentBelongTo = EquipmentBelongToService.getById(id);
        EquipmentUtil.validate(playerId, equipmentBelongTo);
        EquipmentUtil.updateEquipment(playerId, equipmentBelongTo, true);
    }

    /**
     * 卸下装备
     *
     * @param playerId 玩家ID
     */
    public void remove(long playerId, long id) {
        EquipmentBelongTo equipmentBelongTo = EquipmentBelongToService.getById(id);
        EquipmentUtil.validate(playerId, equipmentBelongTo);
        EquipmentUtil.updateEquipment(playerId, equipmentBelongTo, false);
    }

    /**
     * 获取所有装备
     *
     * @param playerId 玩家ID
     * @return 装备概览
     */
    public List<EquipmentGroupVO> listEquipmentGroup(long playerId) {
        PlayerCache.getPlayerById(playerId);
        return EquipmentBelongToService.listGroupByBelongToId(BelongToEnum.PLAYER.getBelongTo(), playerId);
    }

    /**
     * 查询装备背包
     *
     * @param playerId      玩家ID
     * @param equipmentName 装备名称
     * @return 装备背包
     */
    public List<EquipmentVO> listPlayerEquipmentByName(long playerId, String equipmentName) {
        PlayerCache.getPlayerById(playerId);
        EquipmentEnum equipmentEnum = EquipmentEnum.getByName(equipmentName);
        if (Objects.isNull(equipmentEnum)) {
            throw EquipmentExceptionEnum.EQUIPMENT_NOT_EXIST.getException();
        }

        List<EquipmentBelongTo> equipmentBelongToList = EquipmentBelongToService.getByBelongToIdAndEquipmentId(
                BelongToEnum.PLAYER.getBelongTo(), playerId, equipmentEnum.getId());
        if (Objects.isNull(equipmentBelongToList) || equipmentBelongToList.size() == 0) {
            throw EquipmentExceptionEnum.DONT_HAVE_EQUIPMENT.getException();
        }

        return equipmentBelongToList.stream().map(equipmentBelongTo -> {
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
                    if (prop.getProp().getVal() > 0) {
                        builder.append("+");
                    }
                    builder.append(prop.getProp().getRate() * 100);
                    builder.append("%").append(Constants.BLANK);
                }
                return builder;
            }).reduce(StringBuilder::append).orElse(new StringBuilder()).toString();

            EquipmentVO equipmentVO = new EquipmentVO();
            equipmentVO.setId(equipmentBelongTo.getId());
            equipmentVO.setEquipmentName(equipmentEnum.getName());
            equipmentVO.setEquipmentProperty(propStr);
            equipmentVO.setLevel(equipmentBelongTo.getLevel());
            equipmentVO.setEquip(equipmentBelongTo.getEquip() ? "已装备" : "未装备");
            return equipmentVO;
        }).collect(Collectors.toList());
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
    public List<EquipmentEnum> queryPage(int pageIndex, int pageSize) {
        return Arrays.stream(EquipmentEnum.values()).skip(pageIndex * pageSize).limit(pageSize).collect(Collectors.toList());
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
        EquipmentBelongToService.addBelongTo(belongTo);
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
