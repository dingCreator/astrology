package com.dingCreator.astrology.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 生效条件
 *
 * @author ding
 * @date 2024/2/4
 */
@Getter
@AllArgsConstructor
public enum SkillConditionEnum {
    /**
     * 无需条件
     */
    NONE("NONE"),

    /**
     * 物攻更高
     */
    ATK_GT("ATK_GT"),
    /**
     * 物攻等于
     */
    ATK_EQ("ATK_EQ"),
    /**
     * 物攻更低
     */
    ATK_LT("ATK_LT"),

    /**
     * 物防更高
     */
    DEF_GT("DEF_GT"),
    /**
     * 物防等于
     */
    DEF_EQ("DEF_EQ"),
    /**
     * 物防更低
     */
    DEF_LT("DEF_LT"),

    ;
    private final String condition;
}
