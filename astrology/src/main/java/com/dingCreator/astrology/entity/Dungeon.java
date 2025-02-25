package com.dingCreator.astrology.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 刷怪副本
 *
 * @author ding
 * @date 2024/2/3
 */
@Data
@TableName("astrology_dungeon")
public class Dungeon {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 名称
     */
    @TableField("name")
    private String name;
    /**
     * 地图ID
     */
    @TableField("map_id")
    private Long mapId;
    /**
     * 可挑战此副本的最高Rank
     */
    @TableField("max_rank")
    private Integer maxRank;
    /**
     * 冷却时间
     */
    @TableField("flush_time")
    private Long flushTime;
    /**
     * 直接通过概率
     */
    @TableField("pass_rate")
    private BigDecimal passRate;
}
