package com.dingCreator.astrology.util;

/**
 * @author ding
 * @date 2024/3/25
 */
public class RankUtil {

    /**
     * 阶级压制
     *
     * @param fromRank 阶级
     * @param toRank   对方阶级
     * @return 压制后数值
     */
    public static float getRankSuppression(long fromRank, long toRank) {
        if (fromRank < toRank) {
            return -Double.valueOf(Math.pow(2, (toRank - fromRank - 1)) * 0.1).floatValue();
        }
        return 0;
    }
}
