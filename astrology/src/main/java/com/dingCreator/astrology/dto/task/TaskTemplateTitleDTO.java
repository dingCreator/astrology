package com.dingCreator.astrology.dto.task;

import com.dingCreator.astrology.dto.LootDTO;
import com.dingCreator.astrology.enums.task.TaskTypeEnum;
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
public class TaskTemplateTitleDTO {
    /**
     * 主键
     */
    private Long id;
    /**
     * 任务标题
     */
    private String name;
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
     * 是否允许多次接取
     */
    private Boolean allowRepeatableReceive;
    /**
     * 掉落物
     */
    private LootDTO lootDTO;
    /**
     * 子任务
     */
    private List<TaskTemplateDTO> templateList;
}
