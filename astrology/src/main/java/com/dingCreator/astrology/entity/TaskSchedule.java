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
 * @date 2024/7/26
 */
@Data
@TableName("astrology_task_schedule")
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
     * 任务模板ID
     */
    @TableField("task_template_title_id")
    private Long taskTemplateTitleId;
    /**
     * 任务进度
     *
     * @see com.dingCreator.astrology.enums.task.TaskScheduleEnum
     */
    @TableField("task_schedule")
    private String taskSchedule;

    public static final String ID = "id";
    public static final String PLAYER_ID = "player_id";
    public static final String TASK_TEMPLATE_ID = "task_template_id";
    public static final String TASK_SCHEDULE = "task_schedule";
}
