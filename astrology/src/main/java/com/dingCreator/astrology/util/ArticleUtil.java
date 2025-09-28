package com.dingCreator.astrology.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.dto.article.ArticleEquipmentItem;
import com.dingCreator.astrology.dto.article.ArticleItemDTO;
import com.dingCreator.astrology.dto.article.ArticleSkillItem;
import com.dingCreator.astrology.enums.ArticleTypeEnum;
import com.dingCreator.astrology.enums.equipment.EquipmentEnum;
import com.dingCreator.astrology.enums.exception.ArticleExceptionEnum;
import com.dingCreator.astrology.enums.skill.SkillEnum;
import com.dingCreator.astrology.exception.BusinessException;
import com.dingCreator.astrology.util.function.FunctionExecutor;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ding
 * @date 2024/10/28
 */
public class ArticleUtil {

    @SuppressWarnings("rawtypes")
    public static ArticleItemDTO convert(String json) {
        Map itemMap = JSONObject.parseObject(json, Map.class);
        String itemType = itemMap.get(Constants.ITEM_TYPE).toString();
        return ArticleTypeEnum.getByType(itemType).getConvertJsonFunc().apply(json);
    }

    public static List<ArticleItemDTO> convertList(String json) {
        JSONArray jsonArray = JSONObject.parseArray(json);
        return jsonArray.stream().map(arrItem -> {
            String itemType = ((Map<?, ?>) arrItem).get(Constants.ITEM_TYPE).toString();
            return ArticleTypeEnum.getByType(itemType).getConvertJsonFunc().apply(JSONObject.toJSONString(arrItem));
        }).collect(Collectors.toList());
    }

    public static Set<ArticleItemDTO> convertSet(String json) {
        JSONArray jsonArray = JSONObject.parseArray(json);
        return jsonArray.stream().map(arrItem -> {
            String itemType = ((Map<?, ?>) arrItem).get(Constants.ITEM_TYPE).toString();
            return ArticleTypeEnum.getByType(itemType).getConvertJsonFunc().apply(JSONObject.toJSONString(arrItem));
        }).collect(Collectors.toSet());
    }

    public static ArticleItemDTO constructArticleByName(String name) {
        EquipmentEnum equipmentEnum = EquipmentEnum.getByName(name, false);
        if (Objects.nonNull(equipmentEnum)) {
            return new ArticleEquipmentItem(equipmentEnum.getId());
        }
        SkillEnum skillEnum = SkillEnum.getByName(name);
        if (Objects.nonNull(skillEnum)) {
            return new ArticleSkillItem(skillEnum.getId());
        }
        throw ArticleExceptionEnum.ARTICLE_NOT_EXIST.getException(name);
    }

    public static void batchSendArticle(Long playerId, List<ArticleItemDTO> articleItems) {
        List<ArticleItemDTO> sendingQueue = new ArrayList<>(articleItems.size());
        articleItems.forEach(item -> item.add2SendingQueue(sendingQueue));
        Map<String, List<ArticleItemDTO>> itemMap = sendingQueue.stream().collect(Collectors.groupingBy(ArticleItemDTO::getItemType));
        ThreadPoolUtil.join(itemMap.entrySet().stream().map(entry -> (FunctionExecutor) () -> {
            ArticleTypeEnum articleTypeEnum = ArticleTypeEnum.getByType(entry.getKey());
            articleTypeEnum.getBatchSendFunc().accept(playerId, entry.getValue());
        }).collect(Collectors.toList()));
    }
}
