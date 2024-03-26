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
    ATK("ATK"),
    /**
     * 法术攻击
     */
    MAGIC("MAGIC"),
    /**
     * 特殊攻击
     */
    SPECIAL("SPECIAL");
    ;

    private final String typeName;
}
