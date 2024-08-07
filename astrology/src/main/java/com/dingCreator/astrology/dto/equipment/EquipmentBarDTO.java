package com.dingCreator.astrology.dto.equipment;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ding
 * @date 2024/3/19
 */
@Data
public class EquipmentBarDTO implements Serializable {
    /**
     * 武器
     */
    private EquipmentDTO weapon;
    /**
     * 防具
     */
    private EquipmentDTO armor;
    /**
     * 饰品
     */
    private EquipmentDTO jewelry;
}
