package com.dingCreator.astrology.dto.activity;

import com.dingCreator.astrology.dto.article.ArticleItemDTO;
import lombok.Data;

import java.util.List;

/**
 * @author ding
 * @date 2024/11/28
 */
@Data
public abstract class BaseActivityAwardRuleDTO {

    public static final String FIELD_ARTICLE_ITEM_LIST = "articleItemList";

    /**
     * 物品
     */
    private List<ArticleItemDTO> articleItemList;
}
