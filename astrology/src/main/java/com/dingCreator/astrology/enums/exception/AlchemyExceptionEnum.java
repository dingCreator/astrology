package com.dingCreator.astrology.enums.exception;

import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author ding
 * @date 2025/3/11
 */
@Getter
@AllArgsConstructor
public enum AlchemyExceptionEnum {

    INVALID_HERB_NAME(Constants.ALCHEMY_EXCEPTION_PREFIX + "000", "无法识别的药材：%s"),
    NOT_ENOUGH_HERB(Constants.ALCHEMY_EXCEPTION_PREFIX + "001", "药材%s数量不足，消耗数量：%d，剩余数量：%d"),
    INVALID_HERB_INPUT(Constants.ALCHEMY_EXCEPTION_PREFIX + "002", "投入的药材有误"),
    NOT_ENOUGH_HERB_1(Constants.ALCHEMY_EXCEPTION_PREFIX + "003", "药材数量不足"),
    INVALID_HERB_CNT(Constants.ALCHEMY_EXCEPTION_PREFIX + "004", "药材数量有误：%d"),
    PILL_NOT_FOUND(Constants.ALCHEMY_EXCEPTION_PREFIX + "005", "无法识别的丹药：%s"),
    INVALID_PILL_CNT(Constants.ALCHEMY_EXCEPTION_PREFIX + "006", "丹药数量有误：%d"),
    NOT_ENOUGH_PILL(Constants.ALCHEMY_EXCEPTION_PREFIX + "007", "丹药数量不足，消耗数量：%d，剩余数量：%d"),
    HERB_NOT_HAVE(Constants.ALCHEMY_EXCEPTION_PREFIX + "008", "您并未拥有此药材"),
    NOT_ALCHEMY_MASTER(Constants.ALCHEMY_EXCEPTION_PREFIX + "009", "您还不是炼药师，先成为炼药师再开始炼丹吧"),
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
