package com.dingCreator.astrology.request;

import com.dingCreator.astrology.dto.article.ArticleItemDTO;
import com.dingCreator.astrology.enums.ArticleTypeEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * @author ding
 * @date 2024/12/27
 */
@Data
public class LuckyActivityAwardSettingReq extends ActivityAwardSettingReq {

    private List<AwardSettingItem> settingItemList;

    @Data
    public static class AwardSettingItem {
        /**
         * 概率
         */
        private BigDecimal rate;
        /**
         * 物品
         */
        private List<Set<ArticleItemDTO>> articleItemList;
    }
}
