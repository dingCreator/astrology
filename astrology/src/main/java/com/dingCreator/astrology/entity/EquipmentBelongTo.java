package com.dingCreator.astrology.entity;

import lombok.Data;

/**
 * 装备
 *
 * @author ding
 * @date 2024/1/29
 */
@Data
public class EquipmentBelongTo {
    /**
     * 主键ID
     */
    private Long id;
    /**
     * 装备归属
     */
    private String belongTo;
    /**
     * 装备所属ID
     */
    private Long belongToId;
    /**
     * 装备ID
     */
    private Long equipmentId;
    /**
     * 强化等级
     */
    private Integer level;
    /**
     * 是否装备中
     */
    private Boolean equip;
}
