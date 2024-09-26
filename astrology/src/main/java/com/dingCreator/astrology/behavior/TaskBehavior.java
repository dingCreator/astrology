package com.dingCreator.astrology.behavior;

import com.dingCreator.astrology.util.TaskUtil;
import com.dingCreator.astrology.vo.PeakTaskVO;

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
