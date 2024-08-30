package com.dingCreator.astrology.dto.task;

import com.dingCreator.astrology.dto.ArticleItemDTO;
import com.dingCreator.astrology.dto.LootDTO;
import com.dingCreator.astrology.enums.task.TaskTargetTypeEnum;
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
public class TaskBaseDTO {
    /**
     * 模板ID
     */
    private Long id;
    /**
     * 任务名称
     */
    private String taskName;
    /**
     * 任务描述
     */
    private String description;
    /**
     * 任务类型
     */
    private TaskTypeEnum taskTypeEnum;
    /**
     * 可进行任务的地图
     */
    private List<Long> limitMapId;
    /**
     * 任务内容
     */
    private List<TaskDetailDTO> details;
    /**
     * 子任务
     */
    private List<TaskBaseDTO> children;
    /**
     * 子任务是否必须按顺序完成
     */
    private Boolean childrenSort;
    /**
     * 是否与其他任务互斥
     */
    private Boolean mutualExclusion;
    /**
     * 是否允许组队
     */
    private Boolean allowTeam;
    /**
     * 任务奖励
     */
    private LootDTO lootDTO;
}