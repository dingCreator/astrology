package com.dingCreator.astrology.util;

import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.dto.equipment.EquipmentPropertiesDTO;
import com.dingCreator.astrology.dto.organism.player.PlayerInfoDTO;
import com.dingCreator.astrology.dto.equipment.EquipmentBarDTO;
import com.dingCreator.astrology.dto.equipment.EquipmentDTO;
import com.dingCreator.astrology.entity.EquipmentBelongTo;
import com.dingCreator.astrology.enums.BelongToEnum;
import com.dingCreator.astrology.enums.EquipmentSuitEnum;
import com.dingCreator.astrology.enums.equipment.EquipmentPropertiesTypeEnum;
import com.dingCreator.astrology.enums.equipment.EquipmentEnum;
import com.dingCreator.astrology.enums.equipment.EquipmentTypeEnum;
import com.dingCreator.astrology.enums.exception.EquipmentExceptionEnum;
import com.dingCreator.astrology.service.EquipmentBelongToService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author ding
 * @date 2024/3/25
 */
public class EquipmentUtil {

    /**
     * 校验
     *
     * @param playerId          玩家ID
     * @param equipmentBelongTo 装备
     */
    public static void validate(long playerId, EquipmentBelongTo equipmentBelongTo) {
        if (Objects.isNull(equipmentBelongTo)) {
            throw EquipmentExceptionEnum.DONT_HAVE_EQUIPMENT.getException();
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
        if (Objects.isNull(equipmentPropertiesTypeEnum)) {
            return val;
        }
        // 武器
        val = getLongVal(val, equipmentPropertiesTypeEnum, equipmentBarDTO.getWeapon(), equipmentBarDTO);
        // 防具
        val = getLongVal(val, equipmentPropertiesTypeEnum, equipmentBarDTO.getArmor(), equipmentBarDTO);
        // 饰品
        val = getLongVal(val, equipmentPropertiesTypeEnum, equipmentBarDTO.getJewelry(), equipmentBarDTO);
        return val;
    }

    public static Long getLongVal(Long src, EquipmentPropertiesTypeEnum equipmentPropertiesTypeEnum,
                                  EquipmentDTO equipmentDTO, EquipmentBarDTO bar) {
        if (Objects.isNull(equipmentDTO)) {
            return src;
        }
        EquipmentEnum equipEnum = EquipmentEnum.getById(equipmentDTO.getEquipmentId());
        AtomicLong atomicLong = new AtomicLong(src);
        float sumRate = equipEnum.getProp().stream()
                .filter(equip -> equipmentPropertiesTypeEnum.equals(equip.getEquipmentPropertiesTypeEnum()))
                .peek(prop -> {
                    Long propLongVal = prop.getProp().getVal();
                    EquipmentSuitEnum suit = EquipmentSuitEnum.getByEquipmentId(equipmentDTO.getEquipmentId());
                    if (Objects.nonNull(suit)) {
                        propLongVal = suit.getEquipmentSuit().changeLongProperty(propLongVal, bar);
                    }
                    atomicLong.addAndGet(propLongVal);
                })
                .map(EquipmentPropertiesDTO::getProp)
                .map(prop -> {
                    Float propRateVal = prop.getRate();
                    EquipmentSuitEnum suit = EquipmentSuitEnum.getByEquipmentId(equipmentDTO.getEquipmentId());
                    if (Objects.nonNull(suit)) {
                        propRateVal = suit.getEquipmentSuit().changeRateProperty(propRateVal, bar);
                    }
                    return propRateVal;
                })
                .filter(rate -> rate != 0)
                .reduce(Float::sum).orElse(0F);
        if (atomicLong.get() < 0) {
            atomicLong.set(0);
        }
        atomicLong.set(Math.round(atomicLong.get() * (1 + sumRate)));
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
        if (Objects.isNull(equipmentPropertiesTypeEnum)) {
            return val;
        }
        // 武器
        val = getFloatVal(val, equipmentPropertiesTypeEnum, equipmentBarDTO.getWeapon(), equipmentBarDTO);
        // 防具
        val = getFloatVal(val, equipmentPropertiesTypeEnum, equipmentBarDTO.getArmor(), equipmentBarDTO);
        // 饰品
        val = getFloatVal(val, equipmentPropertiesTypeEnum, equipmentBarDTO.getJewelry(), equipmentBarDTO);
        return val;
    }

    public static Float getFloatVal(final Float src, EquipmentPropertiesTypeEnum equipmentPropertiesTypeEnum,
                                    EquipmentDTO equipmentDTO, EquipmentBarDTO bar) {
        if (Objects.isNull(equipmentDTO)) {
            return src;
        }

        EquipmentEnum equipEnum = EquipmentEnum.getById(equipmentDTO.getEquipmentId());
        return equipEnum.getProp().stream()
                .filter(equip -> equipmentPropertiesTypeEnum.equals(equip.getEquipmentPropertiesTypeEnum()))
                .map(prop -> {
                    Float propRateVal = prop.getProp().getRate();
                    EquipmentSuitEnum suit = EquipmentSuitEnum.getByEquipmentId(equipmentDTO.getEquipmentId());
                    if (Objects.nonNull(suit)) {
                        propRateVal = suit.getEquipmentSuit().changeRateProperty(propRateVal, bar);
                    }
                    return propRateVal;
                })
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
                    equipmentBelongTo.getEquipmentLevel()));
        } else if (EquipmentTypeEnum.WEAPON.equals(equipmentEnum.getEquipmentTypeEnum())) {
            equipmentBarDTO.setWeapon(new EquipmentDTO(equipmentBelongTo.getId(), equipmentBelongTo.getEquipmentId(),
                    equipmentBelongTo.getEquipmentLevel()));
        } else if (EquipmentTypeEnum.JEWELRY.equals(equipmentEnum.getEquipmentTypeEnum())) {
            equipmentBarDTO.setJewelry(new EquipmentDTO(equipmentBelongTo.getId(), equipmentBelongTo.getEquipmentId(),
                    equipmentBelongTo.getEquipmentLevel()));
        }
    }

}
