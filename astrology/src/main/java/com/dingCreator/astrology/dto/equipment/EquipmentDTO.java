package com.dingCreator.astrology.dto.equipment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author ding
 * @date 2024/4/9
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentDTO implements Serializable {
    /**
     * ID，方便从数据库查询对应数据
     */
    private Long id;
    /**
     * 装备ID
     */
    private Long equipmentId;
    /**
     * 强化等级
     */
    private Integer level;
}
