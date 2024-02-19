package com.dingCreator.immortal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author ding
 * @date 2024/2/4
 */
@Getter
@AllArgsConstructor
public enum OrganismEnum {
    /**
     * 生物类型
     */
    PLAYER("PLAYER"),
    AREA_BOSS("AREA_BOSS"),
    RANK_UP_BOSS("RANK_UP_BOSS"),
    DUNGEON_BOSS("DUNGEON_BOSS"),
    ;
    private final String type;
}
