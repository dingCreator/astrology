package com.dingCreator.astrology.dto;

import com.dingCreator.astrology.enums.BuffOverrideStrategyEnum;
import com.dingCreator.astrology.enums.BuffTypeEnum;
import com.dingCreator.astrology.enums.skill.DamageTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

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
    private Float rate;
    /**
     * buff覆盖规则
     */
    private BuffOverrideStrategyEnum buffOverrideStrategyEnum;

    public BuffDTO(BuffTypeEnum buffType, String buffName, Long value) {
        this.buffType = buffType;
        this.buffName = buffName;
        this.value = value;
        this.rate = 0F;
        this.buffOverrideStrategyEnum = BuffOverrideStrategyEnum.MAX_ROUND;
    }

    public BuffDTO(BuffTypeEnum buffType, String buffName, Float rate) {
        this.buffType = buffType;
        this.buffName = buffName;
        this.value = 0L;
        this.rate = rate;
        this.buffOverrideStrategyEnum = BuffOverrideStrategyEnum.MAX_ROUND;
    }

    public BuffDTO(BuffTypeEnum buffType, String buffName, Long value, Float rate) {
        this.buffType = buffType;
        this.buffName = buffName;
        this.value = value;
        this.rate = rate;
        this.buffOverrideStrategyEnum = BuffOverrideStrategyEnum.MAX_ROUND;
    }
}
