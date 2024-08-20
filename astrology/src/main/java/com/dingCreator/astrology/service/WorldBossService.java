package com.dingCreator.astrology.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dingCreator.astrology.database.DatabaseProvider;
import com.dingCreator.astrology.entity.WorldBoss;
import com.dingCreator.astrology.mapper.WorldBossMapper;

import java.time.LocalDate;

/**
 * @author ding
 * @date 2024/8/7
 */
public class WorldBossService {

    /**
     * 查询今天的boss
     *
     * @return boss
     */
    public static WorldBoss getTodayBoss() {
        return DatabaseProvider.getInstance().executeReturn(sqlSession ->
                sqlSession.getMapper(WorldBossMapper.class).selectOne(
                        new QueryWrapper<WorldBoss>().ge(WorldBoss.APPEAR_DATE, LocalDate.now())));
    }
}
