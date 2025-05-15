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
    ATK(OrganismPropertiesEnum.ATK.getFieldName(), OrganismPropertiesEnum.ATK.getChnDesc(), BuffEffectTypeEnum.PROPERTY),
    /**
     * 物防
     */
    DEF(OrganismPropertiesEnum.DEF.getFieldName(), OrganismPropertiesEnum.DEF.getChnDesc(), BuffEffectTypeEnum.PROPERTY),
    /**
     * 法强
     */
    MAGIC_ATK(OrganismPropertiesEnum.MAGIC_ATK.getFieldName(), OrganismPropertiesEnum.MAGIC_ATK.getChnDesc(),
            BuffEffectTypeEnum.PROPERTY),
    /**
     * 法抗
     */
    MAGIC_DEF(OrganismPropertiesEnum.MAGIC_DEF.getFieldName(), OrganismPropertiesEnum.MAGIC_DEF.getChnDesc(),
            BuffEffectTypeEnum.PROPERTY),
    /**
     * 速度
     */
    SPEED(OrganismPropertiesEnum.BEHAVIOR_SPEED.getFieldName(), OrganismPropertiesEnum.BEHAVIOR_SPEED.getChnDesc(),
            BuffEffectTypeEnum.PROPERTY),
    /**
     * 生命值偷取（吸血）
     */
    LIFE_STEAL(OrganismPropertiesEnum.LIFE_STEAL.getFieldName(), OrganismPropertiesEnum.LIFE_STEAL.getChnDesc(),
            BuffEffectTypeEnum.PROPERTY),
    /**
     * 命中
     */
    HIT(OrganismPropertiesEnum.HIT.getFieldName(), OrganismPropertiesEnum.HIT.getChnDesc(), BuffEffectTypeEnum.PROPERTY),
    /**
     * 闪避
     */
    DODGE(OrganismPropertiesEnum.DODGE.getFieldName(), OrganismPropertiesEnum.DODGE.getChnDesc(), BuffEffectTypeEnum.PROPERTY),
    /**
     * 暴击率
     */
    CRITICAL(OrganismPropertiesEnum.CRITICAL_RATE.getFieldName(), OrganismPropertiesEnum.CRITICAL_DAMAGE.getChnDesc(),
            BuffEffectTypeEnum.PROPERTY),
    /**
     * 暴击率减免
     */
    CRITICAL_REDUCTION(OrganismPropertiesEnum.CRITICAL_REDUCTION_RATE.getFieldName(),
            OrganismPropertiesEnum.CRITICAL_REDUCTION_RATE.getChnDesc(), BuffEffectTypeEnum.PROPERTY),
    /**
     * 爆伤
     */
    CRITICAL_DAMAGE(OrganismPropertiesEnum.CRITICAL_DAMAGE.getFieldName(),
            OrganismPropertiesEnum.CRITICAL_DAMAGE.getChnDesc(), BuffEffectTypeEnum.PROPERTY),
    /**
     * 爆伤减免
     */
    CRITICAL_DAMAGE_REDUCTION(OrganismPropertiesEnum.CRITICAL_DAMAGE_REDUCTION.getFieldName(),
            OrganismPropertiesEnum.CRITICAL_DAMAGE_REDUCTION.getChnDesc(), BuffEffectTypeEnum.PROPERTY),
    /**
     * 穿甲
     */
    PENETRATE(OrganismPropertiesEnum.PENETRATE.getFieldName(), OrganismPropertiesEnum.PENETRATE.getChnDesc(),
            BuffEffectTypeEnum.PROPERTY),
    /**
     * 法穿
     */
    MAGIC_PENETRATE(OrganismPropertiesEnum.MAGIC_PENETRATE.getFieldName(),
            OrganismPropertiesEnum.MAGIC_PENETRATE.getChnDesc(), BuffEffectTypeEnum.PROPERTY),
    /**
     * 免疫
     */
    IMMUNITY("immunity", "免疫", BuffEffectTypeEnum.MARK),
    /**
     * 暂停
     */
    PAUSE("pause", "暂停", BuffEffectTypeEnum.MARK),
    /**
     * 嘲讽
     */
    TAUNT("taunt", "嘲讽", BuffEffectTypeEnum.MARK),
    /**
     * 治疗量变更
     */
    HEAL("heal", "治疗量", BuffEffectTypeEnum.PROPERTY),
    /**
     * 流血
     */
    BLEEDING("bleeding", "流血", BuffEffectTypeEnum.MARK),
    /**
     * 无敌
     */
    INVINCIBLE("invincible", "无敌", BuffEffectTypeEnum.MARK),
    /**
     * 护盾
     */
    SHIELD("shield", "护盾", BuffEffectTypeEnum.MARK),
    /**
     * 伤害
     */
    DAMAGE("damage", "受到伤害", BuffEffectTypeEnum.PROPERTY),
    /**
     * 伤害
     */
    DO_DAMAGE("doDamage", "造成伤害", BuffEffectTypeEnum.PROPERTY),
    /**
     * 法伤
     */
    MAGIC_DAMAGE("magicDamage", "受到法伤", BuffEffectTypeEnum.PROPERTY),
    /**
     * 法伤
     */
    DO_MAGIC_DAMAGE("doMagicDamage", "造成法伤", BuffEffectTypeEnum.PROPERTY),
    /**
     * 反伤
     */
    REFLECT_DAMAGE("reflectDamage", "反伤", BuffEffectTypeEnum.PROPERTY),
    ;

    /**
     * buff名称
     */
    private final String name;
    /**
     * 中文描述
     */
    private final String chnDesc;
    /**
     * buff效果类型
     */
    private final BuffEffectTypeEnum buffEffectTypeEnum;

    public static BuffTypeEnum getByName(String name) {
        return Arrays.stream(values()).filter(e -> e.getName().equals(name)).findFirst().orElse(null);
    }

    public enum BuffEffectTypeEnum {
        /**
         * 改变属性类buff，效果为改变属性值
         */
        PROPERTY,
        /**
         * 标记类buff，效果为拥有该标记会触发特殊效果
         */
        MARK,
    }
}
