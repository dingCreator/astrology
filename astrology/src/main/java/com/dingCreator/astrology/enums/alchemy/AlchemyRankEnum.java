package com.dingCreator.astrology.enums.alchemy;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author ding
 * @date 2025/3/20
 */
@Getter
@AllArgsConstructor
public enum AlchemyRankEnum {
    STUDENT(0, "炼药学徒",
            0.25F, 0F, 0F,
            0F, 0F, 0F
    ),
    RANK_1(1, "一阶炼药师",
            0.6F, 0.2F, 0F,
            0F, 0F, 0F
    ),
    RANK_2(2, "二阶炼药师",
            0.95F, 0.55F, 0.15F,
            0.01F, 0F, 0F
    ),
    RANK_3(3, "三阶炼药师",
            1F, 0.9F, 0.5F,
            0.1F, 0.01F, 0F
    ),
    RANK_4(4, "四阶炼药师",
            1F, 1F, 0.85F,
            0.45F, 0.1F, 0F
    ),
    RANK_5(5, "五阶炼药师",
            1F, 1F, 1F,
            0.8F, 0.4F, 0.1F
    ),
    ;

    /**
     * 炼药师等级
     */
    private final Integer rank;
    /**
     * 炼药师等级名称
     */
    private final String rankName;
    /**
     * 炼制1阶丹药成功率
     */
    private final Float pillRank1SuccessRate;
    /**
     * 炼制2阶丹药成功率
     */
    private final Float pillRank2SuccessRate;
    /**
     * 炼制3阶丹药成功率
     */
    private final Float pillRank3SuccessRate;
    /**
     * 炼制4阶丹药成功率
     */
    private final Float pillRank4SuccessRate;
    /**
     * 炼制5阶丹药成功率
     */
    private final Float pillRank5SuccessRate;
    /**
     * 炼制帝丹成功率
     */
    private final Float pillRank99SuccessRate;

    public static AlchemyRankEnum getByRank(int rank) {
        return Arrays.stream(values()).filter(e -> e.getRank() == rank).findFirst().orElse(null);
    }

    public float getSuccessRate(int pillRank) {
        switch (pillRank) {
            case 1:
                return pillRank1SuccessRate;
            case 2:
                return pillRank2SuccessRate;
            case 3:
                return pillRank3SuccessRate;
            case 4:
                return pillRank4SuccessRate;
            case 5:
                return pillRank5SuccessRate;
            case 99:
                return pillRank99SuccessRate;
            default:
                return 0;
        }
    }
}
