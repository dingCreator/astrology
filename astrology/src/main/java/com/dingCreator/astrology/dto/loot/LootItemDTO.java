package com.dingCreator.astrology.dto.loot;

import com.dingCreator.astrology.dto.article.ArticleItemDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ding
 * @date 2024/8/27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
