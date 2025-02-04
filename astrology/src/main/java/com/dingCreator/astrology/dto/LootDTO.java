package com.dingCreator.astrology.dto;

import lombok.Data;

import java.util.List;

/**
 * @author ding
 * @date 2024/8/27
 */
@Data
public class LootDTO {
    /**
     * 圣星币
     */
    private Long astrologyCoin;
    /**
     * 缘石
     */
    private Long diamond;
    /**
     * 经验
     */
    private Long exp;
    /**
     * 实物
     */
    private List<LootItemDTO> itemList;
}
