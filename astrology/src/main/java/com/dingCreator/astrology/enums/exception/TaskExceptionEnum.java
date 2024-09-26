package com.dingCreator.astrology.enums.exception;

import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author ding
 * @date 2024/7/27
 */
@Getter
@AllArgsConstructor
public enum TaskExceptionEnum {
    /**
     * 任务异常
     */
    PEAK_TASK_NOT_EXIST(new BusinessException(Constants.TASK_EXCEPTION_PREFIX + "001", "暂无巅峰任务")),
    PEAK_LOW_LEVEL(new BusinessException(Constants.TASK_EXCEPTION_PREFIX + "002", "提升到该阶段的最高等级方可接取巅峰任务")),
    PLAYER_IS_MOVING(new BusinessException(Constants.TASK_EXCEPTION_PREFIX + "003", "移动中不允许接取任务")),
    NOT_ALLOW_TEAM(new BusinessException(Constants.TASK_EXCEPTION_PREFIX + "004", "该任务限制了不允许组队参与")),
    NOT_ALLOW_MUTUAL(new BusinessException(Constants.TASK_EXCEPTION_PREFIX + "005", "该任务限制了不能与其他任务同时进行")),
    NOT_ALLOW_MAP(new BusinessException(Constants.TASK_EXCEPTION_PREFIX + "006", "请前往指定地图开始任务")),
    ALREADY_IN_PROGRESS(new BusinessException(Constants.TASK_EXCEPTION_PREFIX + "007", "任务进行中，请不要重复接取")),
    ALREADY_COMPLETE(new BusinessException(Constants.TASK_EXCEPTION_PREFIX + "008", "任务已完成，请不要重复接取")),
    ALREADY_FAILED(new BusinessException(Constants.TASK_EXCEPTION_PREFIX + "009", "任务已失败，无法再次接取")),
    TASK_TPL_NOT_EXIST(new BusinessException(Constants.TASK_EXCEPTION_PREFIX + "010", "任务模板不存在")),
    ;
    private final BusinessException exception;
}
