package com.dingCreator.astrology.dto;

import com.alibaba.fastjson.JSONObject;
import com.dingCreator.astrology.dto.article.ArticleItemDTO;
import com.dingCreator.astrology.entity.MarketItem;
import com.dingCreator.astrology.enums.AssetTypeEnum;
import com.dingCreator.astrology.util.ArticleUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ding
 * @date 2025/6/19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketItemDTO {
    /**
     * 主键ID
     */
    private Long id;
    /**
     * 玩家ID
     */
    private Long playerId;
    /**
     * 物品名称
     */
    private String articleName;
    /**
     * 物品json
     */
    private ArticleItemDTO articleItem;
    /**
     * 物品数量
     */
    private Integer itemCnt;
    /**
     * 价格（单价）
     */
    private Map<AssetTypeEnum, Long> costMap;

    public MarketItem convert() {
        String costMapJson = JSONObject.toJSONString(this.costMap);
        return MarketItem.builder().id(this.id).playerId(this.playerId).itemCnt(this.itemCnt)
                .articleName(this.articleName).articleJson(JSONObject.toJSONString(this.articleItem))
                .costMapJson(costMapJson).build();
    }
}
