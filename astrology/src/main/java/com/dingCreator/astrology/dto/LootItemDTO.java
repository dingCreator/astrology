package com.dingCreator.astrology.dto;

import lombok.Data;

/**
 * @author ding
 * @date 2024/8/27
 */
@Data
public class LootItemDTO {
    /**
     * 掉落率
     */
    private Float rate;
    /**
     * 掉落物
     */
    private ArticleItemDTO articleItem;
}
