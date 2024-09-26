package com.dingCreator.astrology.util;

import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.response.PageResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author ding
 * @date 2024/9/19
 */
public class PageUtil {

    public static <T> PageResponse<T> buildPage(List<T> list, int pageIndex, int pageSize) {
        if (pageIndex < Constants.MIN_PAGE_INDEX) {
            pageIndex = Constants.MIN_PAGE_INDEX;
        }
        if (pageSize < Constants.MIN_PAGE_SIZE || pageSize > Constants.MAX_PAGE_SIZE) {
            pageSize = Constants.DEFAULT_PAGE_SIZE;
        }
        if (Objects.isNull(list)) {
            list = new ArrayList<>();
        }

        int size = list.size();
        int maxPageIndex = (int) Math.floor((float) size / (float) pageSize);
        pageIndex = Math.min(pageIndex, maxPageIndex);

        PageResponse<T> page = new PageResponse<>();
        page.setPageIndex(pageIndex);
        page.setData(list.stream().skip((pageIndex - 1) * pageSize).collect(Collectors.toList()));
        page.setPageSize(pageSize);
        page.setTotal(size);
        return page;
    }

    public static <T> PageResponse<T> buildPage(T[] array, int pageIndex, int pageSize) {
        return buildPage(Arrays.asList(array), pageIndex, pageSize);
    }
}
