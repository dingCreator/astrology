package com.dingCreator.astrology.enums;

import com.alibaba.fastjson.JSONObject;
import com.dingCreator.astrology.dto.article.ArticleItemDTO;
import com.dingCreator.astrology.dto.article.ArticleEquipmentItem;
import com.dingCreator.astrology.dto.article.ArticleSkillItem;
import com.dingCreator.astrology.dto.article.ArticleTitleItem;
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
    EQUIPMENT("equipment", str -> JSONObject.parseObject(str, ArticleEquipmentItem.class)),
    /**
     * 称号
     */
    TITLE("title", str -> JSONObject.parseObject(str, ArticleTitleItem.class)),
    /**
     * 技能
     */
    SKILL("skill", str -> JSONObject.parseObject(str, ArticleSkillItem.class)),
    ;

    private final String type;

    private final Function<String, ? extends ArticleItemDTO> function;

    public static ArticleTypeEnum getByType(String type) {
        return Arrays.stream(ArticleTypeEnum.values()).filter(e -> e.getType().equals(type)).findFirst().orElse(null);
    }
}
