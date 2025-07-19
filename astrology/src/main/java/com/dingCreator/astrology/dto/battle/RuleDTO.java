package com.dingCreator.astrology.dto.battle;

import com.dingCreator.astrology.enums.EffectTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 法则
 *
 * @author ding
 * @date 2025/1/12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleDTO implements Serializable {
    /**
     * 法则名称
     */
    private String ruleName;
    /**
     * 属性
     */
    private EffectTypeEnum effectTypeEnum;
    /**
     * 值
     */
    private Long val;
    /**
     * 比例
     */
    private BigDecimal rate;
}
