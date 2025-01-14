package com.dingCreator.astrology.util;

import com.dingCreator.astrology.dto.BattleDTO;
import com.dingCreator.astrology.dto.RuleDTO;
import com.dingCreator.astrology.enums.OrganismPropertiesEnum;
import lombok.val;

import java.math.BigDecimal;

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

        if (builder.length() > 0) {
            builder.append("，");
        }
        builder.append(battleDTO.getOrganismInfoDTO().getOrganismDTO().getName())
                .append("附加法则<").append(ruleName).append(">");
        if (val != 0) {
            builder.append("，").append(organismPropertiesEnum.getChnDesc()).append(val > 0 ? "+" + val : val);
        }
        if (rate != 0) {
            BigDecimal rateDecimal = BigDecimalUtil.multiply(100, new BigDecimal(rate), 2);
            String rateStr = rate > 0 ? "+" + rateDecimal.floatValue() + "%" : rateDecimal.floatValue() + "%";
            builder.append("，").append(organismPropertiesEnum.getChnDesc()).append(rateStr);
        }
    }

    public static long getVal(BattleDTO battleDTO, OrganismPropertiesEnum organismPropertiesEnum, long src) {
        long val = battleDTO.getRuleList().stream()
                .filter(rule -> organismPropertiesEnum.equals(rule.getOrganismPropertiesEnum()))
                .filter(rule -> rule.getVal() != 0)
                .map(RuleDTO::getVal)
                .reduce(src, Long::sum);
        BigDecimal rate = battleDTO.getRuleList().stream().filter(rule -> rule.getRate().compareTo(BigDecimal.ZERO) != 0)
                .map(RuleDTO::getRate)
                .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        return BigDecimalUtil.multiply(val, rate.add(BigDecimal.ONE));
    }

    public static float getRate(BattleDTO battleDTO, OrganismPropertiesEnum organismPropertiesEnum, float src) {
        return battleDTO.getRuleList().stream()
                .filter(rule -> organismPropertiesEnum.equals(rule.getOrganismPropertiesEnum()))
                .filter(rule -> rule.getRate().compareTo(BigDecimal.ZERO) != 0)
                .map(RuleDTO::getRate)
                .reduce(new BigDecimal(src), BigDecimal::add).floatValue();
    }
}
