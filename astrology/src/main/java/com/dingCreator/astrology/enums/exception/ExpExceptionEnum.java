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
    CANT_HANG_UP(new BusinessException(Constants.EXP_EXCEPTION_PREFIX + "000", "非空闲状态，无法挂机")),
    NOT_HANG_UP(new BusinessException(Constants.EXP_EXCEPTION_PREFIX + "001", "非挂机状态，无法结束挂机")),
    ;

    private final BusinessException exception;
}
