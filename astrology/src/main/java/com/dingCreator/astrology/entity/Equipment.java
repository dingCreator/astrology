package com.dingCreator.astrology.entity;

import lombok.Data;

/**
 * 装备
 *
 * @author ding
 * @date 2024/1/29
 */
@Data
public class Equipment {
    /**
     * 主键ID
     */
    private Long id;
    /**
     * 装备类型
     */
    private String equipmentType;
    /**
     * 装备归属
     */
    private String belongTo;
    /**
     * 装备所属ID
     */
    private Long belongToId;
}
