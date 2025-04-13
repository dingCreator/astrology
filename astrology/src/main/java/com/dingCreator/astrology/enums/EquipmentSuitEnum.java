package com.dingCreator.astrology.enums;

import com.dingCreator.astrology.dto.equipment.EquipmentBarDTO;
import com.dingCreator.astrology.enums.equipment.EquipmentEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author ding
 * @date 2025/3/24
 */
@Getter
@AllArgsConstructor
public enum EquipmentSuitEnum {

    SUIT_413_414("九临清河晏", new EquipmentSuit(
            EquipmentEnum.EQUIPMENT_414.getId(),
            null,
            EquipmentEnum.EQUIPMENT_413.getId()) {

        @Override
        public Long changeLongProperty(Long src, EquipmentBarDTO bar) {
            if (suitNum(bar) >= 2) {
                return src * 2;
            }
            return src;
        }

        @Override
        public Float changeRateProperty(Float src, EquipmentBarDTO bar) {
            if (suitNum(bar) >= 2) {
                return src * 2;
            }
            return src;
        }
    }),
    ;
    private final String suitEffectName;
    private final EquipmentSuit equipmentSuit;

    private static final Map<Long, EquipmentSuitEnum> EQUIPMENT_ENUM_SUIT_MAP = new HashMap<>();

    static {
        EQUIPMENT_ENUM_SUIT_MAP.put(EquipmentEnum.EQUIPMENT_413.getId(), SUIT_413_414);
        EQUIPMENT_ENUM_SUIT_MAP.put(EquipmentEnum.EQUIPMENT_414.getId(), SUIT_413_414);
    }

    @Data
    @Getter
    @AllArgsConstructor
    public static class EquipmentSuit {
        /**
         * 武器ID
         */
        private Long weaponId;
        /**
         * 防具ID
         */
        private Long armorId;
        /**
         * 饰品ID
         */
        private Long jewelryId;

        /**
         * 已穿套装数量
         *
         * @param bar 装备栏
         * @return 数量
         */
        public Integer suitNum(EquipmentBarDTO bar) {
            int num = 0;
            if (Objects.nonNull(bar.getWeapon()) && bar.getWeapon().getEquipmentId().equals(weaponId)) {
                num++;
            }
            if (Objects.nonNull(bar.getArmor()) && bar.getArmor().getEquipmentId().equals(armorId)) {
                num++;
            }
            if (Objects.nonNull(bar.getJewelry()) && bar.getJewelry().getEquipmentId().equals(jewelryId)) {
                num++;
            }
            return num;
        }

        public Long changeLongProperty(Long src, EquipmentBarDTO bar) {
            return src;
        }

        public Float changeRateProperty(Float src, EquipmentBarDTO bar) {
            return src;
        }
    }

    public static EquipmentSuitEnum getByEquipmentId(Long equipmentId) {
        return EQUIPMENT_ENUM_SUIT_MAP.get(equipmentId);
    }
}
