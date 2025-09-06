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
     * 副本怪物波
     */
    DUNGEON_WAVE("DungeonWave"),
    /**
     * 副本怪物层
     */
    DUNGEON_FLOOR("DungeonFloor"),
    /**
     * 直接通关副本
     */
    DUNGEON_STRAIGHT_PASS("DungeonStraightPass"),
    /**
     * 世界boss
     */
    WORLD_BOSS("WorldBoss"),
    /**
     * 任务标题（相当于完成整个任务）
     */
    TASK_TITLE("TaskTitle"),
    /**
     * 任务
     */
    TASK("Task"),
    ;

    private final String belongTo;
}
