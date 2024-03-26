package com.dingCreator.astrology.enums.equipment;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author ding
 * @date 2024/3/24
 */
@Getter
@AllArgsConstructor
public enum EquipmentTypeEnum {
    /**
     * 武器
     */
    WEAPON("weapon"),
    /**
     * 防具
     */
    ARMOR("armor"),
    /**
     * 饰品
     */
    JEWELRY("jewelry"),
    ;
    private final String type;
}
