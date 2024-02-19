package com.dingCreator.immortal.enums.exception;

import com.dingCreator.immortal.exception.BusinessException;
import com.dingCreator.immortal.constants.Constants;
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
    ALREADY_HANG_UP(new BusinessException(Constants.EXP_EXCEPTION_PREFIX + "000", "已处于挂机状态，请勿重复操作")),
    NOT_HANG_UP(new BusinessException(Constants.EXP_EXCEPTION_PREFIX + "001", "非挂机状态，无法结束挂机")),
    ;

    private final BusinessException exception;
}
