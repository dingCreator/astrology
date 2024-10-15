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
     * 此任务是否允许失败（即失败后是否直接导致任务失败）
     */
    @TableField("allow_failed")
    private Boolean allowFailed;
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
     * 完成任务返回信息
     */
    @TableField("success_msg")
    private String successMsg;
    /**
     * 任务失败返回信息
     */
    @TableField("fail_msg")
    private String failMsg;
}
