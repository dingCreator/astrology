package com.dingCreator.astrology.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DungeonExploreStatusEnum {
    /**
     * 探索中
     */
    EXPLORE("EXPLORE"),
    /**
     * 已完成
     */
    COMPLETE("COMPLETE"),
    /**
     * 已失败
     */
    FAIL("FAIL"),
    ;

    private final String code;
}
