package com.dingCreator.astrology.util;

import com.dingCreator.astrology.dto.battle.BattleDTO;
import com.dingCreator.astrology.dto.battle.RuleDTO;
import com.dingCreator.astrology.enums.OrganismPropertiesEnum;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author ding
 * @date 2025/1/12
 */
public class RuleUtil {

    public static void addRule(BattleDTO battleDTO, OrganismPropertiesEnum organismPropertiesEnum, String ruleName,
                               long val, StringBuilder builder) {
        addRule(battleDTO, organismPropertiesEnum, ruleName, val, 0F, builder);
    }

    public static void addRule(BattleDTO battleDTO, OrganismPropertiesEnum organismPropertiesEnum, String ruleName,
                               float rate, StringBuilder builder) {
        addRule(battleDTO, organismPropertiesEnum, ruleName, 0L, rate, builder);
    }

    public static void addRule(BattleDTO battleDTO, OrganismPropertiesEnum organismPropertiesEnum, String ruleName,
                               long val, float rate, StringBuilder builder) {
        RuleDTO ruleDTO = RuleDTO.builder()
                .ruleName(ruleName)
                .organismPropertiesEnum(organismPropertiesEnum)
                .val(val)
                .rate(new BigDecimal(rate)).build();
        battleDTO.getRuleList().add(ruleDTO);

        if (!builder.toString().contains(ruleName)
                || !builder.toString().contains(battleDTO.getOrganismInfoDTO().getOrganismDTO().getName())) {
            if (builder.length() > 0) {
                builder.append("，");
            }
            builder.append(battleDTO.getOrganismInfoDTO().getOrganismDTO().getName())
                    .append("附加法则<").append(ruleName).append(">");
        }
        if (val != 0) {
            builder.append("，").append(organismPropertiesEnum.getChnDesc()).append(val > 0 ? "+" + val : val);
        }
        if (rate != 0) {
            BigDecimal rateDecimal = NumberUtil.multiply(100, new BigDecimal(rate), 2);
            String rateStr = rate > 0 ? "+" + rateDecimal.floatValue() + "%" : rateDecimal.floatValue() + "%";
            builder.append("，").append(organismPropertiesEnum.getChnDesc()).append(rateStr);
        }
    }

    public static long getVal(BattleDTO battleDTO, OrganismPropertiesEnum organismPropertiesEnum, long src) {
        if (Objects.isNull(organismPropertiesEnum)) {
            return src;
        }
        long val = battleDTO.getRuleList().stream()
                .filter(rule -> organismPropertiesEnum.equals(rule.getOrganismPropertiesEnum()))
                .map(RuleDTO::getVal)
                .filter(ruleVal -> ruleVal != 0)
                .reduce(src, Long::sum);
        BigDecimal rate = battleDTO.getRuleList().stream()
                .filter(rule -> organismPropertiesEnum.equals(rule.getOrganismPropertiesEnum()))
                .map(RuleDTO::getRate)
                .filter(ruleRate -> ruleRate.compareTo(BigDecimal.ZERO) != 0)
                .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        return NumberUtil.multiply(val, rate.add(BigDecimal.ONE));
    }

    public static float getRate(BattleDTO battleDTO, OrganismPropertiesEnum organismPropertiesEnum, float src) {
        if (Objects.isNull(organismPropertiesEnum)) {
            return src;
        }
        return battleDTO.getRuleList().stream()
                .filter(rule -> organismPropertiesEnum.equals(rule.getOrganismPropertiesEnum()))
                .map(RuleDTO::getRate)
                .filter(rate -> rate.compareTo(BigDecimal.ZERO) != 0)
                .reduce(new BigDecimal(src), BigDecimal::add).floatValue();
    }
}
