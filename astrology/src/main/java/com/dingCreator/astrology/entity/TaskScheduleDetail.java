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
 * @date 2024/8/27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("astrology_task_schedule_detail")
public class TaskScheduleDetail {
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
     * 任务模板标题id
     */
    @TableField("task_title_id")
    private Long taskTitleId;
    /**
     * 任务模板ID
     */
    @TableField("task_template_id")
    private Long taskTemplateId;
    /**
     * 任务模板详情ID
     */
    @TableField("task_template_detail_id")
    private Long taskTemplateDetailId;
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
    /**
     * 任务进度
     */
    @TableField("task_schedule_type")
    private String taskScheduleType;

    public TaskScheduleDetail(Long taskTemplateDetailId, Long targetId, Integer targetCnt) {
        this.taskTemplateDetailId = taskTemplateDetailId;
        this.targetId = targetId;
        this.targetCnt = targetCnt;
        this.completeCnt = 0;
    }

    public static final String PLAYER_ID = "player_id";
}
