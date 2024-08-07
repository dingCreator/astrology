package com.dingCreator.astrology.enums.task;

import com.dingCreator.astrology.enums.RankEnum;
import com.dingCreator.astrology.enums.exception.TaskExceptionEnum;
import com.dingCreator.astrology.enums.job.JobEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ding
 * @date 2024/7/24
 */
@Getter
@AllArgsConstructor
public enum PeakTaskEnum {
    /**
     * 巅峰任务-星术师
     */
    MAGICIAN_RANK_1("夺得圣城.圣星武道大会魁首", JobEnum.MAGICIAN, RankEnum.MAGICIAN_RANK_1,
            "前往圣城圣星武道大会副本参加圣星武道大会，在副本内被击败或放弃巅峰任务时自动退出副本，并视为巅峰任务失败\n" +
                    "圣星武道大会共经过三轮比赛\n" +
                    "任务1：击败首轮对手莫子锋\n" +
                    "击败奖励： 莫子锋\n" +
                    "任务2：击败次轮对手居缈音\n" +
                    "击败奖励： 居缈音\n" +
                    "任务3：击败终轮对手嬴天麟，夺得魁首\n" +
                    "击败奖励：称号——巅峰.圣星之子",
            Arrays.asList(
                    TaskTypeEnum.BATTLE,
                    TaskTypeEnum.BATTLE,
                    TaskTypeEnum.BATTLE
            )
    ),
    ;
    /**
     * 巅峰任务名称
     */
    private final String taskName;
    /**
     * 职业
     */
    private final JobEnum jobEnum;
    /**
     * 阶级
     */
    private final RankEnum rankEnum;
    /**
     * 任务描述
     */
    private final String desc;
    /**
     * 阶段
     */
    private final List<TaskTypeEnum> stepList;

    /**
     * 构建任务Map
     */
    private static final Map<JobEnum, Map<RankEnum, PeakTaskEnum>> PEAK_TASK_MAP;

    static {
        PEAK_TASK_MAP = Arrays.stream(PeakTaskEnum.values()).collect(Collectors.toMap(PeakTaskEnum::getJobEnum, task -> {
            Map<RankEnum, PeakTaskEnum> map = new HashMap<>();
            map.put(task.getRankEnum(), task);
            return map;
        }, (oldVal, newVal) -> {
            oldVal.putAll(newVal);
            return oldVal;
        }));
    }

    /**
     * 获取巅峰任务
     *
     * @param jobEnum  职业枚举
     * @param rankEnum 阶级枚举
     * @return 巅峰任务
     */
    public static PeakTaskEnum getPeakTask(JobEnum jobEnum, RankEnum rankEnum) {
        Map<RankEnum, PeakTaskEnum> peakTaskRankEnum = PEAK_TASK_MAP.get(jobEnum);
        if (Objects.isNull(peakTaskRankEnum)) {
            throw TaskExceptionEnum.PEAK_TASK_NOT_EXIST.getException();
        }
        PeakTaskEnum peakTaskEnum = peakTaskRankEnum.get(rankEnum);
        if (Objects.isNull(peakTaskEnum)) {
            throw TaskExceptionEnum.PEAK_TASK_NOT_EXIST.getException();
        }
        return peakTaskEnum;
    }
}
