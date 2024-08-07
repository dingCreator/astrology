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
    Dungeon("Dungeon"),
    /**
     * 副本怪物
     */
    DungeonBoss("DungeonBoss"),
    /**
     * 守关boss
     */
    MapBoss("MapBoss"),
    /**
     * 世界boss
     */
    WorldBoss("WorldBoss"),
    ;

    private final String belongTo;
}
