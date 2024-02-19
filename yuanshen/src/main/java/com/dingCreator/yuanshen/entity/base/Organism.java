package com.dingCreator.yuanshen.entity.base;

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
    private String name;
    /**
     * 血量
     */
    private Long hp;
    /**
     * 血量上限
     */
    private Long maxHp;
    /**
     * 蓝
     */
    private Long mp;
    /**
     * 蓝上限
     */
    private Long maxMp;
    /**
     * 攻击力
     */
    private Long atk;
    /**
     * 魔攻
     */
    private Long magicAtk;
    /**
     * 防御力
     */
    private Long def;
    /**
     * 魔抗
     */
    private Long magicDef;
    /**
     * 穿透
     */
    private Float penetrate;
    /**
     * 暴击
     */
    private Long critical;
    /**
     * 行动速度
     */
    private Long behaviorSpeed;
    /**
     * 命中率
     */
    private Long hit;
    /**
     * 闪避率
     */
    private Long dodge;
    /**
     * 阶段
     */
    private Integer rank;
    /**
     * 等级
     */
    private Integer level;
}
