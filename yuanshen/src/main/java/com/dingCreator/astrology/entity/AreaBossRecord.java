package com.dingCreator.yuanshen.entity;

import lombok.Data;

/**
 * 玩家地图解锁记录（即战胜该区域boss）
 *
 * @author ding
 * @date 2024/2/1
 */
@Data
public class AreaBossRecord {
    /**
     * 主键ID
     */
    private Long id;
    /**
     * 玩家ID
     */
    private Long playerId;
    /**
     * 地图ID
     */
    private Long mapId;
}
