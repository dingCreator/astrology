package com.dingCreator.astrology.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 副本boss配置
 *
 * @author ding
 * @date 2024/2/1
 */
@Data
@TableName("astrology_dungeon_config")
public class DungeonConfig {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 所属副本ID
     */
    @TableField("dungeon_id")
    private Long dungeonId;
    /**
     * 层数
     */
    @TableField("floor")
    private Integer floor;
    /**
     * 波数
     */
    @TableField("wave")
    private Integer wave;
    /**
     * 怪物ID
     */
    @TableField("monster_id")
    private Long monsterId;
    /**
     * 数量
     */
    @TableField("cnt")
    private Integer cnt;

    public static final String DUNGEON_ID = "dungeon_id";

    public static final String FLOOR = "floor";

    public static final String WAVE = "wave";
}
