package com.dingCreator.astrology.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dingCreator.astrology.database.DatabaseProvider;
import com.dingCreator.astrology.entity.DungeonRecord;
import com.dingCreator.astrology.enums.DungeonExploreStatusEnum;
import com.dingCreator.astrology.mapper.DungeonRecordMapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author ding
 * @date 2024/4/7
 */
public class DungeonRecordService {

    /**
     * 查询探索副本记录
     *
     * @param playerId  玩家ID
     * @param dungeonId 副本ID
     * @return 记录
     */
    public DungeonRecord query(Long playerId, Long dungeonId) {
        return DatabaseProvider.getInstance().executeReturn(sqlSession ->
                sqlSession.getMapper(DungeonRecordMapper.class).query(playerId, dungeonId));
    }

    /**
     * 查询探索副本记录
     *
     * @param playerIdList 玩家ID
     * @param dungeonId    副本ID
     * @return 探索副本记录
     */
    public List<DungeonRecord> queryLastExplore(List<Long> playerIdList, Long dungeonId) {
        return DatabaseProvider.getInstance().transactionExecuteReturn(sqlSession -> {
            QueryWrapper<DungeonRecord> wrapper = new QueryWrapper<DungeonRecord>()
                    .eq(DungeonRecord.DUNGEON_ID, dungeonId)
                    .in(DungeonRecord.PLAYER_ID, playerIdList);
            return sqlSession.getMapper(DungeonRecordMapper.class).selectList(wrapper);
        });
    }

    /**
     * 查询探索副本记录
     *
     * @param playerId  玩家ID
     * @return 探索副本记录
     */
    public DungeonRecord queryLastExplore(Long playerId, Long dungeonId) {
        return DatabaseProvider.getInstance().transactionExecuteReturn(sqlSession -> {
            QueryWrapper<DungeonRecord> wrapper = new QueryWrapper<DungeonRecord>()
                    .eq(DungeonRecord.PLAYER_ID, playerId)
                    .eq(DungeonRecord.DUNGEON_ID, dungeonId);
            return sqlSession.getMapper(DungeonRecordMapper.class).selectOne(wrapper);
        });
    }

    /**
     * 查询探索副本记录
     *
     * @param playerId  玩家ID
     * @return 探索副本记录
     */
    public DungeonRecord queryLastExplore(Long playerId) {
        return DatabaseProvider.getInstance().transactionExecuteReturn(sqlSession -> {
            QueryWrapper<DungeonRecord> wrapper = new QueryWrapper<DungeonRecord>()
                    .eq(DungeonRecord.PLAYER_ID, playerId)
                    .eq(DungeonRecord.EXPLORE_STATUS, DungeonExploreStatusEnum.EXPLORE.getCode());
            return sqlSession.getMapper(DungeonRecordMapper.class).selectOne(wrapper);
        });
    }

    /**
     * 新增或更新数据
     *
     * @param playerIds   玩家ID
     * @param dungeonId   副本ID
     * @param exploreStatus 探索状态
     * @param exploreTime 探索时间
     */
    public void insertOrUpdate(List<Long> playerIds, Long dungeonId, LocalDateTime exploreTime, int floor, String exploreStatus) {
        DatabaseProvider.getInstance().transactionExecute(sqlSession -> {
            List<DungeonRecord> dungeonRecordList = queryLastExplore(playerIds, dungeonId);
            DungeonRecordMapper mapper = sqlSession.getMapper(DungeonRecordMapper.class);

            dungeonRecordList.forEach(dungeonRecord -> {
                dungeonRecord.setFloor(floor);
                dungeonRecord.setExploreStatus(exploreStatus);
                dungeonRecord.setLastExploreTime(exploreTime);
                mapper.updateById(dungeonRecord);
            });

            if (dungeonRecordList.size() < playerIds.size()) {
                playerIds.stream()
                        .filter(playerId -> dungeonRecordList.stream().map(DungeonRecord::getPlayerId)
                                .noneMatch(recPlayerId -> recPlayerId.equals(playerId)))
                        .forEach(playerId -> {
                            DungeonRecord record = new DungeonRecord();
                            record.setPlayerId(playerId);
                            record.setDungeonId(dungeonId);
                            record.setFloor(floor);
                            record.setExploreStatus(exploreStatus);
                            record.setLastExploreTime(exploreTime);
                            mapper.insert(record);
                        });
            }
        });
    }


    private static class Holder {
        private static final DungeonRecordService SERVICE = new DungeonRecordService();
    }

    private DungeonRecordService() {

    }

    public static DungeonRecordService getInstance() {
        return DungeonRecordService.Holder.SERVICE;
    }
}
