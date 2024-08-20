package com.dingCreator.astrology.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dingCreator.astrology.database.DatabaseProvider;
import com.dingCreator.astrology.entity.DungeonBoss;
import com.dingCreator.astrology.mapper.DungeonBossMapper;

import java.util.List;

/**
 * @author ding
 * @date 2024/4/7
 */
public class DungeonBossService {

    /**
     * 根据ID获取副本boss
     *
     * @param id ID
     * @return boss
     */
    public static DungeonBoss getById(Long id) {
        return DatabaseProvider.getInstance().executeReturn(sqlSession ->
                sqlSession.getMapper(DungeonBossMapper.class).selectById(id));
    }

    /**
     * 根据ID获取副本boss
     *
     * @param dungeonId 副本ID
     * @return boss
     */
    public static List<DungeonBoss> getByDungeonId(Long dungeonId) {
        return DatabaseProvider.getInstance().executeReturn(sqlSession -> {
            QueryWrapper<DungeonBoss> queryWrapper = new QueryWrapper<DungeonBoss>()
                    .eq(DungeonBoss.DUNGEON_ID, dungeonId);
            return sqlSession.getMapper(DungeonBossMapper.class).selectList(queryWrapper);
        });
    }
}
