package com.dingCreator.astrology.behavior;

import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.entity.Map;
import com.dingCreator.astrology.enums.exception.MapExceptionEnum;
import com.dingCreator.astrology.service.MapService;
import com.dingCreator.astrology.util.MapUtil;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ding
 * @date 2024/2/4
 */
public class MapBehavior {

    private final MapService mapService = MapService.getInstance();

    /**
     * 获取距离
     *
     * @param playerId 玩家ID
     * @param mapName  地图名称
     * @return 距离
     */
    public long getDistance(Long playerId, String mapName) {
        long fromMapId = PlayerCache.getPlayerById(playerId).getPlayerDTO().getMapId();
        Map target = mapService.getMapByName(mapName);
        return MapUtil.manhattanDistance(fromMapId, target.getId());
    }

    /**
     * 获取移动时间
     *
     * @param playerId 玩家ID
     * @param mapName  地图名称
     * @return 需要花费时间
     */
    public long getMoveSeconds(Long playerId, String mapName) {
        Map target = mapService.getMapByName(mapName);
        if (MapUtil.getNowLocation(playerId).equals(target.getId())) {
            throw MapExceptionEnum.SAME_MAP.getException();
        }
        PlayerCache.getPlayerById(playerId).getPlayerDTO().getStatus();
        return MapUtil.moveTime(playerId, target.getId());
    }

    /**
     * 开始移动
     *
     * @param playerId 玩家ID
     * @param mapName  地图名称
     */
    public long startMove(Long playerId, String mapName) {
        Map target = mapService.getMapByName(mapName);
        if (MapUtil.getNowLocation(playerId).equals(target.getId())) {
            throw MapExceptionEnum.SAME_MAP.getException();
        }
        PlayerCache.getPlayerById(playerId).getPlayerDTO().getStatus();
        MapUtil.startMove(playerId, target.getId());
        return MapUtil.moveTime(playerId, target.getId());
    }

    /**
     * 查询地图列表
     *
     * @return 地图列表
     */
    public List<String> listMap(Long playerId) {
        List<Map> mapList = MapService.listMap();
        List<String> info = mapList.stream().map(m -> m.getName() + "(" + m.getXPos() + Constants.COMMA + m.getYPos() + ") 距离你" +
                MapUtil.manhattanDistance(MapUtil.getNowLocation(playerId), m.getId()))
                .collect(Collectors.toList());
        info.add("你当前位于：" + MapUtil.getMapById(MapUtil.getNowLocation(playerId)).getName());
        return info;
    }

    private static class Holder {
        private static final MapBehavior BEHAVIOR = new MapBehavior();
    }

    private MapBehavior() {

    }

    public static MapBehavior getInstance() {
        return MapBehavior.Holder.BEHAVIOR;
    }

}
