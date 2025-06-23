package com.dingCreator.astrology.service;

import com.dingCreator.astrology.database.DatabaseProvider;
import com.dingCreator.astrology.dto.loot.LootDTO;
import com.dingCreator.astrology.dto.loot.LootQueryDTO;
import com.dingCreator.astrology.entity.Loot;
import com.dingCreator.astrology.entity.LootItem;
import com.dingCreator.astrology.mapper.LootItemMapper;
import com.dingCreator.astrology.mapper.LootMapper;
import com.dingCreator.astrology.util.LootUtil;

import java.util.List;
import java.util.Objects;

/**
 * @author ding
 * @date 2024/4/11
 */
public class LootService {
    /**
     * 获取掉落物配置
     *
     * @param belongTo   归属
     * @param belongToId 归属ID
     * @return 掉落物配置
     */
    public List<LootQueryDTO> getByBelongToId(String belongTo, Long belongToId) {
        return DatabaseProvider.getInstance().executeReturn(sqlSession ->
            sqlSession.getMapper(LootMapper.class).getByBelongToId(belongTo, belongToId)
        );
    }

    /**
     * 获取掉落物配置
     *
     * @param belongTo       归属
     * @param belongToIdList 归属ID
     * @return 掉落物配置
     */
    public List<LootQueryDTO> getByBelongToId(String belongTo, List<Long> belongToIdList) {
        return DatabaseProvider.getInstance().executeReturn(sqlSession -> sqlSession.getMapper(LootMapper.class)
                .getByBelongToIdList(belongTo, belongToIdList));
    }

    public void createLoot(String belongTo, Long belongToId, LootDTO lootDTO) {
        DatabaseProvider.getInstance().transactionExecute(sqlSession -> {
            LootQueryDTO lootQueryDTO = LootUtil.convertLoot(lootDTO);
            if (Objects.isNull(lootQueryDTO)) {
                return;
            }
            Loot loot = new Loot();
            loot.setBelongTo(belongTo);
            loot.setBelongToId(belongToId);
            loot.setExp(lootQueryDTO.getExp());
            loot.setAsset(lootQueryDTO.getAsset());
            loot.setExtInfo(lootQueryDTO.getExtInfo());
            sqlSession.getMapper(LootMapper.class).insert(loot);
            lootQueryDTO.getItemList().forEach(itemQry -> {
                LootItem lootItem = new LootItem();
                lootItem.setLootId(loot.getId());
                lootItem.setRate(itemQry.getRate());
                lootItem.setArticleJson(itemQry.getArticleJson());
                sqlSession.getMapper(LootItemMapper.class).insert(lootItem);
            });
        });
    }

    private LootService() {

    }

    public static LootService getInstance() {
        return Holder.SERVICE;
    }

    private static class Holder {
        private static final LootService SERVICE = new LootService();
    }
}
