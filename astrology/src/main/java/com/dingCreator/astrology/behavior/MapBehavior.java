package com.dingCreator.astrology.behavior;

import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.cache.TeamCache;
import com.dingCreator.astrology.dto.PlayerDTO;
import com.dingCreator.astrology.dto.TeamDTO;
import com.dingCreator.astrology.entity.Map;
import com.dingCreator.astrology.enums.exception.MapExceptionEnum;
import com.dingCreator.astrology.enums.exception.TeamExceptionEnum;
import com.dingCreator.astrology.service.MapService;
import com.dingCreator.astrology.util.MapUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author ding
 * @date 2024/2/4
 */
public class MapBehavior {

    /**
     * 获取距离
     *
     * @param playerId 玩家ID
     * @param mapName  地图名称
     * @return 距离
     */
    public long getDistance(Long playerId, String mapName) {
        long fromMapId = PlayerCache.getPlayerById(playerId).getPlayer().getMapId();
        Map target = MapService.getMapByName(mapName);
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
        Map target = MapService.getMapByName(mapName);
        return MapUtil.moveTime(playerId, target.getId());
    }

    /**
     * 开始移动
     *
     * @param playerId 玩家ID
     * @param mapName  地图名称
     */
    public long startMove(Long playerId, String mapName) {
        Map target = MapService.getMapByName(mapName);
        MapUtil.startMove(playerId, target.getId());
        return MapUtil.moveTime(playerId, target.getId());
    }

    /**
     * 查询地图列表
     *
     * @return 地图列表
     */
    public List<String> listMap() {
        List<Map> mapList = MapService.listMap();
        return mapList.stream().map(m -> m.getName() + "(" + m.getXPos() + "," + m.getYPos() + ")")
                .collect(Collectors.toList());
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
