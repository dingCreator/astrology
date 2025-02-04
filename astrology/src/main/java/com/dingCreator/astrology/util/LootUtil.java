package com.dingCreator.astrology.util;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.dingCreator.astrology.behavior.ExpBehavior;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.dto.LootDTO;
import com.dingCreator.astrology.dto.LootItemDTO;
import com.dingCreator.astrology.entity.Loot;
import com.dingCreator.astrology.enums.AssetTypeEnum;
import com.dingCreator.astrology.vo.LootVO;

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
    public static LootDTO convertLoot(Loot loot) {
        if (Objects.isNull(loot)) {
            return null;
        }
        LootDTO lootDTO = new LootDTO();
        String assetJson = loot.getAsset();
        JSONObject assetJsonObj = JSONObject.parseObject(assetJson);
        lootDTO.setAstrologyCoin(assetJsonObj.getLong(AssetTypeEnum.ASTROLOGY_COIN.getCode()));
        lootDTO.setDiamond(assetJsonObj.getLong(AssetTypeEnum.DIAMOND.getCode()));
        lootDTO.setExp(loot.getExp());
        // 转化实物
        lootDTO.setItemList(loot.getItemList().stream().map(item -> {
            LootItemDTO dto = new LootItemDTO();
            dto.setRate(item.getRate());
            dto.setArticleItem(ArticleUtil.convert(item.getArticleJson()));
            return dto;
        }).collect(Collectors.toList()));
        return lootDTO;
    }

    public static LootVO sendLoot(Loot loot, Long playerId) {
        LootDTO lootDTO = convertLoot(loot);
        return sendLoot(lootDTO, playerId);
    }

    public static Map<Long, LootVO> sendLoot(Loot loot, List<Long> playerIds) {
        LootDTO lootDTO = convertLoot(loot);
        return sendLoot(lootDTO, playerIds);
    }

    public static LootVO sendLoot(LootDTO lootDTO, Long playerId) {
        LootVO vo = new LootVO();
        if (Objects.isNull(lootDTO)) {
            return vo;
        }
        vo.setAstrologyCoin(lootDTO.getAstrologyCoin());
        vo.setDiamond(lootDTO.getDiamond());
        ExpBehavior.getInstance().getExp(playerId, lootDTO.getExp());
        vo.setExp(lootDTO.getExp());
        if (!CollectionUtil.isEmpty(lootDTO.getItemList())) {
            vo.setItemVOList(lootDTO.getItemList().stream()
                    .filter(loot -> RandomUtil.isHit(loot.getRate()))
                    .map(loot -> {
                        // 发送
                        loot.getArticleItem().send2Player(playerId);
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
