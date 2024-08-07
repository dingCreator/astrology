package com.dingCreator.astrology.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author ding
 * @date 2024/7/26
 */
@Data
public class TaskSchedule {
    /**
     * 主键
     */
    private Long id;
    /**
     * 玩家ID
     */
    private Long playerId;
    /**
     * 父任务ID
     */
    private Long parentTaskId;
    /**
     * 任务类型
     *
     * @see com.dingCreator.astrology.enums.task.TaskTypeEnum
     */
    private String taskType;
    /**
     * 任务进度
     *
     * @see com.dingCreator.astrology.enums.task.TaskScheduleEnum
     */
    private String taskSchedule;
    /**
     * 任务目标数量
     */
    private Integer target;
    /**
     * 已完成数量
     */
    private Integer complete;
    /**
     * 状态更新时间
     */
    private Date updateTime;
}
