package com.dingCreator.astrology.service;

import com.dingCreator.astrology.database.DatabaseProvider;
import com.dingCreator.astrology.entity.Map;
import com.dingCreator.astrology.enums.exception.MapExceptionEnum;
import com.dingCreator.astrology.mapper.MapDataMapper;

import java.util.List;
import java.util.Objects;

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
    public Map getMapById(Long mapId) {
        return DatabaseProvider.getInstance().executeReturn(sqlSession ->
                sqlSession.getMapper(MapDataMapper.class).getMapById(mapId));
    }

    /**
     * 根据地图名称获取地图信息
     *
     * @param name 地图名称
     * @return 地图信息
     */
    public Map getMapByName(String name) {
        Map map = DatabaseProvider.getInstance().executeReturn(sqlSession ->
                sqlSession.getMapper(MapDataMapper.class).getMapByName(name));
        if (Objects.isNull(map)) {
            throw MapExceptionEnum.MAP_NOT_FOUND.getException();
        }
        return map;
    }

    /**
     * 查询地图列表
     *
     * @return 地图列表
     */
    public static List<Map> listMap() {
        return DatabaseProvider.getInstance().executeReturn(sqlSession ->
                sqlSession.getMapper(MapDataMapper.class).listMap());
    }


    private static class Holder {
        private static final MapService SERVICE = new MapService();
    }

    private MapService() {

    }

    public static MapService getInstance() {
        return MapService.Holder.SERVICE;
    }
}
