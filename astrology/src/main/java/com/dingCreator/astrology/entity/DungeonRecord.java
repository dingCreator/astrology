package com.dingCreator.astrology.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author ding
 * @date 2024/4/7
 */
@Data
@TableName("dungeon_record")
public class DungeonRecord {
    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 副本ID
     */
    @TableField("dungeon_id")
    private Long dungeonId;
    /**
     * 玩家ID
     */
    @TableField("player_id")
    private Long playerId;
    /**
     * 最近一次探索时间
     */
    @TableField("last_explore_time")
    private Date lastExploreTime;
}
