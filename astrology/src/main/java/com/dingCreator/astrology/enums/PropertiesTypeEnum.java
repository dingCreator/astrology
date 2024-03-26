package com.dingCreator.astrology.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author ding
 * @date 2024/3/24
 */
@Getter
@AllArgsConstructor
public enum PropertiesTypeEnum {
    /**
     * 血量
     */
    HP("hp"),
    /**
     * 蓝量
     */
    MP("mp"),
    /**
     * 物攻
     */
    ATK("atk"),
    /**
     * 魔攻
     */
    MAGIC_ATK("magicAtk"),
    /**
     * 防御力
     */
    DEF("def"),
    /**
     * 魔抗
     */
    MAGIC_DEF("magicDef"),
    /**
     * 穿透
     */
    PENETRATE("penetrate"),
    /**
     * 暴击率
     */
    CRITICAL_RATE("criticalRate"),
    /**
     * 抗暴率
     */
    CRITICAL_REDUCTION_RATE("criticalReductionRate"),
    /**
     * 爆伤倍率
     */
    CRITICAL_DAMAGE("criticalDamage"),
    /**
     * 爆伤减免
     */
    CRITICAL_DAMAGE_REDUCTION("criticalDamageReduction"),
    /**
     * 行动速度
     */
    BEHAVIOR_SPEED("behaviorSpeed"),
    /**
     * 命中率
     */
    HIT("hit"),
    /**
     * 闪避率
     */
    DODGE("dodge"),
    ;
    private final String name;
}
