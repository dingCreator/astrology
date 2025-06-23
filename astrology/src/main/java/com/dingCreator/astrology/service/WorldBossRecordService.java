package com.dingCreator.astrology.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dingCreator.astrology.database.DatabaseProvider;
import com.dingCreator.astrology.dto.WorldBossRankRecordDTO;
import com.dingCreator.astrology.entity.WorldBossRecord;
import com.dingCreator.astrology.mapper.WorldBossRecordMapper;
import com.dingCreator.astrology.response.PageResponse;
import com.dingCreator.astrology.util.PageUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ding
 * @date 2024/8/7
 */
public class WorldBossRecordService {

    public void insert(WorldBossRecord worldBossRecord) {
        DatabaseProvider.getInstance().execute(sqlSession ->
                sqlSession.getMapper(WorldBossRecordMapper.class).insert(worldBossRecord));
    }

    public WorldBossRankRecordDTO getPlayerRank(Long playerId, Long worldBossId) {
        return DatabaseProvider.getInstance().executeReturn(sqlSession ->
                sqlSession.getMapper(WorldBossRecordMapper.class).queryRankByPlayerId(playerId, worldBossId));
    }

    public PageResponse<WorldBossRankRecordDTO> queryRankPage(Long worldBossId, int pageIndex, int pageSize) {
        return DatabaseProvider.getInstance().transactionExecuteReturn(sqlSession -> {
                    WorldBossRecordMapper mapper = sqlSession.getMapper(WorldBossRecordMapper.class);
                    int count = mapper.selectCount(new QueryWrapper<WorldBossRecord>()
                            .eq(WorldBossRecord.WORLD_BOSS_ID, worldBossId));
                    List<WorldBossRankRecordDTO> records;
                    if (count == 0) {
                        records = new ArrayList<>();
                    } else {
                        records = mapper.queryRank(worldBossId, (pageIndex - 1) * pageSize, pageSize);
                    }
                    return PageUtil.addPageDesc(records, pageIndex, pageSize, count);
                }
        );
    }

    public WorldBossRecord getByPlayerId(Long playerId, Long worldBossId) {
        return DatabaseProvider.getInstance().transactionExecuteReturn(sqlSession ->
                sqlSession.getMapper(WorldBossRecordMapper.class).selectOne(
                        new QueryWrapper<WorldBossRecord>()
                                .eq(WorldBossRecord.PLAYER_ID, playerId)
                                .eq(WorldBossRecord.WORLD_BOSS_ID, worldBossId)
                )
        );
    }

    public void updateById(WorldBossRecord worldBossRecord) {
        DatabaseProvider.getInstance().transactionExecute(sqlSession ->
                sqlSession.getMapper(WorldBossRecordMapper.class).updateById(worldBossRecord)
        );
    }

    private static class Holder {
        private static final WorldBossRecordService SERVICE = new WorldBossRecordService();
    }

    private WorldBossRecordService() {

    }

    public static WorldBossRecordService getInstance() {
        return WorldBossRecordService.Holder.SERVICE;
    }
}
