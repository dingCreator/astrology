package com.dingCreator.astrology.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 区域Boss
 *
 * @author ding
 * @date 2024/2/2
 */
@Data
@TableName("astrology_area_boss")
public class AreaBoss {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 所属地图ID
     */
    @TableField("map_id")
    private Long mapId;
    /**
     * boss的ID
     */
    @TableField("monster_id")
    private Long monsterId;
}
