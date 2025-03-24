package com.dingCreator.astrology.enums.exception;

import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author ding
 * @date 2024/10/25
 */
@Getter
@AllArgsConstructor
public enum ShopExceptionEnum {
    /**
     * 商店异常
     */
    SHOP_ITEM_NO_INVALID(Constants.SHOP_ITEM_EXCEPTION_PREFIX + "000", "商品编码有误"),
    SHOP_ITEM_SELL_OUT(Constants.SHOP_ITEM_EXCEPTION_PREFIX + "001", "商品已售罄"),
    NOT_ENOUGH_MONEY(Constants.SHOP_ITEM_EXCEPTION_PREFIX + "002", "余额不足，购买失败"),
    ;

    private final String code;
    private final String cheDesc;

    public BusinessException getException() {
        return new BusinessException(this.code, this.cheDesc);
    }
}
