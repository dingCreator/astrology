package com.dingCreator.astrology.enums.exception;

import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author ding
 * @date 2025/6/20
 */
@Getter
@AllArgsConstructor
public enum MarketExceptionEnum {

    ITEM_NOT_EXIST(Constants.MARKET_EXCEPTION_PREFIX + "000", "该编号的物品不存在"),
    NOT_YOUR_ITEM(Constants.MARKET_EXCEPTION_PREFIX + "001", "该物品不是你的"),
    INVALID_COST(Constants.MARKET_EXCEPTION_PREFIX + "002", "单价输入有误"),
    CANT_BUY_SELF(Constants.MARKET_EXCEPTION_PREFIX + "003", "你为什么要购买自己的东西呢"),
    NOT_DEAL_ASSET(Constants.MARKET_EXCEPTION_PREFIX + "004", "该资产不属于可交易范畴"),
    ;

    private final String code;

    private final String chnDesc;

    public BusinessException getException() {
        return new BusinessException(this.code, this.chnDesc);
    }
}
