package com.dingCreator.astrology.behavior;

/**
 * @author ding
 * @date 2024/2/20
 */
public class DungeonBehavior {

    private static class Holder {
        private static final DungeonBehavior BEHAVIOR = new DungeonBehavior();
    }

    private DungeonBehavior() {

    }

    public static DungeonBehavior getInstance() {
        return Holder.BEHAVIOR;
    }

    public void joinDungeon(Long dungeonId, Long playerId) {

    }


}
