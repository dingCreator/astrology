package com.dingCreator.astrology.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author ding
 * @date 2024/7/26
 */
@Data
@TableName("astrology_task_schedule")
public class TaskSchedule {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 玩家ID
     */
    @TableField("player_id")
    private Long playerId;
    /**
     * 父任务ID
     */
    @TableField("parent_task_id")
    private Long parentTaskId;
    /**
     * 任务类型
     *
     * @see com.dingCreator.astrology.enums.task.TaskTypeEnum
     */
    @TableField("task_type")
    private String taskType;
    /**
     * 任务进度
     *
     * @see com.dingCreator.astrology.enums.task.TaskScheduleEnum
     */
    @TableField("task_schedule")
    private String taskSchedule;
    /**
     * 任务目标数量
     */
    @TableField("target")
    private Integer target;
    /**
     * 已完成数量
     */
    @TableField("complete")
    private Integer complete;
    /**
     * 状态更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;
}
