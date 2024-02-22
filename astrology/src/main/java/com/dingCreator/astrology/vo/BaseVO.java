package com.dingCreator.astrology.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author ding
 * @date 2024/2/22
 */
@Data
public class BaseVO<E> implements Serializable {
    /**
     * 返回信息
     */
    private List<E> msg;
}
