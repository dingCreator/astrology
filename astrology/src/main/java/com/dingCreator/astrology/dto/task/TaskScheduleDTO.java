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
public class TaskScheduleDTO {
    /**
     * 玩家ID
     */
    private Long playerId;
    /**
     * 任务模板id
     */
    private Long taskTemplateId;
    /**
     * 描述
     */
    private String description;
    /**
     * 进度
     */
    private TaskScheduleEnum taskScheduleEnum;
    /**
     * 详情
     */
    private List<TaskScheduleDetailDTO> detailList;
}
