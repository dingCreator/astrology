package com.dingCreator.astrology.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

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
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 名称
     */
    @TableField("name")
    private String name;
    /**
     * x坐标
     */
    @TableField("x_pos")
    private Long xPos;
    /**
     * y坐标
     */
    @TableField("y_pos")
    private Long yPos;
}