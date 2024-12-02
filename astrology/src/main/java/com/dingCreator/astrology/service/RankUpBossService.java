package com.dingCreator.astrology.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dingCreator.astrology.database.DatabaseProvider;
import com.dingCreator.astrology.entity.RankUpBoss;
import com.dingCreator.astrology.mapper.RankUpBossMapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
    public List<RankUpBoss> getRankUpBoss(String job, int rank) {
        return DatabaseProvider.getInstance().executeReturn(sqlSession -> sqlSession
                .getMapper(RankUpBossMapper.class).getRankUpBoss(job, rank));
    }

    /**
     * 插入或更新突破boss
     *
     * @param job        职业
     * @param rank       阶级
     * @param monsterIds 怪物ID
     */
    public void insertOrUpdateRankUpBoss(String job, int rank, List<Long> monsterIds) {
        DatabaseProvider.getInstance().execute(sqlSession -> {
            RankUpBossMapper mapper = sqlSession.getMapper(RankUpBossMapper.class);
            QueryWrapper<RankUpBoss> wrapper = new QueryWrapper<RankUpBoss>()
                    .eq(RankUpBoss.JOB, job).eq(RankUpBoss.RANK, rank);
            if (Objects.nonNull(mapper.selectOne(wrapper))) {
                mapper.delete(wrapper);
            }
            monsterIds.stream()
                    .map(id -> RankUpBoss.builder().job(job).rank(rank).monsterId(id).build())
                    .forEach(mapper::insert);
        });
    }


    private static class Holder {
        private static final RankUpBossService SERVICE = new RankUpBossService();
    }

    private RankUpBossService() {

    }

    public static RankUpBossService getInstance() {
        return RankUpBossService.Holder.SERVICE;
    }
}
