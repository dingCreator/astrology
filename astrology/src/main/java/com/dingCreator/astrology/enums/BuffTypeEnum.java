package com.dingCreator.astrology.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

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
    ATK(OrganismPropertiesEnum.ATK.getFieldName(), OrganismPropertiesEnum.ATK.getChnDesc()),
    /**
     * 物防
     */
    DEF(OrganismPropertiesEnum.DEF.getFieldName(), OrganismPropertiesEnum.DEF.getChnDesc()),
    /**
     * 法强
     */
    MAGIC_ATK(OrganismPropertiesEnum.MAGIC_ATK.getFieldName(), OrganismPropertiesEnum.MAGIC_ATK.getChnDesc()),
    /**
     * 法抗
     */
    MAGIC_DEF(OrganismPropertiesEnum.MAGIC_DEF.getFieldName(), OrganismPropertiesEnum.MAGIC_DEF.getChnDesc()),
    /**
     * 速度
     */
    SPEED(OrganismPropertiesEnum.BEHAVIOR_SPEED.getFieldName(), OrganismPropertiesEnum.BEHAVIOR_SPEED.getChnDesc()),
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
    LIFE_STEAL(OrganismPropertiesEnum.LIFE_STEALING.getFieldName(), OrganismPropertiesEnum.LIFE_STEALING.getChnDesc()),
    /**
     * 流血
     */
    BLEEDING("bleeding", "流血"),
    /**
     * 命中
     */
    HIT(OrganismPropertiesEnum.HIT.getFieldName(), OrganismPropertiesEnum.HIT.getChnDesc()),
    /**
     * 闪避
     */
    DODGE(OrganismPropertiesEnum.DODGE.getFieldName(), OrganismPropertiesEnum.DODGE.getChnDesc()),
    /**
     * 暴击率
     */
    CRITICAL(OrganismPropertiesEnum.CRITICAL_RATE.getFieldName(), OrganismPropertiesEnum.CRITICAL_DAMAGE.getChnDesc()),
    /**
     * 暴击率减免
     */
    CRITICAL_REDUCTION(OrganismPropertiesEnum.CRITICAL_REDUCTION_RATE.getFieldName(),
            OrganismPropertiesEnum.CRITICAL_REDUCTION_RATE.getChnDesc()),
    /**
     * 爆伤
     */
    CRITICAL_DAMAGE(OrganismPropertiesEnum.CRITICAL_DAMAGE.getFieldName(),
            OrganismPropertiesEnum.CRITICAL_DAMAGE.getChnDesc()),
    /**
     * 爆伤减免
     */
    CRITICAL_DAMAGE_REDUCTION(OrganismPropertiesEnum.CRITICAL_DAMAGE_REDUCTION.getFieldName(),
            OrganismPropertiesEnum.CRITICAL_DAMAGE_REDUCTION.getChnDesc()),
    /**
     * 伤害
     */
    DAMAGE("damage", "受到的伤害"),
    /**
     * 法伤
     */
    MAGIC_DAMAGE("magicDamage", "受到的法伤"),
    /**
     * 反伤
     */
    REFLECT_DAMAGE("reflectDamage", "反伤"),
    /**
     * 穿甲
     */
    PENETRATE(OrganismPropertiesEnum.PENETRATE.getFieldName(), OrganismPropertiesEnum.PENETRATE.getChnDesc()),
    /**
     * 法穿
     */
    MAGIC_PENETRATE(OrganismPropertiesEnum.MAGIC_PENETRATE.getFieldName(),
            OrganismPropertiesEnum.MAGIC_PENETRATE.getChnDesc()),
    /**
     * 免疫
     */
    IMMUNITY("immunity", "免疫"),
    ;

    /**
     * buff名称
     */
    private final String name;
    /**
     * 中文描述
     */
    private final String chnDesc;

    public static BuffTypeEnum getByName(String name) {
        return Arrays.stream(values()).filter(e -> e.getName().equals(name)).findFirst().orElse(null);
    }
}
