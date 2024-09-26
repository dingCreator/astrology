package com.dingCreator.astrology.util.context;

import com.dingCreator.astrology.dto.organism.player.PlayerInfoDTO;
import com.dingCreator.astrology.dto.task.TaskScheduleTitleDTO;
import com.dingCreator.astrology.dto.task.TaskTemplateTitleDTO;
import com.dingCreator.astrology.entity.TaskTemplateTitle;
import lombok.Data;

import java.util.List;

/**
 * @author ding
 * @date 2024/9/24
 */
@Data
public class TaskContext {
    /**
     * 玩家信息
     */
    protected PlayerInfoDTO playerInfo;
    /**
     * 玩家小队信息
     */
    protected List<PlayerInfoDTO> teamPlayerInfo;
    /**
     * 任务执行记录
     */
    private List<TaskScheduleTitleDTO> scheduleList;
    /**
     * 任务标题
     */
    private TaskTemplateTitleDTO title;
}
