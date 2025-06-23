package com.dingCreator.astrology.vo;

import com.dingCreator.astrology.entity.Loot;
import com.dingCreator.astrology.enums.AssetTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author ding
 * @date 2024/4/7
 */
@Data
public class DungeonResultVO implements Serializable {
    /**
     * 探索结果
     */
    private ExploreResult exploreResult;
    /**
     * 获得的掉落物汇总
     * playerId : LootVO
     */
    private Map<Long, LootVO> playerLootMap;
    /**
     * 已完成的层数
     */
    private Integer completeFloor;
    /**
     * 最大层数
     */
    private Integer maxFloor;

    public DungeonResultVO() {
        playerLootMap = new HashMap<>();
    }

    public void addLoot(Map<Long, LootVO> lootVOMap) {
        lootVOMap.forEach(this::addLoot);
    }

    public void addLoot(Long id, LootVO vo) {
        LootVO exist = playerLootMap.getOrDefault(id, new LootVO());
        Map<AssetTypeEnum, Long> assetMap = Objects.isNull(exist.getAssetMap()) ? new HashMap<>() : exist.getAssetMap();
        vo.getAssetMap().forEach((k, v) -> assetMap.merge(k, v, Long::sum));
        exist.setExp(exist.getExp() + vo.getExp());
        exist.getItemVOList().addAll(vo.getItemVOList());
    }

    @Getter
    @AllArgsConstructor
    public enum ExploreResult {
        /**
         * 直接通过
         */
        PASS("探索时发现了一处密道，直接通关"),
        /**
         * 通过该层
         */
        COMPLETE("探索成功，并发现了继续前进的道路"),
        /**
         * 通关
         */
        SUCCESS("探索圆满结束"),
        /**
         * 失败
         */
        FAIL("探索失败，尝试增强实力后再来探索吧"),
        ;

        private final String msg;
    }
}
