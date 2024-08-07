package com.dingCreator.astrology.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dingCreator.astrology.behavior.ExpBehavior;
import com.dingCreator.astrology.dto.LootItemDTO;
import com.dingCreator.astrology.entity.Loot;
import com.dingCreator.astrology.enums.LootBelongToEnum;
import com.dingCreator.astrology.enums.LootItemTypeEnum;
import com.dingCreator.astrology.service.LootService;
import com.dingCreator.astrology.vo.LootVO;

import java.util.ArrayList;
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
     * 获取掉落物并发放掉落物
     *
     * @param lootMap 掉落物
     * @return 掉落物汇总
     */
    public static Map<Long, LootVO> getLoot(Map<LootBelongToEnum, List<Long>> lootMap, List<Long> playerIdList) {
        return playerIdList.stream().collect(Collectors.toMap(Function.identity(), playerId -> {
            LootVO lootVO = new LootVO();
            for (Map.Entry<LootBelongToEnum, List<Long>> entry : lootMap.entrySet()) {
                List<Loot> lootList = LootService.getByBelongToId(entry.getKey().getBelongTo(), entry.getValue());
                if (!lootList.isEmpty()) {
                    lootList.forEach(loot -> {
                        // 处理货币和经验
                        lootVO.setMoney(lootVO.getMoney() + loot.getMoney());
                        lootVO.setExp(lootVO.getExp() + loot.getExp());
                        // 处理实物
                        String itemListJson = loot.getLootItemList();
                        if (Objects.nonNull(itemListJson) && !"".equals(itemListJson)) {
                            JSONArray array = JSONObject.parseArray(loot.getLootItemList());
                            List<String> lootItemNameList = new ArrayList<>();
                            for (int i = 0; i < array.size(); i++) {
                                LootItemDTO item = array.getJSONObject(i).toJavaObject(LootItemDTO.class);
                                // 判断是否掉落
                                if (!RandomUtil.isHit(item.getRate())) {
                                    continue;
                                }
                                // 选择策略
                                LootItemTypeEnum lootType = LootItemTypeEnum.getByName(item.getLootItemType());
                                if (Objects.isNull(lootType)) {
                                    return;
                                }
                                // 发放掉落物
                                lootType.getDistributionMethod().accept(array.getJSONObject(i), playerId);
                                lootItemNameList.add(lootType.getViewLoot().apply(array.getJSONObject(i)));
                            }
                            lootVO.setLootItemNameList(lootItemNameList);
                        }
                        // 发放经验值
                        ExpBehavior.getInstance().getExp(playerId, loot.getExp());
                    });
                }
            }
            return lootVO;
        }));
    }

}
