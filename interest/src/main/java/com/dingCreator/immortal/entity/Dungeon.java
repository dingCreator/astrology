package com.dingCreator.immortal.entity;

import lombok.Data;

import java.util.List;

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
    /**
     * 副本boss
     */
    private List<Long> bossId;
}
