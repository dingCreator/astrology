package com.dingCreator.astrology.enums.exception;

import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author ding
 * @date 2024/4/8
 */
@Getter
@AllArgsConstructor
public enum MapExceptionEnum {
    /**
     * 地图错误
     */
    MAP_NOT_FOUND(new BusinessException(Constants.MAP_EXCEPTION_PREFIX + "000", "地图不存在")),
    NOT_MOVING(new BusinessException(Constants.MAP_EXCEPTION_PREFIX + "001", "非移动中状态")),
    NOT_FREE(new BusinessException(Constants.MAP_EXCEPTION_PREFIX + "002", "非空闲状态")),
    SAME_MAP(new BusinessException(Constants.MAP_EXCEPTION_PREFIX + "003", "已位于该地图，无需移动")),
    ;
    private final BusinessException exception;
}
