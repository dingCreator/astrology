package com.dingCreator.astrology.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dingCreator.astrology.database.DatabaseProvider;
import com.dingCreator.astrology.entity.DungeonConfig;
import com.dingCreator.astrology.mapper.DungeonConfigMapper;

import java.util.List;

/**
 * @author ding
 * @date 2024/4/7
 */
public class DungeonConfigService {

    /**
     * 根据ID获取副本boss
     *
     * @param id ID
     * @return boss
     */
    public DungeonConfig getById(Long id) {
        return DatabaseProvider.getInstance().executeReturn(sqlSession ->
                sqlSession.getMapper(DungeonConfigMapper.class).selectById(id));
    }

    /**
     * 根据ID获取副本boss
     *
     * @param dungeonId 副本ID
     * @return boss
     */
    public List<DungeonConfig> getByDungeonId(Long dungeonId) {
        return DatabaseProvider.getInstance().executeReturn(sqlSession -> {
            QueryWrapper<DungeonConfig> queryWrapper = new QueryWrapper<DungeonConfig>()
                    .eq(DungeonConfig.DUNGEON_ID, dungeonId)
                    .orderByAsc(DungeonConfig.FLOOR, DungeonConfig.WAVE);
            return sqlSession.getMapper(DungeonConfigMapper.class).selectList(queryWrapper);
        });
    }

    private static class Holder {
        private static final DungeonConfigService SERVICE = new DungeonConfigService();
    }

    private DungeonConfigService() {

    }

    public static DungeonConfigService getInstance() {
        return DungeonConfigService.Holder.SERVICE;
    }
}
