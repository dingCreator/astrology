package com.dingCreator.yuanshen.util;

import java.util.Random;

/**
 * @author ding
 * @date 2024/2/18
 */
public class RandomUtil {

    private static final Random RAND = new Random();

    /**
     * 将概率标准化到0-1,并保留n位小数
     *
     * @param rate 转化前的概率
     * @param n 小数点后位数
     * @return 转化后的概率
     */
    public static float format(float rate, int n) {
        if (n < 0 || n > 16) {
            throw new IllegalArgumentException("保留位数有误");
        }
        rate = rate > 1 ? 1 : rate;
        rate = rate < 0 ? 0 : rate;
        if (n == 0) {
            return rate;
        }
        float f = 10 * n;
        return Math.round(rate * f) / f;
    }

    /**
     * 是否命中给定概率区间
     *
     * @param rate 概率
     * @return 是否命中该区间
     */
    public static boolean isHit(float rate) {
        return RAND.nextFloat() < rate;
    }
}
