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
    /**
     * 物品
     */
    protected List<ArticleItemDTO> articleItemList;
}
