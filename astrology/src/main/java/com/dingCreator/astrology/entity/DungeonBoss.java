package com.dingCreator.astrology.entity;

import lombok.Data;

/**
 * 副本boss配置
 *
 * @author ding
 * @date 2024/2/1
 */
@Data
public class DungeonBoss {
    /**
     * 主键
     */
    private Long id;
    /**
     * 刷新时间，单位：秒
     */
    private Integer flushTime;
    /**
     * 所属副本ID
     */
    private Long dungeonId;
    /**
     * boss的ID
     */
    private Long bossId;
}
