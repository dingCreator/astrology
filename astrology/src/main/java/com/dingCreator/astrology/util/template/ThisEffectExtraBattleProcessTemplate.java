package com.dingCreator.astrology.util.template;

import com.dingCreator.astrology.dto.battle.BattleDTO;
import com.dingCreator.astrology.dto.battle.BattleEffectDTO;
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
public abstract class ThisEffectExtraBattleProcessTemplate implements Serializable {

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

    public void beforeEffect(BattleEffectDTO battleEffect) {
    }

    public void afterEffect(BattleEffectDTO battleEffect) {
    }
}
