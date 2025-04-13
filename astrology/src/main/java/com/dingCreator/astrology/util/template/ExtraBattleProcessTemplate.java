package com.dingCreator.astrology.util.template;

import com.dingCreator.astrology.dto.battle.BattleDTO;
import com.dingCreator.astrology.dto.battle.BattleEffectDTO;
import com.dingCreator.astrology.dto.battle.BattleFieldDTO;
import com.dingCreator.astrology.dto.battle.BattleRoundDTO;
import com.dingCreator.astrology.dto.skill.SkillEffectDTO;
import com.dingCreator.astrology.enums.skill.SkillEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 插入结算的模板
 * 提供大量钩子方法，重写对应的钩子方法即可在战斗中生效
 *
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
    public void beforeBattle(BattleFieldDTO battleField) {

    }

    /**
     * 战斗后
     */
    public void afterBattle(BattleFieldDTO battleField) {

    }

    /**
     * 每轮前
     */
    public void beforeEachRound(BattleRoundDTO battleRound) {

    }

    /**
     * 每轮后
     */
    public void afterEachRound(BattleRoundDTO battleRound) {

    }

    /**
     * 友方轮次前
     */
    public void beforeOurRound(BattleRoundDTO battleRound) {

    }

    /**
     * 友方轮次后
     */
    public void afterOurRound(BattleRoundDTO battleRound) {

    }

    /**
     * 友方轮次前
     */
    public void beforeEnemyRound(BattleRoundDTO battleRound) {

    }

    /**
     * 友方轮次后
     */
    public void afterEnemyRound(BattleRoundDTO battleRound) {

    }

    /**
     * 我的轮次前
     */
    public void beforeMyRound(BattleRoundDTO battleRound) {

    }

    /**
     * 我的轮次后
     */
    public void afterMyRound(BattleRoundDTO battleRound) {

    }

    public boolean canEffect(BattleEffectDTO battleEffect) {
        return true;
    }

    /**
     * 我的行动前
     */
    public void beforeMyBehavior(BattleEffectDTO battleEffect) {

    }

    /**
     * 我的行动后
     */
    public void afterMyBehavior(BattleEffectDTO battleEffect) {

    }

    /**
     * 技能命中时
     */
    public void ifHit(BattleEffectDTO battleEffect) {
        if (battleEffect.getFrom().equals(this.from)) {
            processIfHit(battleEffect);
        }
        if (this.our.contains(battleEffect.getFrom())) {
            processIfOurHit(battleEffect);
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
    public void processIfHit(BattleEffectDTO battleEffect) {
        if (this.getEnemy().contains(battleEffect.getTar())) {
            processIfHitEnemy(battleEffect);
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
    public void processIfHitEnemy(BattleEffectDTO battleEffect) {

    }

    public void processIfOurHit(BattleEffectDTO battleEffect) {

    }

    public void processIfOurHitEnemy(BattleEffectDTO battleEffect, boolean critical) {

    }

    /**
     * 技能没有命中时
     */
    public void ifNotHit(BattleEffectDTO battleEffect) {
        if (battleEffect.getFrom().equals(this.from)) {
            processIfNotHit(battleEffect);
        }
    }

    public void processIfNotHit(BattleEffectDTO battleEffect) {

    }

    public final void changeDamageRate(BattleEffectDTO battleEffect) {
        if (battleEffect.getFrom().equals(this.getFrom())) {
            processChangeMySkillDamageRate(battleEffect);
        }
    }

    public void processChangeMySkillDamageRate(BattleEffectDTO battleEffect) {

    }

    /**
     * 目标阵亡前（即受到致命攻击时）
     */
    public void beforeTargetDeath(BattleEffectDTO battleEffect) {

    }

    /**
     * 目标阵亡后
     */
    public void afterTargetDeath(BattleEffectDTO battleEffect) {

    }

    /**
     * 自身阵亡前（即受到致命攻击时）
     */
    public void beforeMeDeath(BattleEffectDTO battleEffect) {

    }

    /**
     * 自身阵亡后
     */
    public void afterMeDeath(BattleEffectDTO battleEffect) {

    }

    /**
     * 受到伤害前
     */
    public void beforeDamage(BattleEffectDTO battleEffect) {

    }

    /**
     * 受到伤害后
     *
     * @param tar 受到伤害者
     */
    public void afterDamage(BattleEffectDTO battleEffect) {

    }

    public void beforeHealing() {

    }

    public void afterHealing(long healVal) {

    }
}
