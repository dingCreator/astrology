package com.dingCreator.astrology.util;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.dingCreator.astrology.behavior.ExpBehavior;
import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.dto.loot.LootDTO;
import com.dingCreator.astrology.dto.loot.LootItemDTO;
import com.dingCreator.astrology.dto.loot.LootItemQueryDTO;
import com.dingCreator.astrology.dto.loot.LootQueryDTO;
import com.dingCreator.astrology.dto.organism.player.PlayerAssetDTO;
import com.dingCreator.astrology.enums.AssetTypeEnum;
import com.dingCreator.astrology.service.PlayerService;
import com.dingCreator.astrology.vo.LootVO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author ding
 * @date 2024/4/11
 */
public class LootUtil {

    /**
     * 转化掉落物信息
     *
     * @param loot 掉落物
     * @return 掉落物
     */
    @SuppressWarnings("raw_use")
    public static LootDTO convertLoot(LootQueryDTO loot) {
        if (Objects.isNull(loot)) {
            return null;
        }
        LootDTO lootDTO = new LootDTO();
        String assetJson = loot.getAsset();
        JSONObject assetJsonObj = JSONObject.parseObject(assetJson);
        Map<AssetTypeEnum, Long> assetMap = new HashMap<>();
        assetJsonObj.forEach((assetType, val) -> assetMap.put(AssetTypeEnum.getByCode(assetType), ((Number) val).longValue()));
        lootDTO.setAssetMap(assetMap);
        lootDTO.setExp(loot.getExp());
        lootDTO.setExtInfo(lootDTO.getExtInfo());
        // 转化实物
        lootDTO.setItemList(loot.getItemList().stream().map(item -> {
            LootItemDTO dto = new LootItemDTO();
            dto.setRate(item.getRate());
            dto.setArticleItem(ArticleUtil.convert(item.getArticleJson()));
            return dto;
        }).collect(Collectors.toList()));
        return lootDTO;
    }

    public static LootQueryDTO convertLoot(LootDTO lootDTO) {
        if (Objects.isNull(lootDTO)) {
            return null;
        }
        Map<String, Long> assetMap = new HashMap<>();
        lootDTO.getAssetMap().forEach((key, value) -> assetMap.put(key.getCode(), value));
        LootQueryDTO loot = new LootQueryDTO();
        loot.setExp(lootDTO.getExp());
        loot.setAsset(JSONObject.toJSONString(assetMap));
        loot.setExtInfo(lootDTO.getExtInfo());
        List<LootItemQueryDTO> lootItemList = lootDTO.getItemList().stream().map(dto -> {
            LootItemQueryDTO item = new LootItemQueryDTO();
            item.setRate(dto.getRate());
            item.setArticleJson(JSONObject.toJSONString(dto.getArticleItem()));
            return item;
        }).collect(Collectors.toList());
        loot.setItemList(lootItemList);
        return loot;
    }

    public static LootVO sendLoot(LootQueryDTO loot, Long playerId) {
        LootDTO lootDTO = convertLoot(loot);
        return sendLoot(lootDTO, playerId);
    }

    public static Map<Long, LootVO> sendLoot(LootQueryDTO loot, List<Long> playerIds) {
        LootDTO lootDTO = convertLoot(loot);
        return sendLoot(lootDTO, playerIds);
    }

    public static LootVO sendLoot(LootDTO lootDTO, Long playerId) {
        LootVO vo = new LootVO();
        if (Objects.isNull(lootDTO)) {
            return vo;
        }
        // 发钱
        vo.setAssetMap(lootDTO.getAssetMap());
        List<PlayerAssetDTO> assetList = lootDTO.getAssetMap().entrySet().stream()
                .map(entry -> PlayerAssetDTO.builder().assetType(entry.getKey().getCode())
                        .assetCnt(entry.getValue()).playerId(playerId).build())
                .collect(Collectors.toList());
        PlayerService.getInstance().changeAsset(PlayerCache.getPlayerById(playerId), assetList);
        // 发经验
        ExpBehavior.getInstance().getExp(playerId, lootDTO.getExp());
        vo.setExp(lootDTO.getExp());
        // 发实物
        if (!CollectionUtil.isEmpty(lootDTO.getItemList())) {
            vo.setItemVOList(lootDTO.getItemList().stream()
                    .filter(loot -> RandomUtil.isHit(loot.getRate()))
                    .map(loot -> {
                        // 发送
                        loot.getArticleItem().changeCnt(playerId, 1);
                        // 获取名字
                        return loot.getArticleItem().view();
                    }).collect(Collectors.toList()));
        }
        return vo;
    }


    /**
     * 获取掉落物并发放掉落物
     *
     * @return 掉落物汇总
     */
    public static Map<Long, LootVO> sendLoot(LootDTO lootDTO, List<Long> playerIdList) {
        return playerIdList.stream().collect(Collectors.toMap(Function.identity(), playerId -> sendLoot(lootDTO, playerId)));
    }

}
