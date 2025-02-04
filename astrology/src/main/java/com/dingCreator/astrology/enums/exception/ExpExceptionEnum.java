package com.dingCreator.astrology.enums.exception;

import com.dingCreator.astrology.exception.BusinessException;
import com.dingCreator.astrology.constants.Constants;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author ding
 * @date 2024/2/1
 */
@Getter
@AllArgsConstructor
public enum ExpExceptionEnum {
    /**
     * 业务异常
     */
    ALREADY_HANG_UP(new BusinessException(Constants.EXP_EXCEPTION_PREFIX + "000", "您已经处于挂机状态中了哦~")),
    MOVING(new BusinessException(Constants.EXP_EXCEPTION_PREFIX + "001", "您已经处于移动中，无法挂机")),
    NOT_HANG_UP(new BusinessException(Constants.EXP_EXCEPTION_PREFIX + "002", "您没有处于挂机中")),
    ;

    private final BusinessException exception;
}
