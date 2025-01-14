package com.dingCreator.astrology.util;

import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.dto.organism.player.PlayerInfoDTO;
import com.dingCreator.astrology.dto.equipment.EquipmentBarDTO;
import com.dingCreator.astrology.dto.equipment.EquipmentDTO;
import com.dingCreator.astrology.entity.EquipmentBelongTo;
import com.dingCreator.astrology.enums.BelongToEnum;
import com.dingCreator.astrology.enums.equipment.EquipmentPropertiesTypeEnum;
import com.dingCreator.astrology.enums.equipment.EquipmentEnum;
import com.dingCreator.astrology.enums.equipment.EquipmentTypeEnum;
import com.dingCreator.astrology.enums.exception.EquipmentExceptionEnum;
import com.dingCreator.astrology.service.EquipmentBelongToService;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author ding
 * @date 2024/3/25
 */
public class EquipmentUtil {

    static EquipmentBelongToService equipmentBelongToService = EquipmentBelongToService.getInstance();

    /**
     * 校验
     *
     * @param playerId          玩家ID
     * @param equipmentBelongTo 装备
     */
    public static void validate(long playerId, EquipmentBelongTo equipmentBelongTo) {
        if (Objects.isNull(equipmentBelongTo)) {
            throw EquipmentExceptionEnum.EQUIPMENT_NOT_EXIST.getException();
        }
        if (!BelongToEnum.PLAYER.getBelongTo().equals(equipmentBelongTo.getBelongTo())
                || !equipmentBelongTo.getBelongToId().equals(playerId)) {
            throw EquipmentExceptionEnum.NOT_YOUR_EQUIPMENT.getException();
        }
        EquipmentEnum equipmentEnum = EquipmentEnum.getById(equipmentBelongTo.getEquipmentId());
        if (Objects.isNull(equipmentEnum)) {
            throw EquipmentExceptionEnum.DATA_ERROR.getException();
        }

        PlayerInfoDTO infoDTO = PlayerCache.getPlayerById(playerId);
        if (!equipmentEnum.getLimitJob().contains(Constants.ALL)
                && !equipmentEnum.getLimitJob().contains(infoDTO.getPlayerDTO().getJob())) {
            throw EquipmentExceptionEnum.INVALID_JOB.getException();
        }
        if (equipmentEnum.getLimitLevel() > infoDTO.getPlayerDTO().getLevel()) {
            throw EquipmentExceptionEnum.LEVEL_TOO_LOW.getException();
        }
    }

    /**
     * 获取指定属性
     *
     * @param val                         原属性
     * @param equipmentPropertiesTypeEnum 属性类型
     * @param equipmentBarDTO             装备栏
     * @return 计算装备后的属性
     */
    public static Long getLongVal(Long val, EquipmentPropertiesTypeEnum equipmentPropertiesTypeEnum,
                                  EquipmentBarDTO equipmentBarDTO) {
        if (Objects.isNull(equipmentBarDTO)) {
            return val;
        }
        // 武器
        val = getLongVal(val, equipmentPropertiesTypeEnum, equipmentBarDTO.getWeapon());
        // 防具
        val = getLongVal(val, equipmentPropertiesTypeEnum, equipmentBarDTO.getArmor());
        // 饰品
        val = getLongVal(val, equipmentPropertiesTypeEnum, equipmentBarDTO.getJewelry());
        return val;
    }

    public static Long getLongVal(Long src, EquipmentPropertiesTypeEnum equipmentPropertiesTypeEnum,
                                  EquipmentDTO equipmentDTO) {
        if (Objects.isNull(equipmentDTO)) {
            return src;
        }
        EquipmentEnum equipEnum = EquipmentEnum.getById(equipmentDTO.getEquipmentId());
        AtomicLong atomicLong = new AtomicLong(src);
        equipEnum.getProp().stream()
                .filter(equip -> equipmentPropertiesTypeEnum.equals(equip.getEquipmentPropertiesTypeEnum()))
                .forEach(prop -> {
                    atomicLong.addAndGet(prop.getProp().getVal());
                    atomicLong.updateAndGet(prev -> Math.round(prev * (1 + prop.getProp().getRate())));
                });
        return atomicLong.get();
    }

    /**
     * 获取指定属性
     *
     * @param val                         原属性
     * @param equipmentPropertiesTypeEnum 属性类型
     * @param equipmentBarDTO             装备栏
     * @return 计算装备后的属性
     */
    public static Float getFloatVal(Float val, EquipmentPropertiesTypeEnum equipmentPropertiesTypeEnum,
                                    EquipmentBarDTO equipmentBarDTO) {
        if (Objects.isNull(equipmentBarDTO)) {
            return val;
        }
        // 武器
        val = getFloatVal(val, equipmentPropertiesTypeEnum, equipmentBarDTO.getWeapon());
        // 防具
        val = getFloatVal(val, equipmentPropertiesTypeEnum, equipmentBarDTO.getArmor());
        // 饰品
        val = getFloatVal(val, equipmentPropertiesTypeEnum, equipmentBarDTO.getJewelry());
        return val;
    }

