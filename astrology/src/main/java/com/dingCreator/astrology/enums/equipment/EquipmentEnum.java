package com.dingCreator.astrology.enums.equipment;

import com.dingCreator.astrology.dto.EquipmentPropertiesDTO;
import com.dingCreator.astrology.enums.OrganismEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author ding
 * @date 2024/3/24
 */
@Getter
@AllArgsConstructor
public enum EquipmentEnum {


    ;
    /**
     * ID
     */
    private final Long id;
    /**
     * 名称
     */
    private final String name;
    /**
     * 描述
     */
    private final String desc;
    /**
     * 装备类型
     */
    private final EquipmentTypeEnum equipmentTypeEnum;
    /**
     * 属性
     */
    private final OrganismEnum equipmentPropertiesDTO;
}
