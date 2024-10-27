package com.dingCreator.astrology.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ding
 * @date 2024/10/24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopItemDTO {
    /**
     * 物品
     */
    private ArticleItemDTO article;
    /**
     * 数量
     */
    private Integer count;
    /**
     * 单价
     */
    private Long price;
    /**
     * 数量，小于0表示无限
     */
    private Integer stock;
}