    public static Float getFloatVal(final Float src, EquipmentPropertiesTypeEnum equipmentPropertiesTypeEnum,
                                    EquipmentDTO equipmentDTO) {
        if (Objects.isNull(equipmentDTO)) {
            return src;
        }

        EquipmentEnum equipEnum = EquipmentEnum.getById(equipmentDTO.getEquipmentId());
        return equipEnum.getProp().stream()
                .filter(equip -> equipmentPropertiesTypeEnum.equals(equip.getEquipmentPropertiesTypeEnum()))
                .map(prop -> prop.getProp().getRate())
                .reduce(src, Float::sum);
    }

    /**
     * 设置装备栏
     *
     * @param equipmentBarDTO   装备栏
     * @param equipmentEnum     装备
     * @param equipmentBelongTo 装备归属
     */
    public static void setEquipmentBarDTO(EquipmentBarDTO equipmentBarDTO, EquipmentEnum equipmentEnum,
                                          EquipmentBelongTo equipmentBelongTo) {
        if (EquipmentTypeEnum.ARMOR.equals(equipmentEnum.getEquipmentTypeEnum())) {
            equipmentBarDTO.setArmor(new EquipmentDTO(equipmentBelongTo.getId(), equipmentBelongTo.getEquipmentId(),
                    equipmentBelongTo.getLevel()));
        } else if (EquipmentTypeEnum.WEAPON.equals(equipmentEnum.getEquipmentTypeEnum())) {
            equipmentBarDTO.setWeapon(new EquipmentDTO(equipmentBelongTo.getId(), equipmentBelongTo.getEquipmentId(),
                    equipmentBelongTo.getLevel()));
        } else if (EquipmentTypeEnum.JEWELRY.equals(equipmentEnum.getEquipmentTypeEnum())) {
            equipmentBarDTO.setJewelry(new EquipmentDTO(equipmentBelongTo.getId(), equipmentBelongTo.getEquipmentId(),
                    equipmentBelongTo.getLevel()));
        }
    }

    public static void updateWeapon(Long playerId, EquipmentBelongTo equipmentBelongTo) {
        PlayerInfoDTO info = PlayerCache.getPlayerById(playerId);

    }



    public static void updateEquipment(Long playerId, EquipmentBelongTo equipmentBelongTo, boolean equip) {
        PlayerInfoDTO playerInfoDTO = PlayerCache.getPlayerById(playerId);
        EquipmentEnum equipmentEnum = EquipmentEnum.getById(equipmentBelongTo.getEquipmentId());
        EquipmentBarDTO equipmentBarDTO = playerInfoDTO.getEquipmentBarDTO();

        if (EquipmentTypeEnum.WEAPON.equals(equipmentEnum.getEquipmentTypeEnum())) {
            if (Objects.nonNull(equipmentBarDTO.getWeapon())) {
                equipmentBelongToService.updateEquipment(equipmentBarDTO.getWeapon().getId(), false);
            }
            equipmentBarDTO.setWeapon(new EquipmentDTO(equipmentBelongTo.getId(), equipmentBelongTo.getEquipmentId(),
                    equipmentBelongTo.getLevel()));
        } else if (EquipmentTypeEnum.ARMOR.equals(equipmentEnum.getEquipmentTypeEnum())) {
            if (Objects.nonNull(equipmentBarDTO.getArmor())) {
                equipmentBelongToService.updateEquipment(equipmentBarDTO.getArmor().getId(), false);
            }
            equipmentBarDTO.setArmor(new EquipmentDTO(equipmentBelongTo.getId(), equipmentBelongTo.getEquipmentId(),
                    equipmentBelongTo.getLevel()));
        } else if (EquipmentTypeEnum.JEWELRY.equals(equipmentEnum.getEquipmentTypeEnum())) {
            if (Objects.nonNull(equipmentBarDTO.getJewelry())) {
                equipmentBelongToService.updateEquipment(equipmentBarDTO.getJewelry().getId(), false);
            }
            equipmentBarDTO.setJewelry(new EquipmentDTO(equipmentBelongTo.getId(), equipmentBelongTo.getEquipmentId(),
                    equipmentBelongTo.getLevel()));
        }
        playerInfoDTO.getPlayerDTO().clearAdditionVal();
        equipmentBelongToService.updateEquipment(equipmentBelongTo.getId(), equip);
    }
}
