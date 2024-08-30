package com.dingCreator.astrology.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author ding
 * @date 2024/8/27
 */
@Data
@TableName("astrology_task_schedule_detail")
public class TaskScheduleDetail {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 任务ID
     */
    @TableField("task_id")
    private Long taskId;
    /**
     * 任务目标ID
     */
    @TableField("target_id")
    private Long targetId;
    /**
     * 任务目标数量
     */
    @TableField("target_cnt")
    private Integer targetCnt;
    /**
     * 已完成数量
     */
    @TableField("complete_cnt")
    private Integer completeCnt;

    public TaskScheduleDetail(Long taskId, Long targetId, Integer targetCnt) {
        this.taskId = taskId;
        this.targetId = targetId;
        this.targetCnt = targetCnt;
        this.completeCnt = 0;
    }
}
