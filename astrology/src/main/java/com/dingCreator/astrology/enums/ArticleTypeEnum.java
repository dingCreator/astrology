package com.dingCreator.astrology.enums;

import com.alibaba.fastjson.JSONObject;
import com.dingCreator.astrology.dto.article.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author ding
 * @date 2024/10/28
 */
@Getter
@AllArgsConstructor
public enum ArticleTypeEnum {
    /**
     * 装备
     */
    EQUIPMENT("equipment", str -> JSONObject.parseObject(str, ArticleEquipmentItem.class)),
    /**
     * 称号
     */
    TITLE("title", str -> JSONObject.parseObject(str, ArticleTitleItem.class)),
    /**
     * 技能
     */
    SKILL("skill", str -> JSONObject.parseObject(str, ArticleSkillItem.class)),
    /**
     * 药材
     */
    HERB("herb", str -> JSONObject.parseObject(str, ArticleHerbItem.class)),
    /**
     * 资产
     */
    ASSET("asset", str -> JSONObject.parseObject(str, ArticleAssetItem.class)),
    /**
     * 经验
     */
    EXP("exp", str -> JSONObject.parseObject(str, ArticleExpItem.class)),
    ;

    private final String type;

    private final Function<String, ? extends ArticleItemDTO> convertJsonFunc;

    public static ArticleTypeEnum getByType(String type) {
        return Arrays.stream(ArticleTypeEnum.values()).filter(e -> e.getType().equals(type)).findFirst().orElse(null);
    }
}
