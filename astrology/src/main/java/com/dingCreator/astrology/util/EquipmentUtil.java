package com.dingCreator.astrology.util;

import com.dingCreator.astrology.dto.EquipmentDTO;
import com.dingCreator.astrology.dto.EquipmentPropertiesDTO;
import com.dingCreator.astrology.dto.OrganismDTO;
import com.dingCreator.astrology.entity.EquipmentBelongTo;
import com.dingCreator.astrology.enums.PropertiesTypeEnum;
import com.dingCreator.astrology.enums.equipment.EquipmentEnum;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * @author ding
 * @date 2024/3/25
 */
public class EquipmentUtil {

    public static long getVal(long val, PropertiesTypeEnum propertiesTypeEnum, OrganismDTO organismDTO) {
        if (Objects.isNull(organismDTO.getEquipmentBarDTO())) {
            return val;
        }
        // 武器
        val = getVal(val, propertiesTypeEnum, organismDTO.getEquipmentBarDTO().getWeapon());
        // 防具
        val = getVal(val, propertiesTypeEnum, organismDTO.getEquipmentBarDTO().getArmor());
        // 饰品
        val = getVal(val, propertiesTypeEnum, organismDTO.getEquipmentBarDTO().getJewelry());
        return val;
    }

    public static long getVal(long src, PropertiesTypeEnum propertiesTypeEnum, EquipmentDTO equipmentDTO) {
        if (Objects.isNull(equipmentDTO)) {
            return src;
        }
        EquipmentEnum equipEnum = EquipmentEnum.getById(equipmentDTO.getEquipmentId());
        AtomicLong atomicLong = new AtomicLong(src);
        equipEnum.getProp().stream().filter(equip -> propertiesTypeEnum.equals(equip.getEquipmentPropertiesTypeEnum()))
                .forEach(prop -> {
                    atomicLong.addAndGet(prop.getVal());
                    atomicLong.updateAndGet(prev -> Math.round(prev * (1 + prop.getRate())));
                });
        return atomicLong.get();
    }
}
