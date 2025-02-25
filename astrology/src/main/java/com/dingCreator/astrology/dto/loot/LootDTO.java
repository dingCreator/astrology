package com.dingCreator.astrology.dto.loot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author ding
 * @date 2024/8/27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
     * 扩展信息
     */
    private String extInfo;
    /**
     * 实物
     */
    private List<LootItemDTO> itemList;
}
