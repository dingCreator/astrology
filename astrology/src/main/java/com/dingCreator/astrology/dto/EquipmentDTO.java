package com.dingCreator.astrology.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ding
 * @date 2024/4/9
 */
@Data
public class EquipmentDTO implements Serializable {
    /**
     * 装备ID
     */
    private Long equipmentId;
    /**
     * 强化等级
     */
    private Integer level;
}
