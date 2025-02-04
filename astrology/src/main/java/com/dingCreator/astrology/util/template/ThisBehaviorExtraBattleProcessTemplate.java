package com.dingCreator.astrology.util.template;

import com.dingCreator.astrology.dto.BattleDTO;
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
public abstract class ThisBehaviorExtraBattleProcessTemplate implements Serializable {

    /**
     * 本轮开始前
     */
    public void beforeThisRound(BattleDTO from, List<BattleDTO> our, List<BattleDTO> enemy, StringBuilder builder) {

    }

    /**
     * 本轮结束后
     */
    public void afterThisRound(BattleDTO from, List<BattleDTO> our, List<BattleDTO> enemy, StringBuilder builder) {

    }

    /**
     * 技能选定目标后生效前
     */
    public void beforeEffect(BattleDTO from, BattleDTO tar,
                             List<BattleDTO> our, List<BattleDTO> enemy,
                             StringBuilder builder) {

    }

    /**
     * 技能选定目标后生效后
     */
    public void afterEffect(BattleDTO from, BattleDTO tar,
                            List<BattleDTO> our, List<BattleDTO> enemy,
                            StringBuilder builder) {

    }

    /**
     * 技能命中时
     */
    public void ifHit(BattleDTO from, BattleDTO tar,
                            List<BattleDTO> our, List<BattleDTO> enemy,
                            AtomicLong damage, boolean critical, StringBuilder builder) {

    }

    /**
     * 技能没有命中时
     */
    public void ifNotHit(BattleDTO from, BattleDTO tar,
                               List<BattleDTO> our, List<BattleDTO> enemy,
                               StringBuilder builder) {

    }

    /**
     * 修改技能倍率
     *
     * @param from       来源
     * @param tar        目标
     * @param damageRate 原倍率
     * @param builder    文描
     * @return 修改后的技能倍率
     */
    public BigDecimal changeDamageRate(BattleDTO from, BattleDTO tar,
                                       List<BattleDTO> our, List<BattleDTO> enemy,
                                       BigDecimal damageRate, StringBuilder builder) {
        return damageRate;
    }
}
