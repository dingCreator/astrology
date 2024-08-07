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
public enum RankExceptionEnum {

    /**
     * Rank相关异常
     */
    ALREADY_MAX_RANK(new BusinessException(Constants.RANK_EXCEPTION_PREFIX + "000", "已达最高阶级")),
    LEVEL_NOT_ENOUGH(new BusinessException(Constants.RANK_EXCEPTION_PREFIX + "001", "等级不足，无法突破")),
    RANK_BOSS_NOT_FOUND(new BusinessException(Constants.RANK_EXCEPTION_PREFIX + "002", "新阶级尚未解锁，敬请期待")),
    ;
    private final BusinessException exception;
}