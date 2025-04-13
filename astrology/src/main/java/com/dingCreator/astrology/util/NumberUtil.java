package com.dingCreator.astrology.util;

import com.dingCreator.astrology.enums.exception.SysExceptionEnum;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author ding
 * @date 2025/1/9
 */
public class NumberUtil {

    public static Long multiply(long longVal, float floatVal) {
        return multiply(longVal, new BigDecimal(String.valueOf(floatVal)));
    }

    public static Long multiply(long longVal, BigDecimal floatVal) {
        return multiply(longVal, floatVal, 0).longValue();
    }

    public static BigDecimal multiply(long longVal, BigDecimal floatVal, int scale) {
        return BigDecimal.valueOf(longVal).multiply(floatVal).setScale(scale, RoundingMode.HALF_UP);
    }

    public static Long str2Long(String str) {
        try {
            return Long.parseLong(str);
        } catch (Exception e) {
            throw SysExceptionEnum.INPUT_FORMAT_ERROR.getException();
        }
    }
}
