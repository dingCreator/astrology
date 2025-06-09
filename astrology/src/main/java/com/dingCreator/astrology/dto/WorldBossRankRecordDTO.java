package com.dingCreator.astrology.dto;

import lombok.Data;

/**
 * @author ding
 * @date 2025/5/28
 */
@Data
public class WorldBossRankRecordDTO {

    private Long id;
    /**
     * 伤害排名
     */
    private Integer damageRank;
    /**
     * 玩家ID
     */
    private Long playerId;
    /**
     * 伤害量
     */
    private Long damage;
    /**
     * 世界boss配置ID
     */
    private Long worldBossId;
}
