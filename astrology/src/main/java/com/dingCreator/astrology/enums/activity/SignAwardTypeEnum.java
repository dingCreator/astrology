package com.dingCreator.astrology.enums.activity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author ding
 * @date 2025/7/4
 */
@Getter
@AllArgsConstructor
public enum SignAwardTypeEnum {
    NORMAL("normal", "普通"),
    ;
    private final String typeCode;
    private final String chnDesc;
}
