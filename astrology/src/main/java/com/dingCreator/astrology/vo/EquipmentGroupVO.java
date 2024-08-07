package com.dingCreator.astrology.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ding
 * @date 2024/4/24
 */
@Data
public class EquipmentGroupVO implements Serializable {
    /**
     * 装备名称
     */
    private String equipmentName;
    /**
     * 装备数量
     */
    private Integer count;
    /**
     * 装备描述
     */
    private String desc;
}
