package com.dingCreator.astrology.dto.task;

import com.dingCreator.astrology.dto.loot.LootDTO;
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
public class TaskTemplateDTO {
    /**
     * 模板ID
     */
    private Long id;
    /**
     * 标题ID
     */
    private Long titleId;
    /**
     * 任务描述
     */
    private String description;
    /**
     * 任务奖励
     */
    private LootDTO lootDTO;
    /**
     * 优先级
     */
    private Integer priority;
    /**
     * 任务内容
     */
    private List<TaskTemplateDetailDTO> details;
}