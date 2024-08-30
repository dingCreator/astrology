package com.dingCreator.astrology.util;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.dingCreator.astrology.behavior.ExpBehavior;
import com.dingCreator.astrology.dto.LootDTO;
import com.dingCreator.astrology.dto.LootItemDTO;
import com.dingCreator.astrology.entity.Loot;
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
    public static LootDTO convertLoot(Loot loot) {
        if (Objects.isNull(loot)) {
            return null;
        }
        LootDTO lootDTO = new LootDTO();
        lootDTO.setMoney(loot.getMoney());
        lootDTO.setExp(loot.getExp());
        // 转化实物
        String itemListJson = loot.getLootItemList();
        if (StringUtils.isNotBlank(itemListJson)) {
            lootDTO.setItemList(JSONObject.parseArray(loot.getLootItemList(), LootItemDTO.class));
        }
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
        // todo 钱
        vo.setMoney(lootDTO.getMoney());
        ExpBehavior.getInstance().getExp(playerId, lootDTO.getExp());
        vo.setExp(lootDTO.getExp());
        if (!CollectionUtil.isEmpty(lootDTO.getItemList())) {
            vo.setLootItemNameList(lootDTO.getItemList().stream()
                    .filter(loot -> RandomUtil.isHit(loot.getRate()))
                    .map(loot -> {
                        // 发送
                        loot.getArticleItem().send2Player(playerId);
                        // 获取名字
                        return loot.getArticleItem().view().getName();
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
