package com.dingCreator.astrology.util.template;

import com.dingCreator.astrology.dto.BattleDTO;
import com.dingCreator.astrology.dto.skill.SkillEffectDTO;
import com.dingCreator.astrology.enums.skill.SkillEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author ding
 * @date 2024/4/3
 */
@Data
public abstract class ExtraBattleProcessTemplate implements Serializable {
    /**
     * 插入结算的来源
     */
    private BattleDTO from;
    /**
     * 插入结算的友方
     */
    private List<BattleDTO> our;
    /**
     * 插入结算的敌方
     */
    private List<BattleDTO> enemy;
    /**
     * 优先级
     */
    private Integer priority;
    /**
     * 此插入结算的冷却CD
     */
    private Integer cd;

    public ExtraBattleProcessTemplate() {
        this.priority = 0;
        this.cd = 0;
    }

    /**
     * 战斗前
     */
    public void beforeBattle(List<String> battleMsg) {

    }

    /**
     * 战斗后
     */
    public void afterBattle(List<String> battleMsg) {

    }

    /**
     * 每轮前
     */
    public void beforeEachRound(List<String> battleMsg) {

    }

    /**
     * 每轮后
     */
    public void afterEachRound(List<String> battleMsg) {

    }

    /**
     * 友方轮次前
     */
    public void beforeOurRound(List<String> battleMsg) {

    }

    /**
     * 友方轮次后
     */
    public void afterOurRound(List<String> battleMsg) {

    }

    /**
     * 友方轮次前
     */
    public void beforeEnemyRound(List<String> battleMsg) {

    }

    /**
     * 友方轮次后
     */
    public void afterEnemyRound(List<String> battleMsg) {

    }

    /**
     * 我的轮次前
     */
    public void beforeMyRound(List<String> battleMsg) {

    }

    /**
     * 我的轮次后
     */
    public void afterMyRound(List<String> battleMsg) {

    }

    public boolean canEffect(BattleDTO tar, SkillEffectDTO skillEffect, StringBuilder builder){
        return true;
    }

    /**
     * 我的行动前
     */
    public void beforeMyBehavior(BattleDTO tar, StringBuilder builder) {

    }

    /**
     * 我的行动后
     */
    public void afterMyBehavior(BattleDTO tar, StringBuilder builder) {

    }

    /**
     * 技能命中时
     */
    public void ifHit(BattleDTO from, BattleDTO tar,
                      SkillEnum skillEnum, SkillEffectDTO skillEffect,
                      AtomicLong damage, boolean critical, StringBuilder builder) {
        if (from.equals(this.from)) {
            processIfHit(tar, skillEnum, skillEffect, damage, critical, builder);
        }
        if (this.our.contains(from)) {
            processIfOurHit(tar, skillEnum, skillEffect, damage, critical, builder);
        }
    }

    /**
     * 我方技能命中时
     *
     * @param tar         目标
     * @param skillEnum   技能
     * @param skillEffect 技能效果
     * @param damage      伤害量
     * @param critical    是否暴击
     * @param builder     文描
     */
    public void processIfHit(BattleDTO tar, SkillEnum skillEnum, SkillEffectDTO skillEffect,
                             AtomicLong damage, boolean critical, StringBuilder builder) {
        if (this.getEnemy().contains(tar)) {
            processIfHitEnemy(tar, skillEnum, skillEffect, damage, critical, builder);
        }
    }

    /**
     * 我方技能命中敌方时
     *
     * @param tar         目标
     * @param skillEnum   技能
     * @param skillEffect 技能效果
     * @param damage      伤害量
     * @param critical    是否暴击
     * @param builder     文描
     */
    public void processIfHitEnemy(BattleDTO tar, SkillEnum skillEnum, SkillEffectDTO skillEffect,
                                  AtomicLong damage, boolean critical, StringBuilder builder) {

    }

    public void processIfOurHit(BattleDTO tar, SkillEnum skillEnum, SkillEffectDTO skillEffect,
                                AtomicLong damage, boolean critical, StringBuilder builder) {

    }

    public void processIfOurHitEnemy(BattleDTO tar, SkillEnum skillEnum, AtomicLong damage, boolean critical, StringBuilder builder) {

    }

    /**
     * 技能没有命中时
     */
    public void ifNotHit(BattleDTO from, BattleDTO tar, SkillEnum skillEnum, StringBuilder builder) {
        if (from.equals(this.from)) {
            processIfNotHit(tar, skillEnum, builder);
        }
    }

    public void processIfNotHit(BattleDTO tar, SkillEnum skillEnum, StringBuilder builder) {

    }

    public final BigDecimal changeDamageRate(BattleDTO from, BattleDTO tar, BigDecimal damageRate, SkillEnum skillEnum, StringBuilder builder) {
        if (from.equals(this.getFrom())) {
            return processChangeMySkillDamageRate(tar, damageRate, skillEnum, builder);
        }
        return damageRate;
    }

    public BigDecimal processChangeMySkillDamageRate(BattleDTO tar, BigDecimal damageRate, SkillEnum skillEnum, StringBuilder builder) {
        return damageRate;
    }

    /**
     * 目标阵亡前（即受到致命攻击时）
     */
    public void beforeTargetDeath(BattleDTO from, BattleDTO tar,
                                  AtomicLong atoDamage, StringBuilder builder) {

    }

    /**
     * 目标阵亡后
     */
    public void afterTargetDeath(BattleDTO from, BattleDTO tar,
                                 AtomicLong atoDamage, StringBuilder builder) {

    }

    /**
     * 自身阵亡前（即受到致命攻击时）
     */
    public void beforeMeDeath(BattleDTO from, BattleDTO tar,
                              AtomicLong atoDamage, StringBuilder builder) {

    }

    /**
     * 自身阵亡后
     */
    public void afterMeDeath(BattleDTO from, BattleDTO tar,
                             AtomicLong atoDamage, StringBuilder builder) {

    }

    /**
     * 受到伤害前
     */
    public void beforeDamage(StringBuilder builder) {

    }

    /**
     * 受到伤害后
     */
    public void afterDamage(BattleDTO tar, AtomicLong atoDamage, StringBuilder builder) {

    }

    public void beforeHealing() {

    }

    public void afterHealing(long healVal) {

    }

    /**
     * 被其他效果触发
     */
    public void execute() {

    }
}
