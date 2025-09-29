package com.dingCreator.astrology.enums.exception;

import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author ding
 * @date 2024/12/6
 */
@Getter
@AllArgsConstructor
public enum ActivityExceptionEnum {

    /**
     * 活动错误
     */
    OVER_JOIN_TIMES_LIMIT(Constants.ACTIVITY_EXCEPTION_PREFIX + "000", "参与次数已达上限，%s"),
    ACTIVITY_NOT_START(Constants.ACTIVITY_EXCEPTION_PREFIX + "001", "尚未开始，敬请期待"),
    ACTIVITY_HAS_BEEN_END(Constants.ACTIVITY_EXCEPTION_PREFIX + "002", "活动已结束"),
    NOT_ENOUGH_ASSET(Constants.ACTIVITY_EXCEPTION_PREFIX + "003", "货币不足，参与失败"),
    ACTIVITY_NOT_EXIST(Constants.ACTIVITY_EXCEPTION_PREFIX + "004", "活动不存在"),
    DEFAULT_ACTIVITY_NOT_EXIST(Constants.ACTIVITY_EXCEPTION_PREFIX + "005", "默认活动不存在"),
    ACTIVITY_NOT_ENABLED(Constants.ACTIVITY_EXCEPTION_PREFIX + "006", "活动已被禁用"),
    LUCKY_RATE_INVALID(Constants.ACTIVITY_EXCEPTION_PREFIX + "007", "抽奖概率之和不为100%"),
    ;

    private final String code;
    private final String chnDesc;
    
    public BusinessException getException() {
        return new BusinessException(this.code, this.chnDesc);
    }
}
