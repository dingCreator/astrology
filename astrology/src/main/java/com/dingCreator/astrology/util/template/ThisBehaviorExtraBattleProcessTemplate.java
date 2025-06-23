package com.dingCreator.astrology.util.template;

import com.dingCreator.astrology.dto.battle.BattleEffectDTO;
import com.dingCreator.astrology.dto.battle.BattleRoundDTO;
import lombok.Data;

import java.io.Serializable;

/**
 * @author ding
 * @date 2024/4/3
 */
@Data
public abstract class ThisBehaviorExtraBattleProcessTemplate implements Serializable {

    /**
     * 本轮开始前
     */
    public void beforeThisRound(BattleRoundDTO battleRound) {
    }

    /**
     * 本轮结束后
     */
    public void afterThisRound(BattleRoundDTO battleRound) {
    }

    /**
     * 技能选定目标后生效前
     */
    public void beforeEffect(BattleEffectDTO battleEffect) {
    }

    /**
     * 技能选定目标后生效后
     */
    public void afterEffect(BattleEffectDTO battleEffect) {
    }

    /**
     * 技能命中时
     */
    public void ifHit(BattleEffectDTO battleEffect) {
    }

    /**
     * 技能没有命中时
     */
    public void ifNotHit(BattleEffectDTO battleEffect) {
    }

    /**
     * 修改技能倍率
     */
    public void changeDamageRate(BattleEffectDTO battleEffect) {
    }
}
