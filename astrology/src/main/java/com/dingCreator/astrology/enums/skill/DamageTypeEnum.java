package com.dingCreator.astrology.enums.skill;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author ding
 * @date 2024/2/4
 */
@Getter
@AllArgsConstructor
public enum DamageTypeEnum {
    /**
     * 物理攻击
     */
    ATK("ATK", "物理"),
    /**
     * 法术攻击
     */
    MAGIC("MAGIC", "法术"),
    /**
     * 特殊攻击
     */
    SPECIAL("SPECIAL", "特殊"),
    /**
     * 真实伤害
     */
    REAL("REAL", "真实"),
    ;

    private final String typeName;

    private final String typeChnDesc;
}
