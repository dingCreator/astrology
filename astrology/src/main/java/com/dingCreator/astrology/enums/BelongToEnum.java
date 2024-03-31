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
     * 玩家
     */
    Player("Player"),
    /**
     * 怪物
     */
    Monster("Monster"),

    ;

    private final String belongTo;
}
