package com.dingCreator.astrology.enums.alchemy;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author ding
 * @date 2025/3/18
 */
@Getter
@AllArgsConstructor
public enum PillQualityRankEnum {
    NORMAL(1, "凡品"),
    MIDDLE(2, "中品"),
    EXTREME(3, "极品"),
    STAR(4, "星品"),
    ;
    private final Integer rank;
    private final String rankDesc;

    public static PillQualityRankEnum getByRank(int rank) {
        return Arrays.stream(values()).filter(e -> rank == e.getRank()).findFirst().orElse(null);
    }

    public static PillQualityRankEnum getByDesc(String desc) {
        return Arrays.stream(values()).filter(e -> e.getRankDesc().equals(desc)).findFirst().orElse(null);
    }
}
