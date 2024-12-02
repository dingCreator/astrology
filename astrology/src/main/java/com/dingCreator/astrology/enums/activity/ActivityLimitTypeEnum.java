package com.dingCreator.astrology.enums.activity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author ding
 * @date 2024/11/28
 */
@Getter
@AllArgsConstructor
public enum ActivityLimitTypeEnum {
    /**
     * 参与限制类型
     */
    MINUTE(1, "分钟"),
    HOUR(2, "小时"),
    DAY(3, "天"),
    MONTH(4, "月"),
    YEAR(5, "年"),
    ALL(6, "总"),
    ;

    private final Integer code;

    private final String chnDesc;

    public static ActivityLimitTypeEnum getByCode(Integer code) {
        return Arrays.stream(values()).filter(e -> e.getCode().equals(code)).findFirst().orElse(null);
    }
}
