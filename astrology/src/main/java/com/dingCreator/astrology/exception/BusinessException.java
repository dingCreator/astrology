package com.dingCreator.astrology.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 业务上的错误
 *
 * @author ding
 * @date 2024/2/1
 */
@Data
public class BusinessException extends RuntimeException {

    /**
     * 错误代码
     */
    private String code;
    /**
     * 错误描述
     */
    private String desc;

    public BusinessException(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public BusinessException(String code, String desc, Object[] args) {
        this.code = code;
        this.desc = String.format(desc, args);
    }

    public BusinessException fillArgs(Object... args) {
        this.desc = String.format(this.desc, args);
        return this;
    }
}
