package com.dingCreator.astrology.enums.exception;

import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author ding
 * @date 2024/4/3
 */
@Getter
@AllArgsConstructor
public enum DungeonExceptionEnum {
    /**
     * 副本相关错误
     */
    PLAYER_NOT_FREE(new BusinessException(Constants.DUNGEON_EXCEPTION_PREFIX + "000", "非空闲状态，无法开始探索副本")),
    DUNGEON_NOT_FOUND(new BusinessException(Constants.DUNGEON_EXCEPTION_PREFIX + "001", "该副本不存在")),
    RANK_OVER_LIMIT(new BusinessException(Constants.DUNGEON_EXCEPTION_PREFIX + "002", "阶级超限")),
    ;
    private final BusinessException exception;
}
