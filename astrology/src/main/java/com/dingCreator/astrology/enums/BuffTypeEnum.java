package com.dingCreator.astrology.enums;

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
     * 物攻
     */
    ATK("atk", "攻击"),
    /**
     * 物防
     */
    DEF("def", "防御"),
    /**
     * 魔攻
     */
    MAGIC_ATK("magicAtk", "法强"),
    /**
     * 魔防
     */
    MAGIC_DEF("magicDef", "法抗"),
    /**
     * 速度
     */
    SPEED("speed", "速度"),
    /**
     * 暂停
     */
    PAUSE("pause", ""),
    /**
     * 嘲讽
     */
    TAUNT("taunt", "嘲讽"),
    /**
     * 治疗
     */
    HEAL("heal", ""),
    /**
     * 生命值偷取（吸血）
     */
    LIFE_STEAL("lifeSteal", "吸血"),
    /**
     * 流血
     */
    BLEEDING("bleeding", "流血"),
    /**
     * 命中
     */
    HIT("hit", "命中"),
    /**
     * 闪避
     */
    DODGE("dodge", "闪避"),
    /**
     * 暴击率
     */
    CRITICAL("critical", "暴击"),
    /**
     * 暴击率减免
     */
    CRITICAL_REDUCTION("critical_reduction", "抗暴"),
    /**
     * 爆伤
     */
    CRITICAL_DAMAGE("critical_damage", "爆伤"),
    /**
     * 爆伤减免
     */
    CRITICAL_DAMAGE_REDUCTION("critical_damage_reduction", "爆免"),
    /**
     * 伤害
     */
    DAMAGE("damage", "伤害"),
    /**
     * 法伤
     */
    MAGIC_DAMAGE("magic_damage", "法伤"),
    /**
     * 反伤
     */
    REFLECT_DAMAGE("reflect_damage", "反伤"),
    ;

    /**
     * buff名称
     */
    private final String name;
    /**
     * 中文描述
     */
    private final String chnDesc;
}
