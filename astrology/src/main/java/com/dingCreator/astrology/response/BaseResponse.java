package com.dingCreator.astrology.response;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ding
 * @date 2024/2/29
 */
@Data
public class BaseResponse<E> implements Serializable {
    /**
     * 内容
     */
    private E content;
}
