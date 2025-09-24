package com.dingCreator.astrology.behavior;

import com.dingCreator.astrology.dto.MarketItemDTO;
import com.dingCreator.astrology.dto.article.ArticleEquipmentItem;
import com.dingCreator.astrology.dto.article.ArticleItemDTO;
import com.dingCreator.astrology.enums.equipment.EquipmentEnum;
import com.dingCreator.astrology.enums.equipment.EquipmentRankEnum;
import com.dingCreator.astrology.enums.exception.MarketExceptionEnum;
import com.dingCreator.astrology.response.PageResponse;
import com.dingCreator.astrology.service.MarketService;
import com.dingCreator.astrology.util.ArticleUtil;
import com.dingCreator.astrology.vo.MarketResultVO;

import java.util.Map;

/**
 * @author ding
 * @date 2025/6/17
 */
public class MarketBehavior {

    public MarketResultVO buy(long playerId, long itemId, int cnt) {
        return MarketService.getInstance().buy(playerId, itemId, cnt);
    }

    public Long shelve(long playerId, String itemName, int itemCnt, Map<String, Long> assetMap) {
        ArticleItemDTO item = ArticleUtil.constructArticleByName(itemName);
        if (item instanceof ArticleEquipmentItem &&
                EquipmentRankEnum.RULE.equals(EquipmentEnum.getById(((ArticleEquipmentItem) item).getEquipmentId()).getEquipmentRankEnum())) {
            throw MarketExceptionEnum.CANT_DEAL_RULE_EQUIPMENT.getException();
        }
        return MarketService.getInstance().shelve(playerId, item, itemCnt, assetMap);
    }

    public MarketResultVO offShelve(long playerId, long itemId, int cnt) {
        return MarketService.getInstance().offShelve(playerId, itemId, cnt);
    }

    public PageResponse<MarketItemDTO> marketPage(int pageIndex, int pageSize, String itemName) {
        return MarketService.getInstance().pageMarketItem(pageIndex, pageSize, itemName);
    }

    public void changeCost(long playerId, long itemId, Map<String, Long> assetMap) {
        MarketService.getInstance().changeCost(playerId, itemId, assetMap);
    }

    private MarketBehavior() {

    }

    public static MarketBehavior getInstance() {
        return Holder.BEHAVIOR;
    }

    private static class Holder {
        public static final MarketBehavior BEHAVIOR = new MarketBehavior();
    }
}
