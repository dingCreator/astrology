package com.dingCreator.astrology.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author ding
 * @date 2024/12/27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerInfoVO {
    /**
     * 玩家ID
     */
    private Long id;
    /**
     * 经验值
     */
    private Long exp;
    /**
     * 当前等级最大经验值
     */
    private Long currentLevelMaxExp;
    /**
     * 职业
     */
    private String job;
    /**
     * 所处位置
     */
    private Long mapId;
    /**
     * 状态
     */
    private String status;

    /**
     * 名称
     */
    protected String name;
    /**
     * 血量
     */
    protected Long hp;
    /**
     * 血量上限
     */
    protected Long maxHp;
    /**
     * 蓝
     */
    protected Long mp;
    /**
     * 蓝上限
     */
    protected Long maxMp;
    /**
     * 攻击力
     */
    protected Long atk;
    /**
     * 法强
     */
    protected Long magicAtk;
    /**
     * 防御
     */
    protected Long def;
    /**
     * 法抗
     */
    protected Long magicDef;
    /**
     * 穿透
     */
    protected Float penetrate;
    /**
     * 法穿
     */
    protected Float magicPenetrate;
    /**
     * 暴击率
     */
    protected Float criticalRate;
    /**
     * 抗暴率
     */
    protected Float criticalReductionRate;
    /**
     * 爆伤倍率
     */
    protected Float criticalDamage;
    /**
     * 爆伤减免
     */
    protected Float criticalDamageReduction;
    /**
     * 行动速度
     */
    protected Long behaviorSpeed;
    /**
     * 命中率
     */
    protected Long hit;
    /**
     * 闪避率
     */
    protected Long dodge;
    /**
     * 吸血
     */
    protected Float lifeStealing;
    /**
     * 阶段
     */
    protected Integer rank;
    /**
     * 等级
     */
    protected Integer level;
}
