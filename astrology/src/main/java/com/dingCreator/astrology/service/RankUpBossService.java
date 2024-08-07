package com.dingCreator.astrology.service;

import com.dingCreator.astrology.database.DatabaseProvider;
import com.dingCreator.astrology.entity.RankUpBoss;
import com.dingCreator.astrology.mapper.RankUpBossMapper;

import java.util.List;

/**
 * @author ding
 * @date 2024/3/29
 */
public class RankUpBossService {

    /**
     * 获取怪物信息
     *
     * @param job  职业
     * @param rank 突破前阶级
     * @return 怪物信息
     */
    public static List<RankUpBoss> getRankUpBoss(String job, Integer rank) {
        return DatabaseProvider.getInstance().executeReturn(sqlSession -> sqlSession
                .getMapper(RankUpBossMapper.class).getRankUpBoss(job, rank));
    }
}
