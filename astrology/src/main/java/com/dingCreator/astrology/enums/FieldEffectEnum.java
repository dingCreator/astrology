package com.dingCreator.astrology.enums;

import com.dingCreator.astrology.dto.battle.BattleEffectDTO;
import com.dingCreator.astrology.dto.battle.BattleFieldDTO;
import com.dingCreator.astrology.dto.battle.BattleRoundDTO;
import com.dingCreator.astrology.util.BuffUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author ding
 * @date 2025/4/2
 */
@Getter
@AllArgsConstructor
public enum FieldEffectEnum {

    EFFECT_1(1L, "临渊绝响",
            "双方血量回复效果降低70%，攻击与法强提升50%",
            new Effect() {
                @Override
                public Float getRate(Float rate, BuffTypeEnum buffTypeEnum) {
                    if (BuffTypeEnum.HEAL.equals(buffTypeEnum)) {
                        return 0.3F * rate;
                    }
                    if (BuffTypeEnum.ATK.equals(buffTypeEnum) || BuffTypeEnum.MAGIC_ATK.equals(buffTypeEnum)) {
                        return 1.5F * rate;
                    }
                    return rate;
                }
            }
    ),

    EFFECT_2(2L, "星之彼岸",
            "双方血量回复效果提升30%，法强与法术穿透提升20%，星之彼岸存在时双方清除并免疫异常状态与弱化类buff效果",
            new Effect() {
                @Override
                public Float getRate(Float rate, BuffTypeEnum buffTypeEnum) {
                    return Effect.super.getRate(rate, buffTypeEnum);
                }

                @Override
                public List<BuffTypeEnum> markTypeBuff() {
                    return Collections.singletonList(BuffTypeEnum.IMMUNITY);
                }

                @Override
                public void initEffect(BattleRoundDTO battleRound) {
                    battleRound.getOur().forEach(b -> {
                                BuffUtil.clearInactiveBuff(b, battleRound.getBuilder());
                                BuffUtil.clearAbnormalBuff(b, battleRound.getBuilder());
                            }
                    );
                    battleRound.getEnemy().forEach(b -> {
                                BuffUtil.clearInactiveBuff(b, battleRound.getBuilder());
                                BuffUtil.clearAbnormalBuff(b, battleRound.getBuilder());
                            }
                    );
                }
            }
    );

    private final Long id;

    private final String fieldEffectName;

    private final String description;

    private final Effect effect;


    public interface Effect {
        /**
         * 场地效果开启时
         */
        default void initEffect(BattleFieldDTO battleField) {

        }

        default void initEffect(BattleRoundDTO battleRound) {

        }

        default void initEffect(BattleEffectDTO battleEffect) {

        }

        default Long getVal(Long src, BuffTypeEnum buffTypeEnum) {
            return src;
        }

        default Float getRate(Float rate, BuffTypeEnum buffTypeEnum) {
            return rate;
        }

        default List<BuffTypeEnum> markTypeBuff() {
            return new ArrayList<>();
        }

        /**
         * 场地效果关闭时
         */
        default void finalizeEffect(BattleFieldDTO battleField) {

        }

        default void finalizeEffect(BattleRoundDTO battleRound) {

        }

        default void finalizeEffect(BattleEffectDTO battleEffect) {

        }
    }
}
