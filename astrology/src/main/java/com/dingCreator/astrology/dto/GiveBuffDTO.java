package com.dingCreator.astrology.dto;

import com.dingCreator.astrology.enums.BuffTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 给予对方的buff效果，分为两种情况
 * 1. 改变固定数值
 * 2. 改变自身属性比例的数值
 *
 * @author ding
 * @date 2024/4/2
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GiveBuffDTO implements Serializable {
    /**
     * buff类型
     */
    private BuffTypeEnum buffType;
    /**
     * 基于何种数值的比例变化
     */
    private BuffTypeEnum dependsBuffType;
    /**
     * 固定数值变化
     */
    private Long value;
    /**
     * 比例变化
     */
    private Float rate;
    /**
     * 持续回合数
     */
    private Integer round;
    /**
     * 生效概率
     */
    private Float effectedRate;

    public GiveBuffDTO(BuffTypeEnum buffTypeEnum, Long value, Float rate, Integer round, Float effectedRate) {
        this.buffType = buffTypeEnum;
        this.dependsBuffType = buffTypeEnum;
        this.value = value;
        this.rate = rate;
        this.round = round;
        this.effectedRate = effectedRate;
    }
}
