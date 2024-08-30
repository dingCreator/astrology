package com.dingCreator.astrology.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 显示物品
 *
 * @author ding
 * @date 2024/8/27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleItemVO {
    /**
     * 名称
     */
    private String name;
    /**
     * 描述
     */
    private String description;
}
