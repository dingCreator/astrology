package com.dingCreator.immortal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author ding
 * @date 2024/2/2
 */
@Getter
@AllArgsConstructor
public enum BuffTypeEnum {
    /**
     * 物攻数值
     */
    ATK("atk"),
    /**
     * 物攻比例
     */
    ATK_RATE("atkRate"),
    /**
     * 物防数值
     */
    DEF("def"),
    /**
     * 物防比例
     */
    DEF_RATE("defRate"),
    /**
     * 魔攻数值
     */
    MAGIC_ATK("magicAtk"),
    /**
     * 魔攻比例
     */
    MAGIC_ATK_RATE("magicAtkRate"),
    /**
     * 魔防数值
     */
    MAGIC_DEF("magicDef"),
    /**
     * 魔防比例
     */
    MAGIC_DEF_RATE("magicDefRate"),
    /**
     * 速度数值
     */
    SPEED("speed"),
    /**
     * 速度比例
     */
    SPEED_RATE("speedRate"),
    /**
     * 暂停
     */
    PAUSE("pause"),
    /**
     * 嘲讽
     */
    TAUNT("taunt"),
    /**
     * 治疗数值
     */
    HEAL("heal"),
    /**
     * 治疗比例
     */
    HEAL_RATE("healRate"),
    /**
     * 生命值偷取（吸血）
     */
    LIFE_STEAL("lifeSteal"),
    /**
     * 流血
     */
    BLEEDING("bleeding"),
    /**
     * 命中
     */
    HIT("hit"),
    /**
     * 命中比例
     */
    HIT_RATE("hitRate"),
    /**
     * 闪避
     */
    DODGE("dodge"),
    /**
     * 闪避比例
     */
    DODGE_RATE("dodgeRate"),
    ;

    private final String name;
}
