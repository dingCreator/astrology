package com.dingCreator.astrology.dto;

import com.dingCreator.astrology.enums.PropertiesTypeEnum;
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
    private PropertiesTypeEnum equipmentPropertiesTypeEnum;
    /**
     * 属性值
     */
    private Double val;
}
