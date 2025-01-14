package com.dingCreator.astrology.request;

import com.dingCreator.astrology.enums.ArticleTypeEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author ding
 * @date 2024/12/27
 */
@Data
public class LuckyActivityAwardSettingReq extends ActivityAwardSettingReq {

    private List<AwardSettingItem> itemList;

    @Data
    public static class AwardSettingItem {
        /**
         * 概率
         */
        private BigDecimal rate;
        /**
         * 类型
         */
        private ArticleTypeEnum articleTypeEnum;
        /**
         * 参数
         */
        private List<List<Long>> params;
    }
}
