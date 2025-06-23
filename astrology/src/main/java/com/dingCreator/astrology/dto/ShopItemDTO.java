package com.dingCreator.astrology.dto;

import com.dingCreator.astrology.dto.article.ArticleItemDTO;
import com.dingCreator.astrology.enums.AssetTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

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
    private Map<AssetTypeEnum, Long> costMap;
    /**
     * 库存，小于0表示无限
     */
    private Integer stock;
}
