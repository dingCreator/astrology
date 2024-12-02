package com.dingCreator.astrology.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dingCreator.astrology.behavior.DungeonBehavior;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.database.DatabaseProvider;
import com.dingCreator.astrology.entity.WorldBoss;
import com.dingCreator.astrology.mapper.WorldBossMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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
    public WorldBoss getTodayBoss() {
        return DatabaseProvider.getInstance().executeReturn(sqlSession ->
                sqlSession.getMapper(WorldBossMapper.class).selectOne(
                        new QueryWrapper<WorldBoss>().ge(WorldBoss.APPEAR_DATE, LocalDate.now())));
    }

    /**
     * 创建世界boss
     *
     * @param appearDate 出现时间
     * @param startHours 开始时间（整点）
     * @param endHours   结束时间（整点）
     * @param monsterId  怪物ID
     */
    public void insertOrUpdateWorldBoss(LocalDate appearDate, int startHours, int endHours, String monsterId) {
        LocalDateTime startTime = appearDate.atTime(startHours, 0);
        LocalDateTime endTime = endHours > Constants.MAX_HOUR ?
                appearDate.atTime(Constants.MAX_HOUR, Constants.MAX_MINUTE, Constants.MAX_SECOND) :
                appearDate.atTime(endHours, 0);

        WorldBoss worldBoss = WorldBoss.builder().appearDate(appearDate)
                .startTime(startTime)
                .endTime(endTime)
                .monsterId(monsterId)
                .build();

        DatabaseProvider.getInstance().execute(sqlSession -> {
            WorldBossMapper mapper = sqlSession.getMapper(WorldBossMapper.class);
            WorldBoss exist = mapper.selectOne(new QueryWrapper<WorldBoss>().eq(WorldBoss.APPEAR_DATE, worldBoss.getAppearDate()));
            if (Objects.nonNull(exist)) {
                worldBoss.setId(exist.getId());
                mapper.updateById(worldBoss);
            } else {
                mapper.insert(worldBoss);
            }
        });
    }

    private static class Holder {
        private static final WorldBossService SERVICE = new WorldBossService();
    }

    private WorldBossService() {

    }

    public static WorldBossService getInstance() {
        return WorldBossService.Holder.SERVICE;
    }
}
