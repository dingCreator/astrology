package com.dingCreator.astrology.dto;

import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.enums.BuffTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
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
    private Float rate;
    /**
     * 持续回合数
     */
    private Integer round;
    /**
     * 生效概率
     */
    private Float effectedRate;
    /**
     * buff上限
     */
    private List<BuffLimit> buffLimit;

    public GiveBuffDTO(BuffTypeEnum buffTypeEnum, String buffName, Integer round) {
        this(buffTypeEnum, buffName, 0L, 0F, round, 1F, new ArrayList<>());
    }

    public GiveBuffDTO(BuffTypeEnum buffTypeEnum, String buffName, Integer round, Float effectedRate) {
        this(buffTypeEnum, buffName, 0L, 0F, round, effectedRate, new ArrayList<>());
    }

    public GiveBuffDTO(BuffTypeEnum buffTypeEnum, String buffName, Long value, Integer round) {
        this(buffTypeEnum, buffName, value, 0F, round, 1F, new ArrayList<>());
    }

    public GiveBuffDTO(BuffTypeEnum buffTypeEnum, String buffName, Float rate, Integer round) {
        this(buffTypeEnum, buffName, 0L, rate, round, 1F, new ArrayList<>());
    }

    public GiveBuffDTO(BuffTypeEnum buffTypeEnum, String buffName, Long value, Float rate, Integer round) {
        this(buffTypeEnum, buffName, value, rate, round, 1F, new ArrayList<>());
    }

    public GiveBuffDTO(BuffTypeEnum buffTypeEnum, String buffName, Long value, Integer round, Float effectedRate) {
        this(buffTypeEnum, buffName, value, 0F, round, effectedRate, new ArrayList<>());
    }

    public GiveBuffDTO(BuffTypeEnum buffTypeEnum, String buffName, Float rate, Integer round, Float effectedRate) {
        this(buffTypeEnum, buffName, 0L, rate, round, effectedRate, new ArrayList<>());
    }

    public GiveBuffDTO(BuffTypeEnum buffTypeEnum, String buffName, Long value, Float rate,
                       Integer round, Float effectedRate, List<BuffLimit> buffLimitList) {
        this(buffTypeEnum, buffTypeEnum, buffName, value, rate, round, effectedRate, buffLimitList);
    }

    public GiveBuffDTO(BuffTypeEnum buffType, BuffTypeEnum dependsBuffType, String buffName,
                       Long value, Float rate, Integer round, Float effectedRate, List<BuffLimit> buffLimitList) {
        if (effectedRate < Constants.MIN_RATE || effectedRate > Constants.MAX_RATE) {
            throw new IllegalArgumentException("buff【" + buffName + "】生效概率配置错误");
        }
        this.buffType = buffType;
        this.dependsBuffType = dependsBuffType;
        this.buffName = buffName;
        this.value = value;
        this.rate = rate;
        this.round = round;
        this.effectedRate = effectedRate;
        this.buffLimit = buffLimitList;
    }

    @Data
    public static class BuffLimit {
        /**
         * 上限类型
         */
        private BuffTypeEnum buffTypeEnum;
        /**
         * 上限值
         */
        private Long limitValue;
        /**
         * 上限比例
         */
        private Float limitRate;
    }
}
