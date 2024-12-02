package com.dingCreator.astrology.dto.task;

import com.dingCreator.astrology.enums.task.TaskScheduleEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author ding
 * @date 2024/9/23
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskScheduleTitleDTO {
    /**
     * 玩家ID
     */
    private Long playerId;
    /**
     * 任务标题id
     */
    private Long taskTemplateTitleId;
    /**
     * 标题
     */
    private String title;
    /**
     * 描述
     */
    private String description;
    /**
     * 进度
     */
    private TaskScheduleEnum taskScheduleEnum;
    /**
     *
     */
    private List<TaskScheduleDTO> scheduleList;
}
