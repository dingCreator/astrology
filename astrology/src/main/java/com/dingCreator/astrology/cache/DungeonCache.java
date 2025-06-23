package com.dingCreator.astrology.cache;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DungeonCache {

    /**
     * 玩家上一次探索副本时间
     * 玩家id -> 副本id -> 上一次探索时间
     */
    private static final Map<Long, Map<Long, LocalDateTime>> LAST_EXPLORE_TIME_MAP = new HashMap<>(512);

    public static void putLastExploreTime(Long playerId, Long dungeonId, LocalDateTime time) {
        LAST_EXPLORE_TIME_MAP.computeIfAbsent(playerId, k -> new HashMap<>(16)).put(dungeonId, time);
    }

    public static LocalDateTime getLastExploreTime(Long playerId, Long dungeonId) {
        return LAST_EXPLORE_TIME_MAP.getOrDefault(playerId, new HashMap<>(16)).get(dungeonId);
    }
}
