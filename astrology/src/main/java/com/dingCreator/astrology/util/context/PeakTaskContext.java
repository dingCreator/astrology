package com.dingCreator.astrology.util.context;

import com.dingCreator.astrology.dto.task.PeakTaskDTO;
import lombok.Data;

/**
 * @author ding
 * @date 2024/9/23
 */
@Data
public class PeakTaskContext extends TaskContext {
    /**
     * 巅峰任务
     */
    private PeakTaskDTO peakTaskDTO;

    public static PeakTaskContext of(TaskContext context) {
        PeakTaskContext peakTaskContext = new PeakTaskContext();
        peakTaskContext.setTeamPlayerInfo(context.getTeamPlayerInfo());
        peakTaskContext.setPlayerInfo(context.getPlayerInfo());
        peakTaskContext.setScheduleList(context.getScheduleList());
        return peakTaskContext;
    }
}
