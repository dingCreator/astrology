package com.dingCreator.astrology.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ding
 * @date 2024/4/16
 */
@Data
public class EquipmentVO implements Serializable {
    /**
     * 装备编号ID
     */
    private Long id;
    /**
     * 装备名称
     */
    private String equipmentName;
    /**
     * 装备属性
     */
    private String equipmentProperty;
    /**
     * 等级
     */
    private Integer level;
    /**
     * 是否已装备
     */
    private String equip;
}
