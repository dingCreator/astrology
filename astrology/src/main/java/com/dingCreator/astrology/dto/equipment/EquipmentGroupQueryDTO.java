package com.dingCreator.astrology.dto.equipment;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ding
 * @date 2024/4/24
 */
@Data
public class EquipmentGroupQueryDTO implements Serializable {
    /**
     * 装备ID
     */
    private Long equipmentId;
    /**
     * 装备数量
     */
    private Integer equipmentCount;
}
