package com.dingCreator.yuanshen.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 业务上的错误
 *
 * @author ding
 * @date 2024/2/1
 */
@Data
@AllArgsConstructor
public class BusinessException extends RuntimeException {

    /**
     * 错误代码
     */
    private String code;
    /**
     * 错误描述
     */
    private String desc;
}
