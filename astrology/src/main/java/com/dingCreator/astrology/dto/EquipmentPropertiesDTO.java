package com.dingCreator.astrology.dto;

import com.dingCreator.astrology.enums.PropertiesTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author ding
 * @date 2024/3/24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentPropertiesDTO implements Serializable {
    /**
     * 属性类型
     */
    private PropertiesTypeEnum equipmentPropertiesTypeEnum;
    /**
     * 属性值
     */
    private Long val;
    /**
     * 属性比例
     */
    private Float rate;

    public EquipmentPropertiesDTO(PropertiesTypeEnum equipmentPropertiesTypeEnum, Long val) {
        this.equipmentPropertiesTypeEnum = equipmentPropertiesTypeEnum;
        this.val = val;
    }

    public EquipmentPropertiesDTO(PropertiesTypeEnum equipmentPropertiesTypeEnum, Float rate) {
        this.equipmentPropertiesTypeEnum = equipmentPropertiesTypeEnum;
        this.rate = rate;
    }
}
