package com.dingCreator.astrology.enums.exception;

import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author ding
 * @date 2025/6/19
 */
@Getter
@AllArgsConstructor
public enum HerbExceptionEnum {

    NOT_ENOUGH_HERB(Constants.HERB_EXCEPTION_PREFIX + "000", "药材不足"),
    ;
    private final String code;
    private final String chnDesc;

    public BusinessException getException() {
        return new BusinessException(code, chnDesc);
    }
}
