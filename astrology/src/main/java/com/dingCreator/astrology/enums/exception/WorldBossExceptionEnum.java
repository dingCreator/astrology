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
    IN_CD(Constants.WORLD_BOSS_EXCEPTION_PREFIX + "001", "讨伐冷却中，CD：%s s"),
    NOT_EXIST(Constants.WORLD_BOSS_EXCEPTION_PREFIX + "002", "尚未生成世界boss，请耐心等待"),
    TOO_EARLY(Constants.WORLD_BOSS_EXCEPTION_PREFIX + "003", "讨伐未开始，请耐心等待"),
    TOO_LATE(Constants.WORLD_BOSS_EXCEPTION_PREFIX + "004", "讨伐已结束，请耐心等待下次开启"),
    DEFEATED(Constants.WORLD_BOSS_EXCEPTION_PREFIX + "005", "世界boss已被击杀"),
    ATK_TIMES_OVER_LIMIT(Constants.WORLD_BOSS_EXCEPTION_PREFIX + "006", "剩余次数不足"),
    ;

    private final String code;
    private final String chnDesc;

    public BusinessException getException(Object... args) {
        String cheDesc;
        if (args.length == 0) {
            cheDesc = this.chnDesc;
        } else {
            cheDesc = String.format(this.chnDesc, args);
        }
        return new BusinessException(this.code, cheDesc);
    }
}
