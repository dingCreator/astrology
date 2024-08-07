package com.dingCreator.astrology.template;

import com.dingCreator.astrology.dto.BattleDTO;
import com.dingCreator.astrology.enums.skill.SkillEnum;
import lombok.Data;

import java.util.List;

/**
 * @author ding
 * @date 2024/4/3
 */
@Data
public abstract class ExtraBattleProcessTemplate {

    private BattleDTO from;
    private List<BattleDTO> our;
    private List<BattleDTO> enemy;

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
    public void beforeMyBehavior(StringBuilder builder) {

    }

    /**
     * 我的行动后
     */
    public void afterMyBehavior(StringBuilder builder) {

    }

    /**
     * 技能命中时
     */
    public void ifHit(BattleDTO from, SkillEnum skillEnum, Long damage, StringBuilder builder) {

    }

    /**
     * 技能没有命中时
     */
    public void ifNotHit(SkillEnum skillEnum, StringBuilder builder) {

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
