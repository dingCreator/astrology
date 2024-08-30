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
    PLAYER("Player"),
    /**
     * 怪物
     */
    MONSTER("Monster"),

    ;

    private final String belongTo;
}
