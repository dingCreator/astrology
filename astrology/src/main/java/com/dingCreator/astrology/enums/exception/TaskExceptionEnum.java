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
    ;
    private final BusinessException exception;
}
