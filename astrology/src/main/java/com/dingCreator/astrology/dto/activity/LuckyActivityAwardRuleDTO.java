package com.dingCreator.astrology.dto.activity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author ding
 * @date 2024/11/28
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class LuckyActivityAwardRuleDTO extends BaseActivityAwardRuleDTO {
    /**
     * 概率
     */
    private Integer rate;
}
