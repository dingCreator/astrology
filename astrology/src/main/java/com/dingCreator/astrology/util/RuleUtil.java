package com.dingCreator.astrology.util;

import cn.hutool.core.collection.CollectionUtil;
import com.dingCreator.astrology.dto.battle.BattleBuffDTO;
import com.dingCreator.astrology.dto.battle.BattleDTO;
import com.dingCreator.astrology.dto.battle.BattleEffectDTO;
import com.dingCreator.astrology.dto.battle.RuleDTO;
import com.dingCreator.astrology.enums.BuffTypeEnum;
import com.dingCreator.astrology.enums.OrganismPropertiesEnum;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * @author ding
 * @date 2025/1/12
 */
public class RuleUtil {

    public static void addRule(BattleDTO battleDTO, BuffTypeEnum buffTypeEnum, String ruleName,
                               long val, StringBuilder builder) {
        addRule(battleDTO, buffTypeEnum, ruleName, val, 0F, builder);
    }

    public static void addRule(BattleDTO battleDTO, BuffTypeEnum buffTypeEnum, String ruleName,
                               float rate, StringBuilder builder) {
        addRule(battleDTO, buffTypeEnum, ruleName, 0L, rate, builder);
    }

    public static void addRule(BattleDTO battleDTO, BuffTypeEnum buffTypeEnum, String ruleName,
                               long val, float rate, StringBuilder builder) {
        RuleDTO ruleDTO = RuleDTO.builder()
                .ruleName(ruleName)
                .buffTypeEnum(buffTypeEnum)
                .val(val)
                .rate(new BigDecimal(rate)).build();
        battleDTO.getRuleList().add(ruleDTO);

        if (!builder.toString().contains(ruleName)
                || !builder.toString().contains(battleDTO.getOrganismInfoDTO().getOrganismDTO().getName())) {
            if (builder.length() > 0) {
                builder.append("，");
            }
            builder.append(battleDTO.getOrganismInfoDTO().getOrganismDTO().getName())
                    .append("附加<").append(ruleName).append(">");
        }
        if (val != 0) {
            builder.append(buffTypeEnum.getChnDesc()).append(val > 0 ? "+" + val : val);
        }
        if (rate != 0) {
            BigDecimal rateDecimal = NumberUtil.multiply(100, new BigDecimal(rate), 2);
            String rateStr = rate > 0 ? "+" + rateDecimal.floatValue() + "%" : rateDecimal.floatValue() + "%";
            builder.append("，").append(buffTypeEnum.getChnDesc()).append(rateStr);
        }
    }

    public static long getVal(BattleDTO battleDTO, BuffTypeEnum buffTypeEnum, long src) {
        if (Objects.isNull(buffTypeEnum)) {
            return src;
        }
        long val = battleDTO.getRuleList().stream()
                .filter(rule -> buffTypeEnum.equals(rule.getBuffTypeEnum()))
                .map(RuleDTO::getVal)
                .filter(ruleVal -> ruleVal != 0)
                .reduce(src, Long::sum);
        BigDecimal rate = battleDTO.getRuleList().stream()
                .filter(rule -> buffTypeEnum.equals(rule.getBuffTypeEnum()))
                .map(RuleDTO::getRate)
                .filter(ruleRate -> ruleRate.compareTo(BigDecimal.ZERO) != 0)
                .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        return NumberUtil.multiply(val, rate.add(BigDecimal.ONE));
    }

    public static float getRate(BattleDTO battleDTO, BuffTypeEnum buffTypeEnum, float src) {
        if (Objects.isNull(buffTypeEnum)) {
            return src;
        }
        return battleDTO.getRuleList().stream()
                .filter(rule -> buffTypeEnum.equals(rule.getBuffTypeEnum()))
                .map(RuleDTO::getRate)
                .filter(rate -> rate.compareTo(BigDecimal.ZERO) != 0)
                .reduce(new BigDecimal(src), BigDecimal::add).floatValue();
    }

    public static boolean calInvincible(BattleEffectDTO battleEffect) {
        RuleDTO invincibleRule = battleEffect.getTar().getRuleList().stream()
                .filter(r -> BuffTypeEnum.INVINCIBLE.equals(r.getBuffTypeEnum()))
                .findFirst().orElse(null);
        if (Objects.nonNull(invincibleRule)) {
            StringBuilder builder = battleEffect.getBattleRound().getBuilder();
            builder.append("，").append(battleEffect.getTar().getOrganismInfoDTO().getOrganismDTO().getName())
                    .append("状态：").append(invincibleRule.getRuleName()).append("，无法造成伤害");
            long times = invincibleRule.getVal();
            if (times > 0) {
                --times;
                if (times == 0) {
                    battleEffect.getTar().getRuleList().remove(invincibleRule);
                } else {
                    invincibleRule.setVal(times);
                }
                builder.append("，").append("剩余次数：").append(times);
            }
            return true;
        }
        return false;
    }
}
