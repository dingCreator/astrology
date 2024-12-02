package com.dingCreator.astrology.dto.task;

import com.baomidou.mybatisplus.annotation.TableField;
import com.dingCreator.astrology.entity.TaskTemplateDetail;
import com.dingCreator.astrology.enums.task.TaskTargetTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ding
 * @date 2024/8/26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskTemplateDetailDTO {
    /**
     * ID
     */
    private Long id;
    /**
     * 标题ID
     */
    private Long titleId;
    /**
     * 模板id
     */
    private Long templateId;
    /**
     * 目标类型
     */
    private TaskTargetTypeEnum target;
    /**
     * 此任务是否允许失败（即失败后是否直接导致任务失败）
     */
    private Boolean allowFailed;
    /**
     * 目标ID
     */
    private Long targetId;
    /**
     * 数量
     */
    private Integer targetCnt;
    /**
     * 完成任务返回信息
     */
    private String successMsg;
    /**
     * 任务失败返回信息
     */
    private String failMsg;
}
