package com.dingCreator.astrology.dto;

import com.dingCreator.astrology.enums.OrganismPropertiesEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class RuleDTO {
    /**
     * 法则名称
     */
    private String ruleName;
    /**
     * 属性
     */
    private OrganismPropertiesEnum organismPropertiesEnum;
    /**
     * 值
     */
    private Long val;
    /**
     * 比例
     */
    private BigDecimal rate;
}
