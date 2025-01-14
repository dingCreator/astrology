package com.dingCreator.astrology.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author ding
 * @date 2025/1/9
 */
public class BigDecimalUtil {

    public static Long multiply(long longVal, float floatVal) {
        return multiply(longVal, new BigDecimal(String.valueOf(floatVal)));
    }

    public static Long multiply(long longVal, BigDecimal floatVal) {
        return multiply(longVal, floatVal, 0).longValue();
    }

    public static BigDecimal multiply(long longVal, BigDecimal floatVal, int scale) {
        return BigDecimal.valueOf(longVal).multiply(floatVal).setScale(scale, RoundingMode.HALF_UP);
    }
}
