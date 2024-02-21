package com.dingCreator.astrology.behavior;

import java.util.List;

/**
 * @author ding
 * @date 2024/2/21
 */
public class WorldBossBehavior {

    private static class Holder {
        private static final WorldBossBehavior BEHAVIOR = new WorldBossBehavior();
    }

    private WorldBossBehavior() {

    }

    public static WorldBossBehavior getInstance() {
        return WorldBossBehavior.Holder.BEHAVIOR;
    }

    /**
     * 讨伐世界boss
     *
     * @param playerId 玩家ID
     */
    public void attackWorldBoss(Long playerId) {

    }

    /**
     * 查询排名
     *
     * @return 当前排名
     */
    public List<?> worldBossRank() {

        return null;
    }
}
