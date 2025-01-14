package com.dingCreator.astrology.enums;

import com.alibaba.fastjson.JSONObject;
import com.dingCreator.astrology.dto.article.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
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
    EQUIPMENT("equipment", ArticleEquipmentItem.class, str -> JSONObject.parseObject(str, ArticleEquipmentItem.class),
            (id, item) -> {
                ArticleEquipmentItem equipmentItem = (ArticleEquipmentItem) item;
                ((ArticleEquipmentItem) item).setEquipmentId(id);
                return equipmentItem;
            }),
    /**
     * 称号
     */
    TITLE("title", ArticleTitleItem.class, str -> JSONObject.parseObject(str, ArticleTitleItem.class),
            (id, item) -> {
                ArticleTitleItem titleItem = (ArticleTitleItem) item;
                ((ArticleTitleItem) item).setTitleId(id);
                return titleItem;
            }
    ),
    /**
     * 技能
     */
    SKILL("skill", ArticleSkillItem.class, str -> JSONObject.parseObject(str, ArticleSkillItem.class),
            (id, item) -> {
                ArticleSkillItem skillItem = (ArticleSkillItem) item;
                ((ArticleSkillItem) item).setSkillId(id);
                return skillItem;
            }),
    /**
     * 缘石
     */
    DIAMOND("diamond", ArticleDiamondItem.class, str -> JSONObject.parseObject(str, ArticleDiamondItem.class),
            (cnt, item) -> {
                ArticleDiamondItem diamondItem = (ArticleDiamondItem) item;
                ((ArticleDiamondItem) item).setCnt(cnt);
                return diamondItem;
            }),
    /**
     * 圣星币
     */
    ASTROLOGY_COIN("astrologyCoin", ArticleAstrologyCoinItem.class,
            str -> JSONObject.parseObject(str, ArticleAstrologyCoinItem.class),
            (cnt, item) -> {
                ArticleAstrologyCoinItem articleAstrologyCoinItem = (ArticleAstrologyCoinItem) item;
                ((ArticleAstrologyCoinItem) item).setCnt(cnt);
                return articleAstrologyCoinItem;
            }),
    ;

    private final String type;

    private final Class<? extends ArticleItemDTO> clazz;

    private final Function<String, ? extends ArticleItemDTO> function;

    private final BiFunction<Long, ArticleItemDTO, ? extends ArticleItemDTO> getParam;

    public static ArticleTypeEnum getByType(String type) {
        return Arrays.stream(ArticleTypeEnum.values()).filter(e -> e.getType().equals(type)).findFirst().orElse(null);
    }
}
