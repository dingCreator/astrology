package com.dingCreator.astrology.template;

import com.dingCreator.astrology.dto.BattleDTO;
import com.dingCreator.astrology.enums.skill.SkillEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author ding
 * @date 2024/4/3
 */
@Data
public abstract class ExtraBattleProcessTemplate {
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
     * 我的轮次前
     */
    public void beforeMyRound(List<String> battleMsg) {

    }

    /**
     * 我的轮次后
     */
    public void afterMyRound(List<String> battleMsg) {

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
    public final void ifHit(BattleDTO from, BattleDTO tar, SkillEnum skillEnum, AtomicLong damage, StringBuilder builder) {
        if (from.equals(this.from)) {
            processIfHit(tar, skillEnum, damage, builder);
        }
        if (this.our.contains(from)) {
            processIfOurHit(tar, skillEnum, damage, builder);
        }
    }

    public void processIfHit(BattleDTO tar, SkillEnum skillEnum, AtomicLong damage, StringBuilder builder) {

    }

    public void processIfOurHit(BattleDTO tar, SkillEnum skillEnum, AtomicLong damage, StringBuilder builder) {

    }

    /**
     * 技能没有命中时
     */
    public final void ifNotHit(BattleDTO from, BattleDTO tar, SkillEnum skillEnum, StringBuilder builder) {
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
    public void beforeTargetDeath(StringBuilder builder) {

    }

    /**
     * 目标阵亡后
     */
    public void afterTargetDeath(StringBuilder builder) {

    }

    /**
     * 受到伤害前
     */
    public void beforeDamage(StringBuilder builder) {

    }

    /**
     * 受到伤害后
     */
    public void afterDamage(StringBuilder builder) {

    }

    /**
     * 被其他效果触发
     */
    public void execute() {

    }
}
