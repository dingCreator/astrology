package com.dingCreator.astrology.template;

/**
 * @author ding
 * @date 2024/4/3
 */
public interface ExtraBattleProcessTemplate {
    /**
     * 战斗前
     */
    default void beforeBattle() {

    }

    /**
     * 每轮前
     */
    default void beforeEachRound() {

    }

    /**
     * 我的轮次前
     */
    default void beforeMyRound() {

    }

    /**
     * 我的行动前
     */
    default void beforeMyBehavior() {

    }

    /**
     * 攻击命中时
     */
    default void ifHit() {

    }

    /**
     * 如果没有命中
     */
    default void ifNotHit() {

    }

    /**
     * 我的行动后
     */
    default void afterMyBehavior() {

    }

    /**
     * 我的轮次后
     */
    default void afterMyRound() {

    }

    /**
     * 每轮后
     */
    default void afterEachRound() {

    }

    /**
     * 战斗后
     */
    default void afterBattle() {

    }
}
