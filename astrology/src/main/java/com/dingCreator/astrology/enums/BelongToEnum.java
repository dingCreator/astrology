package com.dingCreator.astrology.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author ding
 * @date 2024/3/26
 */
@Getter
@AllArgsConstructor
public enum BelongToEnum {
    /**
     * 归属
     */
    Player("Player"),

    ;

    private final String belongTo;
}
