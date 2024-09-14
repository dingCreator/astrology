package com.dingCreator.astrology.response;

import lombok.Data;

import java.util.List;

/**
 * @author ding
 * @date 2024/9/13
 */
@Data
public class PageResponse<E> {
    /**
     * 数据
     */
    private List<E> data;
    /**
     * 页码
     */
    private Integer pageIndex;
    /**
     * 页面大小
     */
    private Integer pageSize;
    /**
     * 数量
     */
    private Integer total;
}
