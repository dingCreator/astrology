package com.dingCreator.astrology.vo;

import com.dingCreator.astrology.entity.Loot;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
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
    private Map<Long, LootVO> playerLootMap;

    public DungeonResultVO() {
        playerLootMap = new HashMap<>();
    }

    public void addLoot(Map<Long, LootVO> lootVOMap) {
        lootVOMap.forEach(this::addLoot);
    }

    public void addLoot(Long id, LootVO vo) {
        LootVO exist = playerLootMap.getOrDefault(id, new LootVO());
        exist.setMoney(exist.getMoney() + vo.getMoney());
        exist.setExp(exist.getExp() + vo.getExp());
        exist.getLootItemNameList().addAll(vo.getLootItemNameList());
    }
}
