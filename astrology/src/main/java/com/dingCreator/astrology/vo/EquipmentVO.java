package com.dingCreator.astrology.vo;

import com.dingCreator.astrology.enums.equipment.EquipmentRankEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

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
     * 装备品级
     */
    private EquipmentRankEnum rank;
    /**
     * 装备名称
     */
    private String equipmentName;
    /**
     * 限制职业
     */
    private List<String> limitJob;
    /**
     * 限制等级
     */
    private Integer limitLevel;
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
    private Boolean equip;
}
