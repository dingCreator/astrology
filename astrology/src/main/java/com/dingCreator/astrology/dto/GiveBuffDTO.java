package com.dingCreator.astrology.dto;

import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.enums.BuffTypeEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
@Accessors(chain = true)
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
     * buff名称
     */
    private String buffName;
    /**
     * 固定数值变化
     */
    private Long value;
    /**
     * 比例变化
     */
    private BigDecimal rate;
    /**
     * 持续回合数
     */
    private Integer round;
    /**
     * 生效概率
     */
    private BigDecimal effectedRate;
    /**
     * 是否为异常
     */
    private Boolean abnormal;

    public GiveBuffDTO(BuffTypeEnum buffTypeEnum, String buffName, Integer round) {
        this(buffTypeEnum, buffName, 0L, 0F, round, 1F, false);
    }

    public GiveBuffDTO(BuffTypeEnum buffTypeEnum, String buffName, Integer round, Boolean abnormal) {
        this(buffTypeEnum, buffName, 0L, 0F, round, 1F, abnormal);
    }

    public GiveBuffDTO(BuffTypeEnum buffTypeEnum, String buffName, Integer round, Float effectedRate) {
        this(buffTypeEnum, buffName, 0L, 0F, round, effectedRate, false);
    }

    public GiveBuffDTO(BuffTypeEnum buffTypeEnum, String buffName, Long value, Integer round) {
        this(buffTypeEnum, buffName, value, 0F, round, 1F, false);
    }

    public GiveBuffDTO(BuffTypeEnum buffTypeEnum, String buffName, Float rate, Integer round) {
        this(buffTypeEnum, buffName, 0L, rate, round, 1F, false);
    }

    public GiveBuffDTO(BuffTypeEnum buffTypeEnum, String buffName, Long value, Float rate, Integer round) {
        this(buffTypeEnum, buffName, value, rate, round, 1F, false);
    }

    public GiveBuffDTO(BuffTypeEnum buffTypeEnum, String buffName, Long value, Integer round, Float effectedRate) {
        this(buffTypeEnum, buffName, value, 0F, round, effectedRate, false);
    }

    public GiveBuffDTO(BuffTypeEnum buffTypeEnum, String buffName, Float rate, Integer round, Float effectedRate) {
        this(buffTypeEnum, buffName, 0L, rate, round, effectedRate, false);
    }

    public GiveBuffDTO(BuffTypeEnum buffTypeEnum, String buffName, Float rate, Integer round, Boolean abnormal) {
        this(buffTypeEnum, buffName, 0L, rate, round, 1F, abnormal);
    }

    public GiveBuffDTO(BuffTypeEnum buffTypeEnum, String buffName, Float rate, Integer round, Float effectedRate,
                       Boolean abnormal) {
        this(buffTypeEnum, buffName, 0L, rate, round, effectedRate, abnormal);
    }

    public GiveBuffDTO(BuffTypeEnum buffTypeEnum, String buffName, Long value, Float rate,
                       Integer round, Float effectedRate, Boolean abnormal) {
        this(buffTypeEnum, buffTypeEnum, buffName, value, new BigDecimal(String.valueOf(rate)), round,
                new BigDecimal(String.valueOf(effectedRate)), abnormal);
    }

    public GiveBuffDTO(BuffTypeEnum buffType, BuffTypeEnum dependsBuffType, String buffName,
                       Long value, BigDecimal rate, Integer round, BigDecimal effectedRate, Boolean abnormal) {
        if (effectedRate.compareTo(BigDecimal.valueOf(Constants.MIN_RATE)) < 0
                || effectedRate.compareTo(BigDecimal.valueOf(Constants.MAX_RATE)) > 0) {
            throw new IllegalArgumentException("buff【" + buffName + "】生效概率配置错误");
        }
        this.buffType = buffType;
        this.dependsBuffType = dependsBuffType;
        this.buffName = buffName;
        this.value = value;
        this.rate = rate;
        this.round = round;
        this.effectedRate = effectedRate;
        this.abnormal = abnormal;
    }
}
