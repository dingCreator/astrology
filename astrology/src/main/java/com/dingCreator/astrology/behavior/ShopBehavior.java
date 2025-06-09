package com.dingCreator.astrology.behavior;

import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.cache.ShopCache;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.dto.ShopItemDTO;
import com.dingCreator.astrology.dto.organism.player.PlayerAssetDTO;
import com.dingCreator.astrology.dto.organism.player.PlayerInfoDTO;
import com.dingCreator.astrology.enums.AssetTypeEnum;
import com.dingCreator.astrology.enums.exception.ShopExceptionEnum;
import com.dingCreator.astrology.response.PageResponse;
import com.dingCreator.astrology.service.PlayerService;
import com.dingCreator.astrology.util.LockUtil;
import com.dingCreator.astrology.util.PageUtil;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ding
 * @date 2024/10/24
 */
public class ShopBehavior {

    public List<String> listShopName() {
        return ShopCache.listShopName();
    }

    public PageResponse<ShopItemDTO> listShopItems(String shopName, int pageIndex, int pageSize) {
        return PageUtil.buildPage(ShopCache.listShopItems(shopName), pageIndex, pageSize);
    }

    public ShopItemDTO buy(Long playerId, String shopName, Integer no) {
        int idx = no - 1;
        List<ShopItemDTO> itemList = ShopCache.listShopItems(shopName);
        if (idx < 0 || idx >= itemList.size()) {
            throw ShopExceptionEnum.SHOP_ITEM_NO_INVALID.getException();
        }
        PlayerInfoDTO info = PlayerCache.getPlayerById(playerId);
        ShopItemDTO buyResult = LockUtil.execute(Constants.SHOP_BUY_LOCK_PREFIX + no, () -> {
            ShopItemDTO item = itemList.get(idx);
            if (item.getStock() < 0) {
                // 不限购，直接释放锁
                return item;
            } else if (item.getStock() == 0) {
                // 限购且售罄
                throw ShopExceptionEnum.SHOP_ITEM_SELL_OUT.getException();
            } else {
                // 限购且有货，需逻辑完成后才能释放锁
                buy(info, item);
                return item;
            }
        });
        // 不限购的商品释放锁后再处理购买逻辑
        if (buyResult.getStock() < 0) {
            buy(info, buyResult);
        }
        return buyResult;
    }

    private void buy(PlayerInfoDTO info, ShopItemDTO item) {
        item.getCostMap().forEach(((assetTypeEnum, cost) -> {
            if (!assetTypeEnum.getValidateCache().apply(info.getAssetList(), cost)) {
                throw ShopExceptionEnum.NOT_ENOUGH_MONEY.getException();
            }
        }));
        Long playerId = info.getPlayerDTO().getId();
        List<PlayerAssetDTO> assetList = item.getCostMap().entrySet().stream().map(entry ->
            PlayerAssetDTO.builder().playerId(playerId).assetType(entry.getKey().getCode()).assetCnt(-entry.getValue()).build()
        ).collect(Collectors.toList());
        PlayerService.getInstance().changeAsset(info, assetList);
        item.getArticle().send2Player(playerId, item.getCount());
    }

    private static class Holder {
        private static final ShopBehavior BEHAVIOR = new ShopBehavior();
    }

    private ShopBehavior() {

    }

    public static ShopBehavior getInstance() {
        return Holder.BEHAVIOR;
    }
}
