package com.dingCreator.astrology.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dingCreator.astrology.database.DatabaseContext;
import com.dingCreator.astrology.database.DatabaseProvider;
import com.dingCreator.astrology.entity.Loot;
import com.dingCreator.astrology.mapper.LootMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collections;
import java.util.List;

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
    public static Loot getByBelongToId(String belongTo, Long belongToId) {
        List<Loot> lootList = getByBelongToId(belongTo, Collections.singletonList(belongToId));
        if (CollectionUtil.isEmpty(lootList)) {
            return null;
        }
        return lootList.get(0);
    }

    /**
     * 获取掉落物配置
     *
     * @param belongTo       归属
     * @param belongToIdList 归属ID
     * @return 掉落物配置
     */
    public static List<Loot> getByBelongToId(String belongTo, List<Long> belongToIdList) {
        return DatabaseProvider.getInstance().executeReturn(sqlSession -> sqlSession.getMapper(LootMapper.class)
                .getByBelongToId(belongTo, belongToIdList));
    }
}
