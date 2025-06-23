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

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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
            return ArticleTypeEnum.getByType(itemType).getConvertJsonFunc().apply(arrItem.toString());
        }).collect(Collectors.toList());
    }

    public static Set<ArticleItemDTO> convertSet(String json) {
        JSONArray jsonArray = JSONObject.parseArray(json);
        return jsonArray.stream().map(arrItem -> {
            String itemType = ((Map<?, ?>) arrItem).get(Constants.ITEM_TYPE).toString();
            return ArticleTypeEnum.getByType(itemType).getConvertJsonFunc().apply(arrItem.toString());
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
}
