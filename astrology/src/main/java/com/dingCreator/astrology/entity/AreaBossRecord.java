package com.dingCreator.astrology.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 玩家地图解锁记录（即战胜该区域boss）
 *
 * @author ding
 * @date 2024/2/1
 */
@Data
@TableName("astrology_area_boss_record")
public class AreaBossRecord {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 玩家ID
     */
    @TableField("player_id")
    private Long playerId;
    /**
     * 地图ID
     */
    @TableField("map_id")
    private Long mapId;
}
