package com.dingCreator.astrology.behavior;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.dto.equipment.EquipmentBarDTO;
import com.dingCreator.astrology.dto.equipment.EquipmentDTO;
import com.dingCreator.astrology.dto.organism.player.PlayerInfoDTO;
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

import java.util.ArrayList;
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
    public void equip(long playerId, String equipmentName, int level) {
        EquipmentBelongTo equipmentBelongTo = equipmentBelongToService
                .getByNameAndLevel(BelongToEnum.PLAYER.getBelongTo(), playerId, equipmentName, level);
        EquipmentUtil.validate(playerId, equipmentBelongTo);
        PlayerInfoDTO playerInfoDTO = PlayerCache.getPlayerById(playerId);
        EquipmentEnum equipmentEnum = EquipmentEnum.getById(equipmentBelongTo.getEquipmentId());
        EquipmentBarDTO equipmentBarDTO = playerInfoDTO.getEquipmentBarDTO();

        if (EquipmentTypeEnum.WEAPON.equals(equipmentEnum.getEquipmentTypeEnum())) {
            if (Objects.nonNull(equipmentBarDTO.getWeapon())) {
                equipmentBelongToService.updateEquipment(equipmentBarDTO.getWeapon().getId(), false);
            }
            equipmentBarDTO.setWeapon(new EquipmentDTO(equipmentBelongTo.getId(), equipmentBelongTo.getEquipmentId(),
                    equipmentBelongTo.getEquipmentLevel()));
        } else if (EquipmentTypeEnum.ARMOR.equals(equipmentEnum.getEquipmentTypeEnum())) {
            if (Objects.nonNull(equipmentBarDTO.getArmor())) {
                equipmentBelongToService.updateEquipment(equipmentBarDTO.getArmor().getId(), false);
            }
            equipmentBarDTO.setArmor(new EquipmentDTO(equipmentBelongTo.getId(), equipmentBelongTo.getEquipmentId(),
                    equipmentBelongTo.getEquipmentLevel()));
        } else if (EquipmentTypeEnum.JEWELRY.equals(equipmentEnum.getEquipmentTypeEnum())) {
            if (Objects.nonNull(equipmentBarDTO.getJewelry())) {
                equipmentBelongToService.updateEquipment(equipmentBarDTO.getJewelry().getId(), false);
            }
            equipmentBarDTO.setJewelry(new EquipmentDTO(equipmentBelongTo.getId(), equipmentBelongTo.getEquipmentId(),
                    equipmentBelongTo.getEquipmentLevel()));
        }
        playerInfoDTO.getPlayerDTO().clearAdditionVal();
        equipmentBelongToService.updateEquipment(equipmentBelongTo.getId(), true);
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
    public PageResponse<EquipmentGroupVO> listEquipmentGroup(long playerId, int pageIndex, int pageSize, String equipmentName) {
        // 校验一下有没有创建角色
        PlayerCache.getPlayerById(playerId);
        List<EquipmentGroupVO> list = new ArrayList<>();
        int total = 0;
        if (StringUtils.isBlank(equipmentName)) {
            total = equipmentBelongToService.selectCount(BelongToEnum.PLAYER.getBelongTo(), playerId, null);
            list = equipmentBelongToService.listGroupByBelongToId(BelongToEnum.PLAYER.getBelongTo(), playerId, null);
        } else {
            // 获取含关键字的装备ID
            List<EquipmentEnum> enumList = EquipmentEnum.fuzzyQryByName(equipmentName);
            if (CollectionUtil.isNotEmpty(enumList)) {
                List<Long> equipmentIds = enumList.stream().map(EquipmentEnum::getId).collect(Collectors.toList());
                total = equipmentBelongToService.selectCount(BelongToEnum.PLAYER.getBelongTo(), playerId, equipmentIds);
                list = equipmentBelongToService.listGroupByBelongToId(BelongToEnum.PLAYER.getBelongTo(), playerId, equipmentIds);
            }
        }
        return PageUtil.buildPage(list, pageIndex, pageSize);
    }

    /**
     * 查询装备背包
     *
     * @param playerId      玩家ID
     * @param equipmentName 装备名称
     * @return 装备背包
     */
    public EquipmentVO getPlayerEquipmentByName(long playerId, String equipmentName) {
        PlayerCache.getPlayerById(playerId);
        EquipmentEnum equipmentEnum = EquipmentEnum.getByName(equipmentName);

        EquipmentBelongTo equipmentBelongTo = equipmentBelongToService.getByBelongToIdAndEquipmentId(
                BelongToEnum.PLAYER.getBelongTo(), playerId, equipmentEnum.getId());
        if (Objects.isNull(equipmentBelongTo)) {
            throw EquipmentExceptionEnum.DONT_HAVE_EQUIPMENT.getException();
        }

        String propStr = equipmentEnum.getProp().stream()
                .map(prop -> {
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
                })
                .reduce((builder1, builder2) -> builder1.append("\n").append(builder2))
                .orElse(new StringBuilder()).toString();

        EquipmentVO equipmentVO = new EquipmentVO();
        equipmentVO.setRank(equipmentEnum.getEquipmentRankEnum());
        equipmentVO.setId(equipmentBelongTo.getId());
        equipmentVO.setEquipmentName(equipmentEnum.getName());
        equipmentVO.setType(equipmentEnum.getEquipmentTypeEnum());
        equipmentVO.setLimitJob(equipmentEnum.getLimitJob());
        equipmentVO.setLimitLevel(equipmentEnum.getLimitLevel());
        equipmentVO.setEquipmentProperty(propStr);
        equipmentVO.setLevel(equipmentBelongTo.getEquipmentLevel());
        equipmentVO.setEquip(equipmentBelongTo.getEquip());
        equipmentVO.setCnt(equipmentBelongTo.getTotalCnt());
        equipmentVO.setDescription(equipmentEnum.getDesc());
        return equipmentVO;
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
        belongTo.setTotalCnt(1);
        belongTo.setEquip(false);
        belongTo.setEquipmentLevel(1);
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
