package com.dingCreator.immortal.entity;

import lombok.Data;

import java.util.List;

/**
 * 地图
 *
 * @author ding
 * @date 2024/1/22
 */
@Data
public class Map {
    /**
     * ID
     */
    private Long id;
    /**
     * 名称
     */
    private String name;
    /**
     * x坐标
     */
    private Long xPos;
    /**
     * y坐标
     */
    private Long yPos;
    /**
     * boss
     */
    private List<Long> bossIds;
}