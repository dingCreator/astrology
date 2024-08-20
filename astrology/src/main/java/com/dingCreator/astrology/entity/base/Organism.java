package com.dingCreator.astrology.entity.base;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

/**
 * 生物
 *
 * @author ding
 * @date 2023/4/18
 */
@Data
public class Organism {
    /**
     * 名称
     */
    @TableField("`name`")
    protected String name;
    /**
     * 血量
     */
    @TableField("hp")
    protected Long hp;
    /**
     * 血量上限
     */
    @TableField("max_hp")
    protected Long maxHp;
    /**
     * 蓝
     */
    @TableField("mp")
    protected Long mp;
    /**
     * 蓝上限
     */
    @TableField("max_mp")
    protected Long maxMp;
    /**
     * 攻击力
     */
    @TableField("atk")
    protected Long atk;
    /**
     * 法强
     */
    @TableField("magic_atk")
    protected Long magicAtk;
    /**
     * 防御
     */
    @TableField("def")
    protected Long def;
    /**
     * 法抗
     */
    @TableField("magic_def")
    protected Long magicDef;
    /**
     * 穿透
     */
    @TableField("penetrate")
    protected Float penetrate;
    /**
     * 法穿
     */
    @TableField("magic_penetrate")
    protected Float magicPenetrate;
    /**
     * 暴击率
     */
    @TableField("critical_rate")
    protected Float criticalRate;
    /**
     * 抗暴率
     */
    @TableField("critical_reduction_rate")
    protected Float criticalReductionRate;
    /**
     * 爆伤倍率
     */
    @TableField("critical_damage")
    protected Float criticalDamage;
    /**
     * 爆伤减免
     */
    @TableField("critical_damage_reduction")
    protected Float criticalDamageReduction;
    /**
     * 行动速度
     */
    @TableField("behavior_speed")
    protected Long behaviorSpeed;
    /**
     * 命中率
     */
    @TableField("hit")
    protected Long hit;
    /**
     * 闪避率
     */
    @TableField("dodge")
    protected Long dodge;
    /**
     * 吸血
     */
    @TableField("life_stealing")
    protected Float lifeStealing;
    /**
     * 阶段
     */
    @TableField("`rank`")
    protected Integer rank;
    /**
     * 等级
     */
    @TableField("`level`")
    protected Integer level;
}
