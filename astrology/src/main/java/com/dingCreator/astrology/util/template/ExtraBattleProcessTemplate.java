package com.dingCreator.astrology.util.template;

import com.dingCreator.astrology.dto.battle.BattleDTO;
import com.dingCreator.astrology.dto.battle.BattleEffectDTO;
import com.dingCreator.astrology.dto.battle.BattleFieldDTO;
import com.dingCreator.astrology.dto.battle.BattleRoundDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

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
     * 插入结算的归属者
     */
    private BattleDTO owner;

    /**
     * 归属者队友
     */
    private List<BattleDTO> ownerOur;

    /**
     * 归属者敌人
     */
    private List<BattleDTO> ownerEnemy;

    /**
     * 优先级
     */
    protected Integer priority;

    /**
     * 此效果拥有者阵亡后是否生效
     */
    private Boolean effectIfDeath;

    /**
     * 此插入结算的冷却CD
     */
    private Integer cd;

    public ExtraBattleProcessTemplate() {
        this.priority = 0;
        this.cd = 0;
        this.effectIfDeath = false;
    }

    private boolean effect() {
        return effectIfDeath || owner.getOrganismInfoDTO().getOrganismDTO().getHpWithAddition() > 0;
    }

    /**
     * 战斗开始前
     *
     * @param battleField 战场
     */
    public final void executeBeforeBattle(BattleFieldDTO battleField) {
        if (effect()) {
            beforeBattle(battleField);
        }
    }

    /**
     * 战斗结束后
     *
     * @param battleField 战场
     */
    public final void executeAfterBattle(BattleFieldDTO battleField) {
        if (effect()) {
            afterBattle(battleField);
        }
    }

    /**
     * 每回合开始前
     *
     * @param battleRound 回合
     */
    public final void executeBeforeRound(BattleRoundDTO battleRound) {
        if (!effect()) {
            return;
        }
        beforeEachRound(battleRound);
        if (battleRound.getFrom().equals(this.owner)) {
            beforeMyRound(battleRound);
        }
        if (battleRound.getEnemy().contains(this.owner)) {
            beforeEnemyRound(battleRound);
        }
        if (battleRound.getOur().contains(this.owner)) {
            beforeOurRound(battleRound);
        }
    }

    /**
     * 每回合结束后
     *
     * @param battleRound 回合
     */
    public final void executeAfterRound(BattleRoundDTO battleRound) {
        if (!effect()) {
            return;
        }
        if (battleRound.getOur().contains(this.owner)) {
            afterOurRound(battleRound);
        }
        if (battleRound.getEnemy().contains(this.owner)) {
            afterEnemyRound(battleRound);
        }
        if (battleRound.getFrom().equals(this.owner)) {
            afterMyRound(battleRound);
        }
        afterEachRound(battleRound);
    }

    /**
     * 技能命中时
     *
     * @param battleEffect 单段伤害单个目标效果
     */
    public final void executeIfHit(BattleEffectDTO battleEffect) {
        if (!effect()) {
            return;
        }
        boolean fromMe = this.owner.equals(battleEffect.getFrom());
        boolean fromOur = this.ownerOur.contains(battleEffect.getFrom());
        boolean fromEnemy = this.ownerEnemy.contains(battleEffect.getFrom());

        boolean toMe = this.owner.equals(battleEffect.getTar());
        boolean toOur = this.ownerOur.contains(battleEffect.getTar());
        boolean toEnemy = this.ownerEnemy.contains(battleEffect.getTar());

        if (fromMe) {
            ifMeHit(battleEffect);
            if (toMe) {
                ifMeHitMe(battleEffect);
            } else if (toOur) {
                ifMeHitOur(battleEffect);
            } else if (toEnemy) {
                ifMeHitEnemy(battleEffect);
            }
        } else if (fromOur) {
            ifOurHit(battleEffect);
            if (toMe) {
                ifOurHitMe(battleEffect);
            } else if (toOur) {
                ifOurHitOur(battleEffect);
            } else if (toEnemy) {
                ifOurHitEnemy(battleEffect);
            }
        } else if (fromEnemy) {
            if (toMe) {
                ifEnemyHitMe(battleEffect);
            }
        }
    }

    /**
     * 技能未命中时
     *
     * @param battleEffect 单段伤害单个目标效果
     */
    public final void executeIfNotHit(BattleEffectDTO battleEffect) {
        if (!effect()) {
            return;
        }
        boolean fromMe = this.owner.equals(battleEffect.getFrom());
        boolean fromOur = this.ownerOur.contains(battleEffect.getFrom());
        boolean fromEnemy = this.ownerEnemy.contains(battleEffect.getFrom());

        boolean toMe = this.owner.equals(battleEffect.getTar());
        boolean toOur = this.ownerOur.contains(battleEffect.getTar());
        boolean toEnemy = this.ownerEnemy.contains(battleEffect.getTar());

        if (fromMe) {
            ifMeNotHit(battleEffect);
            if (toEnemy) {
                ifMeNotHitEnemy(battleEffect);
            }
        } else if (fromOur) {
            ifOurNotHit(battleEffect);
        } else if (fromEnemy) {
            if (toMe) {
                ifEnemyNotHitMe(battleEffect);
            }
            if (toOur) {
                ifEnemyNotHitOur(battleEffect);
            }
        }
    }

    public final void executeChangeDamageRate(BattleEffectDTO battleEffect) {
        if (!effect()) {
            return;
        }
        changeDamageRate(battleEffect);
    }

    public final void executeBeforeBehavior(BattleEffectDTO battleEffect) {
        if (!effect()) {
            return;
        }
        if (battleEffect.getFrom().equals(this.owner)) {
            beforeMyBehavior(battleEffect);
        }
        if (this.ownerEnemy.contains(battleEffect.getFrom())) {
            beforeEnemyBehavior(battleEffect);
        }
    }

    public final void executeAfterBehavior(BattleEffectDTO battleEffect) {
        if (!effect()) {
            return;
        }
        if (battleEffect.getFrom().equals(this.owner)) {
            afterMyBehavior(battleEffect);
        }
    }

    public final void executeAfterEnemyBehavior(BattleEffectDTO battleEffect) {
        if (!effect()) {
            return;
        }
        if (this.getOwnerEnemy().contains(battleEffect.getFrom())) {
            afterEnemyBehavior(battleEffect);
        }
    }

    public final void executeBeforeDeath(BattleEffectDTO battleEffect) {
        if (battleEffect.getTar().equals(this.owner)) {
            beforeMeDeath(battleEffect);
        }
    }

    public final void executeAfterDeath(BattleEffectDTO battleEffect) {
        if (battleEffect.getTar().equals(this.owner)) {
            afterMeDeath(battleEffect);
        }
    }

    public final void executeBeforeDamage(BattleEffectDTO battleEffect) {
        if (effect()) {
            if (battleEffect.getTar().equals(this.getOwner())) {
                beforeMeDamage(battleEffect);
            }
        }
    }

    public final void executeAfterDamage(BattleEffectDTO battleEffect) {
        if (effect()) {
            if (battleEffect.getTar().equals(this.getOwner())) {
                afterMeDamage(battleEffect);
            }
        }
    }

    public final void executeSpecialExecute(Object obj) {
        if (effect()) {
            specialExecute(obj);
        }
    }

    /**
     * 次数盾破碎
     */
    public final void executeTimesShieldBroken(String shieldName, BattleEffectDTO battleEffect) {
        if (effect()) {
            timesShieldBroken(shieldName, battleEffect);
        }
    }

    // 以下为钩子方法

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
     * 敌方轮次前
     */
    public void beforeEnemyRound(BattleRoundDTO battleRound) {
    }

    /**
     * 敌方轮次后
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

    /**
     * 我的行动前
     */
    public void beforeMyBehavior(BattleEffectDTO battleEffect) {
    }

    /**
     * 敌方行动前
     */
    public void beforeEnemyBehavior(BattleEffectDTO battleEffect) {
    }

    /**
     * 我的行动后
     */
    public void afterMyBehavior(BattleEffectDTO battleEffect) {
    }

    /**
     * 对方行动后
     */
    public void afterEnemyBehavior(BattleEffectDTO battleEffect) {
    }

    /**
     * 我的技能命中时
     */
    public void ifMeHit(BattleEffectDTO battleEffect) {
    }

    /**
     * 我的技能命中我自己时
     */
    public void ifMeHitMe(BattleEffectDTO battleEffect) {
    }

    /**
     * 我的技能命中我方时
     */
    public void ifMeHitOur(BattleEffectDTO battleEffect) {
    }

    /**
     * 我的技能命中敌方时
     */
    public void ifMeHitEnemy(BattleEffectDTO battleEffect) {
    }

    /**
     * 我方技能命中时
     */
    public void ifOurHit(BattleEffectDTO battleEffect) {
    }

    /**
     * 我方技能命中我时
     */
    public void ifOurHitMe(BattleEffectDTO battleEffect) {
    }

    /**
     * 我方技能命中我方时
     */
    public void ifOurHitOur(BattleEffectDTO battleEffect) {
    }

    /**
     * 我方技能命中敌方时
     */
    public void ifOurHitEnemy(BattleEffectDTO battleEffect) {
    }

    /**
     * 敌方技能命中我时
     */
    public void ifEnemyHitMe(BattleEffectDTO battleEffect) {
    }

    /**
     * 技能没有命中时
     */
    public void ifMeNotHit(BattleEffectDTO battleEffect) {
    }

    public void ifMeNotHitEnemy(BattleEffectDTO battleEffect){
    }

    public void ifOurNotHit(BattleEffectDTO battleEffect) {
    }

    public void ifEnemyNotHitMe(BattleEffectDTO battleEffect) {
    }

    public void ifEnemyNotHitOur(BattleEffectDTO battleEffect) {
    }

    public final void changeDamageRate(BattleEffectDTO battleEffect) {
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
     *
     * @param battleEffect 单个技能单个目标的战斗效果
     */
    public void beforeMeDamage(BattleEffectDTO battleEffect) {
    }

    /**
     * 受到伤害后
     *
     * @param battleEffect 单个技能单个目标的战斗效果
     */
    public void afterMeDamage(BattleEffectDTO battleEffect) {
    }

    /**
     * 次数盾破碎
     */
    public void timesShieldBroken(String shieldName, BattleEffectDTO battleEffect) {
    }

    /**
     * 特殊触发
     *
     * @param obj 入参
     */
    public void specialExecute(Object obj) {
    }
}
