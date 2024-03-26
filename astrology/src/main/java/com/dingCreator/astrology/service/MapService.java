package com.dingCreator.astrology.service;

import com.dingCreator.astrology.database.DatabaseProvider;
import com.dingCreator.astrology.entity.Map;
import com.dingCreator.astrology.mapper.MapDataMapper;

/**
 * @author ding
 * @date 2024/2/23
 */
public class MapService {

    /**
     * 根据地图ID获取地图信息
     *
     * @param mapId 地图ID
     * @return 地图信息
     */
    public static Map getMapById(Long mapId) {
        return (Map) DatabaseProvider.getInstance().doExecute(sqlSession ->
                sqlSession.getMapper(MapDataMapper.class).getMapById(mapId));
    }
}
