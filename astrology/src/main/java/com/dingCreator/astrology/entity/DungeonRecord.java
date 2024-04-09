package com.dingCreator.astrology.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author ding
 * @date 2024/4/7
 */
@Data
public class DungeonRecord {
    /**
     * ID
     */
    private Long id;
    /**
     * 副本ID
     */
    private Long dungeonId;
    /**
     * 玩家ID
     */
    private Long playerId;
    /**
     * 最近一次探索时间
     */
    private Date lastExploreTime;
}
