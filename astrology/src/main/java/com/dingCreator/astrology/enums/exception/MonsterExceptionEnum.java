package com.dingCreator.astrology.enums.exception;

import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author ding
 * @date 2024/3/29
 */
@Getter
@AllArgsConstructor
public enum MonsterExceptionEnum {

    /**
     * 怪物相关异常
     */
    MONSTER_NOT_FOUND(new BusinessException(Constants.MONSTER_EXCEPTION_PREFIX + "000", "野怪信息缺失，请联系开发者")),
    ;

    private final BusinessException exception;
}
