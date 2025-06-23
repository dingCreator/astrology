package com.dingCreator.astrology.dto.activity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author ding
 * @date 2024/11/28
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LuckyActivityAwardRuleDTO extends BaseActivityAwardRuleDTO {

    public static final String FIELD_RATE = "rate";

    /**
     * 概率
     */
    private Integer rate;
}
