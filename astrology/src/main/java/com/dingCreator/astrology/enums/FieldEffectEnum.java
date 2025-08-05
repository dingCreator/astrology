package com.dingCreator.astrology.enums;

import com.dingCreator.astrology.dto.battle.BattleDTO;
import com.dingCreator.astrology.dto.battle.BattleEffectDTO;
import com.dingCreator.astrology.dto.battle.BattleFieldDTO;
import com.dingCreator.astrology.dto.battle.BattleRoundDTO;
import com.dingCreator.astrology.enums.skill.SkillEnum;
import com.dingCreator.astrology.util.BattleUtil;
import com.dingCreator.astrology.util.BuffUtil;
import com.dingCreator.astrology.util.RandomUtil;
import com.dingCreator.astrology.util.template.ExtraBattleProcessTemplate;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.*;

/**
 * @author ding
 * @date 2025/4/2
 */
@Getter
@AllArgsConstructor
public enum FieldEffectEnum implements Serializable {

    EFFECT_1(1L, "临渊绝响",
            "双方血量回复效果降低70%，攻击与法强提升50%",
            new Effect() {
                @Override
                public Float getRate(Float rate, EffectTypeEnum effectTypeEnum) {
                    if (EffectTypeEnum.HEAL.equals(effectTypeEnum)) {
                        return rate - 0.7F;
                    }
                    if (EffectTypeEnum.ATK.equals(effectTypeEnum) || EffectTypeEnum.MAGIC_ATK.equals(effectTypeEnum)) {
                        return rate + 0.5F;
                    }
                    return rate;
                }
            }
    ),

    EFFECT_2(2L, "星之彼岸",
            "双方血量回复效果提升30%，法强与法术穿透提升20%，星之彼岸存在时双方清除并免疫异常状态与弱化类buff效果",
            new Effect() {
                @Override
                public Float getRate(Float rate, EffectTypeEnum effectTypeEnum) {
                    if (EffectTypeEnum.HEAL.equals(effectTypeEnum)) {
                        return rate + 0.3F;
                    }
                    if (EffectTypeEnum.MAGIC_PENETRATE.equals(effectTypeEnum) || EffectTypeEnum.MAGIC_ATK.equals(effectTypeEnum)) {
                        return rate + 0.2F;
                    }
                    return rate;
                }

                @Override
                public List<EffectTypeEnum> markTypeBuff() {
                    return Collections.singletonList(EffectTypeEnum.IMMUNITY);
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
    ),

    EFFECT_3(3L, "虚神界", "双方闪避提高40%",
            new Effect() {
                @Override
                public Float getRate(Float rate, EffectTypeEnum effectTypeEnum) {
                    if (EffectTypeEnum.DODGE.equals(effectTypeEnum)) {
                        return rate + 0.4F;
                    }
                    return rate;
                }
            }
    ),

    EFFECT_4(4L, "无疆死域", "双方每回合行动后有20%概率获得一枚“生魂”，70%概率获得一枚“死魂”，" +
            "“生魂”可回复拘魂者10000点血量，“死魂”可对敌方造成2100点真实伤害",
            new Effect() {
                @Override
                public void afterRound(BattleRoundDTO battleRound) {
                    BattleDTO from = battleRound.getFrom();
                    StringBuilder builder = battleRound.getBuilder();
                    Map<String, Integer> markMap = from.getMarkMap();
                    if (RandomUtil.isHit(0.2F)) {
                        builder.append("，").append(from.getOrganismInfoDTO().getOrganismDTO().getName()).append("获得“生魂”");
                        BattleUtil.doHealing(from, 10000L, builder, battleRound.getBattleField());
                        markMap.put("生魂", markMap.getOrDefault("生魂", 0) + 1);
                    }
                    if (RandomUtil.isHit(0.7F)) {
                        builder.append("，").append(from.getOrganismInfoDTO().getOrganismDTO().getName()).append("获得“死魂”");
                        BattleUtil.doRealDamage(battleRound.getEnemy().get(0), 2100L, builder);
                        markMap.put("死魂", markMap.getOrDefault("死魂", 0) + 1);
                        ExtraBattleProcessTemplate specialExecuteTpl = battleRound.getBattleField()
                                .getExtraBattleProcessTemplateList().stream()
                                .filter(extra -> from.equals(extra.getOwner()))
                                .filter(extra -> extra.getClass().equals(SkillEnum.SKILL_1081.getGlobalExtraProcess().getClass()))
                                .findFirst().orElse(null);
                        if (Objects.nonNull(specialExecuteTpl)) {
                            specialExecuteTpl.executeSpecialExecute(battleRound);
                        }
                    }
                }
            }
    ),
    ;

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

        default Long getVal(Long src, EffectTypeEnum effectTypeEnum) {
            return src;
        }

        default Float getRate(Float rate, EffectTypeEnum effectTypeEnum) {
            return rate;
        }

        default List<EffectTypeEnum> markTypeBuff() {
            return new ArrayList<>();
        }

        default void afterRound(BattleRoundDTO battleRound) {
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
