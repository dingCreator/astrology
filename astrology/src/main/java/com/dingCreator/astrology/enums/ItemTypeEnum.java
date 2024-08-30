package com.dingCreator.astrology.enums;

import com.dingCreator.astrology.dto.ArticleItemDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author ding
 * @date 2024/4/10
 */
@Getter
@AllArgsConstructor
public enum ItemTypeEnum {
    /**
     * 装备
     */
    EQUIPMENT("Equipment", article -> (ArticleItemDTO.EquipmentItem) article),
    /**
     * 称号
     */
    TITLE("Title", article -> (ArticleItemDTO.TitleItem) article),
    ;

    /**
     * 类型名称
     */
    private final String typeName;
    /**
     * 转化方法
     */
    private final Function<ArticleItemDTO, ? extends ArticleItemDTO> function;

    public static ItemTypeEnum getByName(String typeName) {
        return Arrays.stream(ItemTypeEnum.values()).filter(e -> e.getTypeName().equals(typeName))
                .findFirst().orElse(null);
    }
}
