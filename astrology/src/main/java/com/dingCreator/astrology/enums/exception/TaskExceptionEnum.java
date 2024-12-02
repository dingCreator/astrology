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
    NO_NEED_COMPLETE_TASK_SCHEDULE(new BusinessException(Constants.TASK_EXCEPTION_PREFIX + "011", "你无需完成该任务")),
    MUST_COMPLETE_TASK_AS_SORT(new BusinessException(Constants.TASK_EXCEPTION_PREFIX + "012", "你必须按指定顺序完成任务")),
    TASK_TYPE_ERR(new BusinessException(Constants.TASK_EXCEPTION_PREFIX + "013", "任务类型输入有误")),
    TASK_MAP_ERR(new BusinessException(Constants.TASK_EXCEPTION_PREFIX + "014", "地图输入有误")),
    INVALID_TASK_SCHEDULE_TITLE_NO(new BusinessException(Constants.TASK_EXCEPTION_PREFIX + "015", "任务编号输入有误")),
    INVALID_JOB_NAME(new BusinessException(Constants.TASK_EXCEPTION_PREFIX + "016", "职业配置有误")),
    INVALID_RANK(new BusinessException(Constants.TASK_EXCEPTION_PREFIX + "017", "阶级配置有误")),
    INVALID_TASK_TPL_TITLE_ID(new BusinessException(Constants.TASK_EXCEPTION_PREFIX + "018", "任务模板配置有误")),
    ;
    private final BusinessException exception;
}
