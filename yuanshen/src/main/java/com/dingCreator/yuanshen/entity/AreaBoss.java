package com.dingCreator.yuanshen.entity;

import lombok.Data;

/**
 * 区域Boss
 *
 * @author ding
 * @date 2024/2/2
 */
@Data
public class AreaBoss {
    /**
     * 主键
     */
    private Long id;
    /**
     * 所属地图ID
     */
    private Long mapId;
    /**
     * boss的ID
     */
    private Long bossId;
}
