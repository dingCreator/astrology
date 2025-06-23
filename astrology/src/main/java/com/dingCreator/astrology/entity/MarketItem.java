package com.dingCreator.astrology.entity;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dingCreator.astrology.dto.MarketItemDTO;
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
 * @date 2025/6/18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("astrology_market_item")
public class MarketItem {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 玩家ID
     */
    @TableField("player_id")
    private Long playerId;
    /**
     * 物品名称
     */
    @TableField("article_name")
    private String articleName;
    /**
     * 物品json
     */
    @TableField("article_json")
    private String articleJson;
    /**
     * 物品数量
     */
    @TableField("item_cnt")
    private Integer itemCnt;
    /**
     * 价格（单价）
     */
    @TableField("cost_map_json")
    private String costMapJson;

    public static final String ARTICLE_NAME = "article_name";

    public static final String ITEM_CNT = "item_cnt";

    public MarketItemDTO convert() {
        JSONObject jsonObject = JSONObject.parseObject(this.costMapJson);
        Map<AssetTypeEnum, Long> costMap = new HashMap<>();
        jsonObject.forEach((assetType, val) -> costMap.put(AssetTypeEnum.getByCode(assetType), ((Number) val).longValue()));
        return MarketItemDTO.builder().id(this.id).playerId(this.playerId).itemCnt(this.itemCnt)
                .articleName(this.articleName).articleItem(ArticleUtil.convert(this.articleJson))
                .costMap(costMap).build();
    }
}
