package com.dingCreator.astrology.entity;

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
@TableName("dungeon_boss")
public class DungeonBoss {
    /**
     * 主键
     */
    @TableId("id")
    private Long id;
    /**
     * 所属副本ID
     */
    private Long dungeonId;
    /**
     * boss的ID
     */
    private Long monsterId;
    /**
     * 掉落物
     */
    private String loot;

    public static final String DUNGEON_ID = "dungeon_id";
}
