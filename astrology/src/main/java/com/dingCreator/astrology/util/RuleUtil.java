package com.dingCreator.astrology.util;

import com.dingCreator.astrology.dto.battle.BattleDTO;
import com.dingCreator.astrology.dto.battle.BattleEffectDTO;
import com.dingCreator.astrology.dto.battle.RuleDTO;
import com.dingCreator.astrology.enums.EffectTypeEnum;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author ding
 * @date 2025/1/12
 */
public class RuleUtil {

    public static void addRule(BattleDTO from, BattleDTO tar, EffectTypeEnum effectTypeEnum, String ruleName,
                               long val, StringBuilder builder) {
        addRule(from, tar, effectTypeEnum, ruleName, val, 0F, builder);
    }

    public static void addRule(BattleDTO from, BattleDTO tar, EffectTypeEnum effectTypeEnum, String ruleName,
                               float rate, StringBuilder builder) {
        addRule(from, tar, effectTypeEnum, ruleName, 0L, rate, builder);
    }

    public static void addRule(BattleDTO from, BattleDTO tar, EffectTypeEnum effectTypeEnum, String ruleName,
                               long val, float rate, StringBuilder builder) {
        RuleDTO ruleDTO = RuleDTO.builder()
                .from(from).tar(tar)
                .ruleName(ruleName).effectTypeEnum(effectTypeEnum)
                .val(val).rate(new BigDecimal(rate)).build();
        tar.getRuleList().add(ruleDTO);

        if (!builder.toString().contains(ruleName)
                || !builder.toString().contains(tar.getOrganismInfoDTO().getOrganismDTO().getName())) {
            if (builder.length() > 0) {
                builder.append("，");
            }
            builder.append(tar.getOrganismInfoDTO().getOrganismDTO().getName())
                    .append("附加<").append(ruleName).append(">");
        }
        if (val != 0) {
            builder.append(effectTypeEnum.getChnDesc()).append(val > 0 ? "+" + val : val);
        }
        if (rate != 0) {
            BigDecimal rateDecimal = NumberUtil.multiply(100, new BigDecimal(rate), 2);
            String rateStr = rate > 0 ? "+" + rateDecimal.floatValue() + "%" : rateDecimal.floatValue() + "%";
            builder.append("，").append(effectTypeEnum.getChnDesc()).append(rateStr);
        }
    }

    public static long getVal(BattleDTO battleDTO, EffectTypeEnum effectTypeEnum, long src) {
        if (Objects.isNull(effectTypeEnum)) {
            return src;
        }
        long val = battleDTO.getRuleList().stream()
                .filter(rule -> effectTypeEnum.equals(rule.getEffectTypeEnum()))
                .map(RuleDTO::getVal)
                .filter(ruleVal -> ruleVal != 0)
                .reduce(src, Long::sum);
        BigDecimal rate = battleDTO.getRuleList().stream()
                .filter(rule -> effectTypeEnum.equals(rule.getEffectTypeEnum()))
                .map(RuleDTO::getRate)
                .filter(ruleRate -> ruleRate.compareTo(BigDecimal.ZERO) != 0)
                .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        return NumberUtil.multiply(val, rate.add(BigDecimal.ONE));
    }

    public static float getRate(BattleDTO battleDTO, EffectTypeEnum effectTypeEnum, float src) {
        if (Objects.isNull(effectTypeEnum)) {
            return src;
        }
        return battleDTO.getRuleList().stream()
                .filter(rule -> effectTypeEnum.equals(rule.getEffectTypeEnum()))
                .map(RuleDTO::getRate)
                .filter(rate -> rate.compareTo(BigDecimal.ZERO) != 0)
                .reduce(new BigDecimal(src), BigDecimal::add).floatValue();
    }

    public static boolean calInvincible(BattleEffectDTO battleEffect) {
        if (battleEffect.getFrom().equals(battleEffect.getTar())
                || battleEffect.getDamageRate().compareTo(BigDecimal.ZERO) == 0) {
            // 不阻挡来源于自己或者没有伤害的技能
            return false;
        }
        RuleDTO invincibleRule = battleEffect.getTar().getRuleList().stream()
                .filter(r -> EffectTypeEnum.TIMES_SHIELD.equals(r.getEffectTypeEnum()))
                .findFirst().orElse(null);
        if (Objects.nonNull(invincibleRule)) {
            StringBuilder builder = battleEffect.getBattleRound().getBuilder();
            builder.append("，").append(battleEffect.getTar().getOrganismInfoDTO().getOrganismDTO().getName())
                    .append("状态：").append(invincibleRule.getRuleName()).append("，技能效果未生效");
            long times = invincibleRule.getVal();
            if (times > 0) {
                --times;
                if (times == 0) {
                    battleEffect.getTar().getRuleList().remove(invincibleRule);
                    battleEffect.getBattleRound().getBattleField().getExtraBattleProcessTemplateList()
                            .forEach(ext -> ext.timesShieldBroken(invincibleRule.getRuleName(), battleEffect));
                } else {
                    invincibleRule.setVal(times);
                }
                builder.append("，").append("剩余次数：").append(times);
            }
            return true;
        }
        return false;
    }

    public static void removeRule(BattleDTO battleDTO, String ruleName) {
        battleDTO.getRuleList().removeIf(rule -> ruleName.equals(rule.getRuleName()));
    }
}
