package com.dingCreator.astrology.dto.task;

import com.dingCreator.astrology.enums.task.TaskScheduleEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ding
 * @date 2024/9/23
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskScheduleDetailDTO {
    /**
     * 主键
     */
    private Long id;
    /**
     * 玩家ID
     */
    private Long playerId;
    /**
     * 任务模板标题id
     */
    private Long taskTitleId;
    /**
     * 任务模板ID
     */
    private Long taskTemplateId;
    /**
     * 任务模板详情ID
     */
    private Long taskTemplateDetailId;
    /**
     * 任务目标ID
     */
    private Long targetId;
    /**
     * 任务目标数量
     */
    private Integer targetCnt;
    /**
     * 已完成数量
     */
    private Integer completeCnt;
    /**
     * 任务进度
     */
    private TaskScheduleEnum taskScheduleType;
}
