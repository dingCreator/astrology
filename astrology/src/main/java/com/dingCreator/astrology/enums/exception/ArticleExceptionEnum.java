package com.dingCreator.astrology.enums.exception;

import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author ding
 * @date 2025/6/18
 */
@Getter
@AllArgsConstructor
public enum ArticleExceptionEnum {

    ARTICLE_NOT_EXIST(Constants.ARTICLE_EXCEPTION_PREFIX + "000", "物品【%s】不存在"),
    ;
    private final String code;
    private final String chnDesc;

    public BusinessException getException() {
        return new BusinessException(this.code, this.chnDesc);
    }

    public BusinessException getException(Object... args) {
        return new BusinessException(this.code, this.chnDesc, args);
    }
}
