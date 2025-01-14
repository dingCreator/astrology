package com.dingCreator.astrology.dto;

import com.dingCreator.astrology.enums.BuffOverrideStrategyEnum;
import com.dingCreator.astrology.enums.BuffTypeEnum;
import com.dingCreator.astrology.enums.skill.DamageTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author ding
 * @date 2024/2/2
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BuffDTO implements Serializable {
    /**
     * buff类型
     */
    private BuffTypeEnum buffType;
    /**
     * buff名称
     * 用于一些技能限制
     */
    private String buffName;
    /**
     * 伤害类buff的伤害类型
     */
    private DamageTypeEnum damageTypeEnum;
    /**
     * 数值变化
     */
    private Long value;
    /**
     * 比例变化
     */
    private BigDecimal rate;
    /**
     * buff覆盖规则
     */
    private BuffOverrideStrategyEnum buffOverrideStrategyEnum;
    /**
     * 是否属于异常
     */
    private Boolean abnormal;

    public BuffDTO(BuffTypeEnum buffType, String buffName, Long value) {
        this.buffType = buffType;
        this.buffName = buffName;
        this.value = value;
        this.rate = BigDecimal.ZERO;
        this.buffOverrideStrategyEnum = BuffOverrideStrategyEnum.MAX_ROUND;
        this.abnormal = false;
    }

    public BuffDTO(BuffTypeEnum buffType, String buffName, Float rate) {
        this.buffType = buffType;
        this.buffName = buffName;
        this.value = 0L;
        this.rate = new BigDecimal(String.valueOf(rate));
        this.buffOverrideStrategyEnum = BuffOverrideStrategyEnum.MAX_ROUND;
        this.abnormal = false;
    }

    public BuffDTO(BuffTypeEnum buffType, String buffName, Long value, Float rate) {
        this.buffType = buffType;
        this.buffName = buffName;
        this.value = value;
        this.rate = new BigDecimal(String.valueOf(rate));
        this.buffOverrideStrategyEnum = BuffOverrideStrategyEnum.MAX_ROUND;
        this.abnormal = false;
    }

    public BuffDTO(BuffTypeEnum buffType, String buffName, Long value, Boolean abnormal) {
        this.buffType = buffType;
        this.buffName = buffName;
        this.value = value;
        this.rate = BigDecimal.ZERO;
        this.buffOverrideStrategyEnum = BuffOverrideStrategyEnum.MAX_ROUND;
        this.abnormal = abnormal;
    }

    public BuffDTO(BuffTypeEnum buffType, String buffName, Float rate, Boolean abnormal) {
        this.buffType = buffType;
        this.buffName = buffName;
        this.value = 0L;
        this.rate = new BigDecimal(String.valueOf(rate));
        this.buffOverrideStrategyEnum = BuffOverrideStrategyEnum.MAX_ROUND;
        this.abnormal = abnormal;
    }

    public BuffDTO(BuffTypeEnum buffType, String buffName, Long value, Float rate, Boolean abnormal) {
        this.buffType = buffType;
        this.buffName = buffName;
        this.value = value;
        this.rate = new BigDecimal(String.valueOf(rate));
        this.buffOverrideStrategyEnum = BuffOverrideStrategyEnum.MAX_ROUND;
        this.abnormal = abnormal;
    }
}
