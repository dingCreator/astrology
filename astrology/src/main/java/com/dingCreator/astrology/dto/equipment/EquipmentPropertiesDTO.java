package com.dingCreator.astrology.dto.equipment;

import com.dingCreator.astrology.enums.equipment.EquipmentPropertiesTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @author ding
 * @date 2024/3/24
 */
@Data
public class EquipmentPropertiesDTO implements Serializable {
    /**
     * 属性类型
     */
    private EquipmentPropertiesTypeEnum equipmentPropertiesTypeEnum;
    /**
     * 属性值
     */
    private Prop prop;

    public EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum equipmentPropertiesTypeEnum, Long val) {
        this.equipmentPropertiesTypeEnum = equipmentPropertiesTypeEnum;
        this.prop = new Prop(val, 0F);
    }

    public EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum equipmentPropertiesTypeEnum, Float rate) {
        this.equipmentPropertiesTypeEnum = equipmentPropertiesTypeEnum;
        this.prop = new Prop(0L, rate);
    }

    public EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum equipmentPropertiesTypeEnum, Long val, Float rate) {
        this.equipmentPropertiesTypeEnum = equipmentPropertiesTypeEnum;
        this.prop = new Prop(val, rate);
    }

    @Data
    @AllArgsConstructor
    public static class Prop {
        /**
         * 属性值
         */
        private Long val;
        /**
         * 属性比例
         */
        private Float rate;
    }
}
