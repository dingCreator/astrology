package com.dingCreator.astrology.enums.activity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author ding
 * @date 2025/7/4
 */
@Getter
@AllArgsConstructor
public enum SignAwardTypeEnum {
    SINGLE("single", "单次签到"),
    CONTINUOUS("continuous", "连续签到"),
    ;
    private final String typeCode;
    private final String chnDesc;

    public static SignAwardTypeEnum getByTypeCode(String typeCode) {
        return Arrays.stream(values()).filter(e -> e.getTypeCode().equals(typeCode)).findFirst().orElse(null);
    }
}
