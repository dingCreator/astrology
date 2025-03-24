package com.dingCreator.astrology.enums.exception;

import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author ding
 * @date 2024/8/9
 */
@Getter
@AllArgsConstructor
public enum SysExceptionEnum {
    /**
     * 系统异常
     */
    SYS_BUSY(Constants.SYS_EXCEPTION_PREFIX + "001", "系统繁忙，操作失败"),
    ;

    private final String code;
    private final String cheDesc;

    public BusinessException getException() {
        return new BusinessException(this.code, this.cheDesc);
    }
}
