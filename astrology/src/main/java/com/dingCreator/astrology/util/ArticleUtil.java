package com.dingCreator.astrology.util;

import com.alibaba.fastjson.JSONObject;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.dto.article.ArticleItemDTO;
import com.dingCreator.astrology.enums.ArticleTypeEnum;

import java.util.Map;

/**
 * @author ding
 * @date 2024/10/28
 */
public class ArticleUtil {

    @SuppressWarnings("rawtypes")
    public static ArticleItemDTO convert(String json) {
        Map itemMap = JSONObject.parseObject(json, Map.class);
        String itemType = itemMap.get(Constants.ITEM_TYPE).toString();
        return ArticleTypeEnum.getByType(itemType).getFunction().apply(json);
    }
}
