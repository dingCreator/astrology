package com.dingCreator.astrology.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author ding
 * @date 2024/11/26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("astrology_activity_record")
public class ActivityRecord {

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
     * 连续参与次数
     */
    @TableField("join_times")
    private Integer joinTimes;

    /**
     * 参与时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;
}
