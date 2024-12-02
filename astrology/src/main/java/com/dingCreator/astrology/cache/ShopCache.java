package com.dingCreator.astrology.cache;

import com.dingCreator.astrology.dto.article.ArticleEquipmentItem;
import com.dingCreator.astrology.dto.article.ArticleItemDTO;
import com.dingCreator.astrology.dto.ShopItemDTO;
import com.dingCreator.astrology.util.RandomUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ding
 * @date 2024/10/24
 */
public class ShopCache {
    /**
     * 商品缓存
     */
    private static final List<ShopItemDTO> SHOP_ITEM_LIST = new ArrayList<>();

    private static volatile boolean initFlag = false;

    public static List<ShopItemDTO> listShopItems() {
        if (!initFlag) {
            init();
        }
        return new ArrayList<>(SHOP_ITEM_LIST);
    }

    private synchronized static void init() {
        if (!initFlag) {
            initFlag = true;
            for (int i = 0; i < 5; i++) {
                ArticleItemDTO article = new ArticleEquipmentItem();
                ((ArticleEquipmentItem) article).setEquipmentId((long) RandomUtil.rangeIntRandom(1, 8));
                SHOP_ITEM_LIST.add(ShopItemDTO.builder().article(article).count(1).price(1000L).build());
            }
        }
    }
}
