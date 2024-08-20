package com.dingCreator.astrology.behavior;

import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.dto.organism.player.PlayerDTO;
import com.dingCreator.astrology.enums.RankEnum;
import com.dingCreator.astrology.enums.job.JobEnum;
import com.dingCreator.astrology.enums.task.PeakTaskEnum;
import com.dingCreator.astrology.vo.PeakTaskVO;

/**
 * @author ding
 * @date 2024/7/26
 */
public class TaskBehavior {

    public PeakTaskVO startPeakTask(Long playerId) {
        PlayerDTO playerDTO = PlayerCache.getPlayerById(playerId).getPlayerDTO();
        PeakTaskEnum peakTaskEnum = PeakTaskEnum.getPeakTask(JobEnum.getByCode(playerDTO.getJob()),
                RankEnum.getEnum(playerDTO.getJob(), playerDTO.getRank()));


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
