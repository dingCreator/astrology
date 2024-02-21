package com.dingCreator.astrology.entity;

import lombok.Data;

/**
 * 世界boss挑战记录
 *
 * @author ding
 * @date 2024/2/20
 */
@Data
public class WorldBossRecord {
    /**
     * 主键ID
     */
    private Long id;
    /**
     * 玩家ID |1|2|3|
     */
    private String playerIds;
    /**
     * 伤害量
     */
    private Long damage;
    /**
     * 创建时间
     */
    private String createTime;
}
