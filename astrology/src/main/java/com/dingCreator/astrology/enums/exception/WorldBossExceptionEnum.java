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
public enum WorldBossExceptionEnum {
    /**
     * 世界boss相关异常
     */
    IN_CD(new BusinessException(Constants.WORLD_BOSS_EXCEPTION_PREFIX + "001", "讨伐冷却中，CD：%s s")),
    NOT_EXIST(new BusinessException(Constants.WORLD_BOSS_EXCEPTION_PREFIX + "002", "尚未生成世界boss，请耐心等待")),
    TOO_EARLY(new BusinessException(Constants.WORLD_BOSS_EXCEPTION_PREFIX + "003", "讨伐未开始，请耐心等待")),
    TOO_LATE(new BusinessException(Constants.WORLD_BOSS_EXCEPTION_PREFIX + "004", "讨伐已结束，请耐心等待下次开启")),
    DEFEATED(new BusinessException(Constants.WORLD_BOSS_EXCEPTION_PREFIX + "005", "世界boss已被击杀")),
    ATK_TIMES_OVER_LIMIT(new BusinessException(Constants.WORLD_BOSS_EXCEPTION_PREFIX + "006", "剩余次数不足")),
    ;
    private final BusinessException exception;

    /**
     * 需要添加参数时
     *
     * @param params 参数
     * @return 附加了参数的异常
     */
    public BusinessException getException(Object... params) {
        return new BusinessException(this.exception.getCode(), String.format(this.exception.getDesc(), params));
    }
}
