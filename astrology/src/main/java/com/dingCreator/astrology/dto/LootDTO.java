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
     * 钱
     */
    private Long money;
    /**
     * 经验
     */
    private Long exp;
    /**
     * 实物
     */
    private List<LootItemDTO> itemList;
}
