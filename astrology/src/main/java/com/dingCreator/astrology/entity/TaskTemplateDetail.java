package com.dingCreator.astrology.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dingCreator.astrology.enums.task.TaskTargetTypeEnum;
import lombok.Data;

/**
 * @author ding
 * @date 2024/8/27
 */
@Data
@TableName("astrology_task_template_detail")
public class TaskTemplateDetail {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 任务模板ID
     */
    @TableField("task_template_id")
    private Long taskTemplateId;
    /**
     * 任务目标
     * @see TaskTargetTypeEnum
     */
    @TableField("target_type")
    private String targetType;
    /**
     * 目标ID
     */
    @TableField("target_id")
    private Long targetId;
    /**
     * 目标数量
     */
    @TableField("target_cnt")
    private Integer targetCnt;
    /**
     * 优先级
     */
    @TableField("priority")
    private Integer priority;
}
