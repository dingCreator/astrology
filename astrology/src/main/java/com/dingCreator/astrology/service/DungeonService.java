package com.dingCreator.astrology.service;

import com.dingCreator.astrology.database.DatabaseProvider;
import com.dingCreator.astrology.entity.Dungeon;
import com.dingCreator.astrology.mapper.DungeonMapper;

import java.util.List;

/**
 * @author ding
 * @date 2024/4/4
 */
public class DungeonService {

    /**
     * 通过ID获取副本
     *
     * @param dungeonId 副本ID
     * @return 副本信息
     */
    public static Dungeon getById(Long dungeonId) {
        return (Dungeon) DatabaseProvider.getInstance().doExecute(sqlSession ->
                sqlSession.getMapper(DungeonMapper.class).getById(dungeonId));
    }

    /**
     * 通过名称获取副本
     *
     * @param mapId       地图ID
     * @param dungeonName 副本名称
     * @return 副本信息
     */
    public static Dungeon getByName(Long mapId, String dungeonName) {
        return (Dungeon) DatabaseProvider.getInstance().doExecute(sqlSession ->
                sqlSession.getMapper(DungeonMapper.class).getByName(mapId, dungeonName));
    }

    /**
     * 通过名称获取副本
     *
     * @param mapId       地图ID
     * @return 副本信息
     */
    @SuppressWarnings("unchecked")
    public static List<Dungeon> list(Long mapId) {
        return (List<Dungeon>) DatabaseProvider.getInstance().doExecute(sqlSession ->
                sqlSession.getMapper(DungeonMapper.class).list(mapId));
    }
}
