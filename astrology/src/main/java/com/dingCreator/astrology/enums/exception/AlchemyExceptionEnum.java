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

    INVALID_HERB_NAME(new BusinessException(Constants.ALCHEMY_EXCEPTION_PREFIX + "000", "无法识别的药材：%s")),
    NOT_ENOUGH_HERB(new BusinessException(Constants.ALCHEMY_EXCEPTION_PREFIX + "001", "药材%s数量不足，消耗数量：%d，剩余数量：%d")),
    INVALID_HERB_INPUT(new BusinessException(Constants.ALCHEMY_EXCEPTION_PREFIX + "002", "投入的药材有误")),
    NOT_ENOUGH_HERB_1(new BusinessException(Constants.ALCHEMY_EXCEPTION_PREFIX + "003", "药材数量不足")),
    INVALID_HERB_CNT(new BusinessException(Constants.ALCHEMY_EXCEPTION_PREFIX + "004", "药材数量有误：%d")),
    PILL_NOT_FOUND(new BusinessException(Constants.ALCHEMY_EXCEPTION_PREFIX + "005", "无法识别的丹药：%s")),
    INVALID_PILL_CNT(new BusinessException(Constants.ALCHEMY_EXCEPTION_PREFIX + "006", "丹药数量有误：%d")),
    NOT_ENOUGH_PILL(new BusinessException(Constants.ALCHEMY_EXCEPTION_PREFIX + "001", "丹药数量不足，消耗数量：%d，剩余数量：%d")),
    ;
    private final BusinessException exception;
}
