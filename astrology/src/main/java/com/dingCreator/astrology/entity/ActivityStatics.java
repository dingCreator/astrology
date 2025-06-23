package com.dingCreator.astrology.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ding
 * @date 2024/11/26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("astrology_activity_statics")
public class ActivityStatics {

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 活动ID
     */
    @TableField("activity_id")
    private Long activityId;

    /**
     * 玩家ID
     */
    @TableField("player_id")
    private Long playerId;

    /**
     * 统计时间节点
     */
    @TableField("date_time")
    private String dateTime;

    /**
     * 次数
     */
    @TableField("`count`")
    private Integer count;

    public static final String ACTIVITY_ID = "activity_id";

    public static final String PLAYER_ID = "player_id";

    public static final String DATE_TIME = "date_time";
}

