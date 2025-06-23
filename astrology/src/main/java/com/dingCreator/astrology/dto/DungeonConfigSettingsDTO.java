package com.dingCreator.astrology.dto;

import com.dingCreator.astrology.dto.loot.LootDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DungeonConfigSettingsDTO {
    /**
     * 通关奖励
     */
    private LootDTO dungeonLoot;
    /**
     * 分层
     */
    private List<DungeonFloor> floorList;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DungeonFloor {
        /**
         * 层数
         */
        private Integer floor;
        /**
         * 层通关奖励
         */
        private LootDTO floorLoot;
        /**
         * 波次
         */
        private List<DungeonWave> waveList;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DungeonWave {
        /**
         * 波数
         */
        private Integer wave;
        /**
         * 波次通关奖励
         */
        private LootDTO waveLoot;
        /**
         * 怪物ID
         */
        private List<Long> monsterIds;
    }
}
