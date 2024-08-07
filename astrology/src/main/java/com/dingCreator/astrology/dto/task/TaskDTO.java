package com.dingCreator.astrology.dto.task;

import com.dingCreator.astrology.enums.task.TaskTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author ding
 * @date 2024/7/24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDTO {
    /**
     * 任务名称
     */
    private String taskName;
    /**
     * 任务描述
     */
    private String desc;
    /**
     * 任务类型
     */
    private TaskTypeEnum taskTypeEnum;
    /**
     * 子任务
     */
    private List<TaskDTO> children;
}