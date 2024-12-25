package com.dingCreator.astrology.enums;

import com.alibaba.fastjson.JSONObject;
import com.dingCreator.astrology.dto.article.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
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
    EQUIPMENT("equipment", ArticleEquipmentItem.class, str -> JSONObject.parseObject(str, ArticleEquipmentItem.class)),
    /**
     * 称号
     */
    TITLE("title", ArticleTitleItem.class, str -> JSONObject.parseObject(str, ArticleTitleItem.class)),
    /**
     * 技能
     */
    SKILL("skill", ArticleSkillItem.class, str -> JSONObject.parseObject(str, ArticleSkillItem.class)),
    /**
     * 缘石
     */
    DIAMOND("diamond", ArticleDiamondItem.class, str -> JSONObject.parseObject(str, ArticleDiamondItem.class)),
    ;

    private final String type;

    private final Class<? extends ArticleItemDTO> clazz;

    private final Function<String, ? extends ArticleItemDTO> function;

    public static ArticleTypeEnum getByType(String type) {
        return Arrays.stream(ArticleTypeEnum.values()).filter(e -> e.getType().equals(type)).findFirst().orElse(null);
    }
}
