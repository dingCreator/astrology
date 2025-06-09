package com.dingCreator.astrology.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 世界boss挑战记录
 *
 * @author ding
 * @date 2024/2/20
 */
@Data
@TableName("astrology_world_boss_record")
public class WorldBossRecord {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 世界boss配置ID
     */
    @TableField("world_boss_id")
    private Long worldBossId;
    /**
     * 玩家ID |1|2|3|
     */
    @TableField("player_id")
    private Long playerId;
    /**
     * 伤害量
     */
    @TableField("damage")
    private Long damage;
    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    public static final String WORLD_BOSS_ID = "world_boss_id";

    public static final String CREATE_TIME = "create_time";

    public static final String PLAYER_ID = "player_id";

    public static final String DAMAGE = "damage";
}
