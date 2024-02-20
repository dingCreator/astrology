package com.dingCreator.astrology.entity;

import lombok.Data;

/**
 * 刷怪副本
 *
 * @author ding
 * @date 2024/2/3
 */
@Data
public class Dungeon {
    /**
     * 主键
     */
    private Long id;
    /**
     * 地图ID
     */
    private Long mapId;
    /**
     * 可挑战此副本的最高Rank
     */
    private Long maxRank;
}
