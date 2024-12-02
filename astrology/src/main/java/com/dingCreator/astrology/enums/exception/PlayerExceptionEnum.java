package com.dingCreator.astrology.enums.exception;

import com.dingCreator.astrology.exception.BusinessException;
import com.dingCreator.astrology.constants.Constants;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author ding
 * @date 2024/2/3
 */
@Getter
@AllArgsConstructor
public enum PlayerExceptionEnum {
    /**
     * 业务异常
     */
    PLAYER_NOT_FOUND(new BusinessException(Constants.PLAYER_EXCEPTION_PREFIX + "000", "未创建角色")),
    PLAYER_EXIST(new BusinessException(Constants.PLAYER_EXCEPTION_PREFIX + "001", "已创建角色，请勿重复创建")),
    JOB_NOT_EXIST(new BusinessException(Constants.PLAYER_EXCEPTION_PREFIX + "002", "角色正在开发，敬请期待")),
    NAME_EXIST(new BusinessException(Constants.PLAYER_EXCEPTION_PREFIX + "003", "该名称已存在")),
    IN_BATTLE(new BusinessException(Constants.PLAYER_EXCEPTION_PREFIX + "004", "决斗尚未结束，不能发起新的决斗")),
    NO_BATTLE(new BusinessException(Constants.PLAYER_EXCEPTION_PREFIX + "005", "没有人向你发起决斗")),
    BATTLE_EXPIRED(new BusinessException(Constants.PLAYER_EXCEPTION_PREFIX + "006", "决斗已过期")),
    RECIPIENT_NOT_FOUND(new BusinessException(Constants.PLAYER_EXCEPTION_PREFIX + "007", "对方未创建角色")),
    PLAYER_DISABLED(new BusinessException(Constants.PLAYER_EXCEPTION_PREFIX + "008", "账号被封禁，若需解禁请联系管理员")),
    ;
    private final BusinessException exception;
}
