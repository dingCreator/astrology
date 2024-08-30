package com.dingCreator.astrology.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author ding
 * @date 2024/4/10
 */
@Getter
@AllArgsConstructor
public enum LootBelongToEnum {
    /**
     * 副本
     */
    DUNGEON("Dungeon"),
    /**
     * 副本怪物
     */
    DUNGEON_BOSS("DungeonBoss"),
    /**
     * 守关boss
     */
    MAP_BOSS("MapBoss"),
    /**
     * 世界boss
     */
    WORLD_BOSS("WorldBoss"),
    /**
     * 任务
     */
    TASK("task"),
    ;

    private final String belongTo;
}
