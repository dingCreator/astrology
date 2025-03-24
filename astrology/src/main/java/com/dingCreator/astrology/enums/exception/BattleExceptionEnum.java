package com.dingCreator.astrology.enums.exception;

import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author ding
 * @date 2024/3/30
 */
@Getter
@AllArgsConstructor
public enum BattleExceptionEnum {

    /**
     * 战斗异常
     */
    INITIATOR_LOW_HP(Constants.BATTLE_EXCEPTION_PREFIX + "000", "你或你的队友处于重伤状态"),
    RECIPIENT_LOW_HP(Constants.BATTLE_EXCEPTION_PREFIX + "001", "对方处于重伤状态"),
    BOSS_NOT_FOUND(Constants.BATTLE_EXCEPTION_PREFIX + "002", "Boss信息不存在"),
    ;

    private final String code;
    private final String cheDesc;

    public BusinessException getException() {
        return new BusinessException(this.code, this.cheDesc);
    }
}
