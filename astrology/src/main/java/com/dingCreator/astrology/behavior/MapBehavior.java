package com.dingCreator.astrology.behavior;

import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.entity.Map;
import com.dingCreator.astrology.service.MapService;

/**
 * @author ding
 * @date 2024/2/4
 */
public class MapBehavior {

    public void move(Long id, Long mapId) {
        long fromMapId = PlayerCache.getPlayerById(id).getPlayer().getMapId();
        Map from = MapService.getMapById(fromMapId);
        Map target = MapService.getMapById(mapId);
        long distance = Math.abs(target.getXPos() - from.getXPos()) + Math.abs(target.getYPos() - from.getYPos());
    }

}
