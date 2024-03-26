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
     * @param val      压制前数值
     * @return 压制后数值
     */
    public static long getRankSupression(long fromRank, long toRank, long val) {
        if (fromRank < toRank) {
            val = Math.round(val * (1 + (fromRank - toRank) * 0.1F));
        }
        return val;
    }
}
