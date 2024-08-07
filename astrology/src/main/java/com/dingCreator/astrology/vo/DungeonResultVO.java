package com.dingCreator.astrology.vo;

import com.dingCreator.astrology.entity.Loot;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author ding
 * @date 2024/4/7
 */
@Data
public class DungeonResultVO implements Serializable {
    /**
     * 探索结果
     */
    private BattleResultVO.BattleResult exploreResult;
    /**
     * 获得的掉落物汇总
     * playerId : LootVO
     */
    private Map<Long, LootVO> lootMap;
    /**
     * 副本boss挑战详情
     */
    private List<DungeonBossResult> dungeonBossResultList;

    @Data
    public static class DungeonBossResult implements Serializable {
        /**
         * 战斗信息
         */
        private BattleResultVO battleResultVO;
        /**
         * boss掉落物
         */
        private Loot loot;
    }
}
