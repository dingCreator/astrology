package com.dingCreator.astrology.cache;

import com.dingCreator.astrology.dto.article.ArticleEquipmentItem;
import com.dingCreator.astrology.dto.article.ArticleItemDTO;
import com.dingCreator.astrology.dto.ShopItemDTO;
import com.dingCreator.astrology.dto.article.ArticleSkillItem;
import com.dingCreator.astrology.enums.AssetTypeEnum;
import com.dingCreator.astrology.enums.equipment.EquipmentEnum;
import com.dingCreator.astrology.enums.skill.SkillEnum;
import com.dingCreator.astrology.util.RandomUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ding
 * @date 2024/10/24
 */
public class ShopCache {
    /**
     * 商品缓存
     */
    private static final Map<String, List<ShopItemDTO>> SHOP_ITEM_MAP = new HashMap<>();

    private static volatile boolean initFlag = false;

    public static List<ShopItemDTO> listShopItems(String shopName) {
        init();
        return SHOP_ITEM_MAP.getOrDefault(shopName, new ArrayList<>());
    }

    public static List<String> listShopName() {
        init();
        return new ArrayList<>(SHOP_ITEM_MAP.keySet());
    }

    private synchronized static void init() {
        if (!initFlag) {
            initFlag = true;
            Map<AssetTypeEnum, Long> costMap;

            List<ShopItemDTO> worldShopItemList = new ArrayList<>();
            costMap = new HashMap<>();
            costMap.put(AssetTypeEnum.MIN_SHUI_HE_XI, 50L);
            worldShopItemList.add(ShopItemDTO.builder()
                    .article(new ArticleSkillItem(SkillEnum.SKILL_50.getId()))
                    .costMap(costMap).count(1).stock(-1).build());

            costMap = new HashMap<>();
            costMap.put(AssetTypeEnum.MIN_SHUI_HE_XI, 500L);
            worldShopItemList.add(ShopItemDTO.builder()
                    .article(new ArticleSkillItem(SkillEnum.SKILL_51.getId()))
                    .costMap(costMap).count(1).stock(-1).build());

            costMap = new HashMap<>();
            costMap.put(AssetTypeEnum.MIN_SHUI_HE_XI, 500L);
            worldShopItemList.add(ShopItemDTO.builder()
                    .article(new ArticleSkillItem(SkillEnum.SKILL_52.getId()))
                    .costMap(costMap).count(1).stock(-1).build());

            costMap = new HashMap<>();
            costMap.put(AssetTypeEnum.MIN_SHUI_HE_XI, 50L);
            worldShopItemList.add(ShopItemDTO.builder()
                    .article(new ArticleSkillItem(SkillEnum.SKILL_53.getId()))
                    .costMap(costMap).count(1).stock(-1).build());

            costMap = new HashMap<>();
            costMap.put(AssetTypeEnum.MIN_SHUI_HE_XI, 500L);
            worldShopItemList.add(ShopItemDTO.builder()
                    .article(new ArticleSkillItem(SkillEnum.SKILL_54.getId()))
                    .costMap(costMap).count(1).stock(-1).build());

            costMap = new HashMap<>();
            costMap.put(AssetTypeEnum.MIN_SHUI_HE_XI, 500L);
            worldShopItemList.add(ShopItemDTO.builder()
                    .article(new ArticleSkillItem(SkillEnum.SKILL_55.getId()))
                    .costMap(costMap).count(1).stock(-1).build());

            costMap = new HashMap<>();
            costMap.put(AssetTypeEnum.MIN_SHUI_HE_XI, 50L);
            worldShopItemList.add(ShopItemDTO.builder()
                    .article(new ArticleSkillItem(SkillEnum.SKILL_56.getId()))
                    .costMap(costMap).count(1).stock(-1).build());

            costMap = new HashMap<>();
            costMap.put(AssetTypeEnum.MIN_SHUI_HE_XI, 50L);
            worldShopItemList.add(ShopItemDTO.builder()
                    .article(new ArticleSkillItem(SkillEnum.SKILL_57.getId()))
                    .costMap(costMap).count(1).stock(-1).build());

            costMap = new HashMap<>();
            costMap.put(AssetTypeEnum.MIN_SHUI_HE_XI, 500L);
            worldShopItemList.add(ShopItemDTO.builder()
                    .article(new ArticleSkillItem(SkillEnum.SKILL_58.getId()))
                    .costMap(costMap).count(1).stock(-1).build());

            costMap = new HashMap<>();
            costMap.put(AssetTypeEnum.MIN_SHUI_HE_XI, 500L);
            worldShopItemList.add(ShopItemDTO.builder()
                    .article(new ArticleSkillItem(SkillEnum.SKILL_59.getId()))
                    .costMap(costMap).count(1).stock(-1).build());

            costMap = new HashMap<>();
            costMap.put(AssetTypeEnum.MIN_SHUI_HE_XI, 1500L);
            worldShopItemList.add(ShopItemDTO.builder()
                    .article(new ArticleEquipmentItem(EquipmentEnum.EQUIPMENT_413.getId()))
                    .costMap(costMap).count(1).stock(-1).build());

            costMap = new HashMap<>();
            costMap.put(AssetTypeEnum.MIN_SHUI_HE_XI, 1500L);
            worldShopItemList.add(ShopItemDTO.builder()
                    .article(new ArticleEquipmentItem(EquipmentEnum.EQUIPMENT_414.getId()))
                    .costMap(costMap).count(1).stock(-1).build());

            costMap = new HashMap<>();
            costMap.put(AssetTypeEnum.MIN_SHUI_HE_XI, 50000L);
            worldShopItemList.add(ShopItemDTO.builder()
                    .article(new ArticleEquipmentItem(EquipmentEnum.EQUIPMENT_600.getId()))
                    .costMap(costMap).count(1).stock(-1).build());

            costMap = new HashMap<>();
            costMap.put(AssetTypeEnum.TRIAL_AFTERGLOW, 5000L);
            worldShopItemList.add(ShopItemDTO.builder()
                    .article(new ArticleEquipmentItem(EquipmentEnum.EQUIPMENT_501.getId()))
                    .costMap(costMap).count(1).stock(-1).build());
            costMap = new HashMap<>();
            costMap.put(AssetTypeEnum.SECRET_SCROLL, 100L);
            worldShopItemList.add(ShopItemDTO.builder()
                    .article(new ArticleEquipmentItem(EquipmentEnum.EQUIPMENT_415.getId()))
                    .costMap(costMap).count(1).stock(-1).build());
            costMap = new HashMap<>();
            costMap.put(AssetTypeEnum.SECRET_SCROLL, 100L);
            worldShopItemList.add(ShopItemDTO.builder()
                    .article(new ArticleEquipmentItem(EquipmentEnum.EQUIPMENT_416.getId()))
                    .costMap(costMap).count(1).stock(-1).build());
            costMap = new HashMap<>();
            costMap.put(AssetTypeEnum.SECRET_SCROLL, 5000L);
            worldShopItemList.add(ShopItemDTO.builder()
                    .article(new ArticleEquipmentItem(EquipmentEnum.EQUIPMENT_506.getId()))
                    .costMap(costMap).count(1).stock(-1).build());

            SHOP_ITEM_MAP.put("世界商店", worldShopItemList);

            List<ShopItemDTO> originShopItemList = new ArrayList<>();
            costMap = new HashMap<>();
            costMap.put(AssetTypeEnum.ORIGIN_DRAGON_BALL, 500L);
            originShopItemList.add(ShopItemDTO.builder()
                    .article(new ArticleEquipmentItem(EquipmentEnum.EQUIPMENT_601.getId()))
                    .costMap(costMap).count(1).stock(-1).build());

            costMap = new HashMap<>();
            costMap.put(AssetTypeEnum.ORIGIN_DRAGON_BALL, 500L);
            originShopItemList.add(ShopItemDTO.builder()
                    .article(new ArticleEquipmentItem(EquipmentEnum.EQUIPMENT_602.getId()))
                    .costMap(costMap).count(1).stock(-1).build());

            costMap = new HashMap<>();
            costMap.put(AssetTypeEnum.ORIGIN_DRAGON_BALL, 500L);
            originShopItemList.add(ShopItemDTO.builder()
                    .article(new ArticleEquipmentItem(EquipmentEnum.EQUIPMENT_603.getId()))
                    .costMap(costMap).count(1).stock(-1).build());

            costMap = new HashMap<>();
            costMap.put(AssetTypeEnum.ORIGIN_DRAGON_BALL, 500L);
            originShopItemList.add(ShopItemDTO.builder()
                    .article(new ArticleEquipmentItem(EquipmentEnum.EQUIPMENT_604.getId()))
                    .costMap(costMap).count(1).stock(-1).build());

            costMap = new HashMap<>();
            costMap.put(AssetTypeEnum.ORIGIN_DRAGON_BALL, 100L);
            originShopItemList.add(ShopItemDTO.builder()
                    .article(new ArticleEquipmentItem(EquipmentEnum.EQUIPMENT_502.getId()))
                    .costMap(costMap).count(1).stock(-1).build());
            SHOP_ITEM_MAP.put("起源商店", originShopItemList);
        }
    }
}
