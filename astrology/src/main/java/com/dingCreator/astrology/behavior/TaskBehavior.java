package com.dingCreator.astrology.behavior;

import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.dto.organism.player.PlayerDTO;
import com.dingCreator.astrology.dto.task.PeakTaskDTO;
import com.dingCreator.astrology.dto.task.TaskBaseDTO;
import com.dingCreator.astrology.enums.RankEnum;
import com.dingCreator.astrology.enums.exception.TaskExceptionEnum;
import com.dingCreator.astrology.enums.job.JobEnum;
import com.dingCreator.astrology.enums.task.PeakTaskEnum;
import com.dingCreator.astrology.service.PeakTaskTemplateService;
import com.dingCreator.astrology.util.TaskUtil;
import com.dingCreator.astrology.vo.PeakTaskVO;

import java.util.Objects;

/**
 * @author ding
 * @date 2024/7/26
 */
public class TaskBehavior {

    public PeakTaskVO startPeakTask(Long playerId) {
        TaskUtil.receivePeakTask(playerId);

        return new PeakTaskVO();
    }



    private static class Holder {
        private static final TaskBehavior BEHAVIOR = new TaskBehavior();
    }

    private TaskBehavior() {

    }

    public static TaskBehavior getInstance() {
        return TaskBehavior.Holder.BEHAVIOR;
    }
}
