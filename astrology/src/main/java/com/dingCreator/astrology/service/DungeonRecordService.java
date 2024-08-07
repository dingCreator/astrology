package com.dingCreator.astrology.service;

import com.dingCreator.astrology.database.DatabaseProvider;
import com.dingCreator.astrology.entity.DungeonRecord;
import com.dingCreator.astrology.mapper.DungeonRecordMapper;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
    public static DungeonRecord query(Long playerId, Long dungeonId) {
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
    public static List<DungeonRecord> queryList(List<Long> playerIdList, Long dungeonId) {
        return DatabaseProvider.getInstance().executeReturn(sqlSession ->
                sqlSession.getMapper(DungeonRecordMapper.class).queryList(playerIdList, dungeonId));
    }

    /**
     * 新增或更新数据
     *
     * @param playerIds   玩家ID
     * @param dungeonId   副本ID
     * @param exploreTime 探索时间
     */
    public static void insertOrUpdate(List<Long> playerIds, Long dungeonId, Date exploreTime) {
        List<DungeonRecord> dungeonRecordList = queryList(playerIds, dungeonId);
        dungeonRecordList.forEach(dungeonRecord -> {
            dungeonRecord.setLastExploreTime(exploreTime);
            DatabaseProvider.getInstance().executeReturn(sqlSession -> sqlSession.getMapper(DungeonRecordMapper.class)
                    .updateRecord(dungeonRecord));
        });

        if (dungeonRecordList.size() < playerIds.size()) {
            playerIds.stream().filter(playerId -> dungeonRecordList.stream().map(DungeonRecord::getPlayerId)
                    .noneMatch(recPlayerId -> recPlayerId.equals(playerId))
            ).forEach(playerId -> {
                DungeonRecord record = new DungeonRecord();
                record.setPlayerId(playerId);
                record.setDungeonId(dungeonId);
                record.setLastExploreTime(exploreTime);
                DatabaseProvider.getInstance().execute(sqlSession ->
                        sqlSession.getMapper(DungeonRecordMapper.class).createRecord(record));
            });
        }
    }
}
