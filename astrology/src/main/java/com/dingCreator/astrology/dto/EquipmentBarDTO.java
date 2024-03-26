package com.dingCreator.astrology.dto;

import com.dingCreator.astrology.entity.EquipmentBelongTo;
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
    private EquipmentBelongTo weapon;
    /**
     * 防具
     */
    private EquipmentBelongTo armor;
    /**
     * 饰品
     */
    private EquipmentBelongTo jewelry;
}
