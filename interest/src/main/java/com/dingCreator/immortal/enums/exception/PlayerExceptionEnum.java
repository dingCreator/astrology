package com.dingCreator.immortal.enums.exception;

import com.dingCreator.immortal.exception.BusinessException;
import com.dingCreator.immortal.constants.Constants;
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
    JOB_NOT_EXIST(new BusinessException(Constants.PLAYER_EXCEPTION_PREFIX + "002", "职业输入错误，输入“查询职业”指令查看职业信息")),
    ;
    private final BusinessException exception;
}
