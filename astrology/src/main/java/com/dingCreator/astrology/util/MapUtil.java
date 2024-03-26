package com.dingCreator.astrology.util;

import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.cache.TeamCache;
import com.dingCreator.astrology.dto.PlayerDTO;
import com.dingCreator.astrology.dto.TeamDTO;
import com.dingCreator.astrology.entity.Map;
import com.dingCreator.astrology.entity.Player;
import com.dingCreator.astrology.enums.PlayerStatusEnum;
import com.dingCreator.astrology.service.MapService;

/**
 * @author ding
 * @date 2024/2/1
 */
public class MapUtil {

    /**
     * 获取玩家当前位置
     *
     * @param playerId 玩家ID
     * @return 地图ID
     */
    public static Long getNowLocation(long playerId) {
        long mapId = PlayerCache.getPlayerById(playerId).getPlayer().getMapId();
        return mapId << 32 >> 32;
    }

    /**
     * 获取玩家正在前往的位置
     *
     * @param playerId 玩家ID
     * @return 地图ID 如果不是移动中，则返回null
     */
    public static Long getTargetLocation(long playerId) {
        Player player = PlayerCache.getPlayerById(playerId).getPlayer();
        if (!PlayerStatusEnum.MOVING.getCode().equals(player.getStatus())) {
            return null;
        }
        long mapId = player.getMapId();
        return mapId >> 32;
    }

    /**
     * 两地图之间的曼哈顿距离
     *
     * @param map1Id 地图1ID
     * @param map2Id 地图2ID
     * @return 曼哈顿距离
     */
    public static long manhattanDistance(long map1Id, long map2Id) {
        Map startMap = MapService.getMapById(map1Id);
        Map endMap = MapService.getMapById(map2Id);
        return Math.abs(startMap.getXPos() - endMap.getXPos()) + Math.abs(startMap.getYPos() - endMap.getYPos());
    }

    /**
     * 计算移动时间
     *
     * @param startMapId 起点地图ID
     * @param endMapId   终点地图ID
     * @return 移动时间（s）
     */
    public static long moveTime(long playerId, long startMapId, long endMapId) {
        PlayerDTO playerDTO = PlayerCache.getPlayerById(playerId);
        long speed;
        if (playerDTO.getTeam()) {
            // 组队情况下，根据最慢的人计算
            TeamDTO teamDTO = TeamCache.getTeamByPlayerId(playerId);
            speed = teamDTO.getMembers().stream().map(PlayerCache::getPlayerById).map(PlayerDTO::getPlayer)
                    .mapToLong(Player::getBehaviorSpeed).min().orElse(0);
        } else {
            speed = playerDTO.getPlayer().getBehaviorSpeed();
        }
        if (speed <= 0) {
            throw new IllegalArgumentException("获取速度失败");
        }
        long distance = manhattanDistance(startMapId, endMapId);
        return Math.abs(distance / speed);
    }

    /**
     * 是否在移动过程中
     *
     * @param playerId 玩家ID
     * @return 是/否
     */
    public static boolean moving(long playerId) {
        String status = PlayerCache.getPlayerById(playerId).getPlayer().getStatus();
        return PlayerStatusEnum.MOVING.getCode().equals(status);
    }

    /**
     * 开始移动
     *
     * @param playerId 玩家ID
     */
    public static void startMove(long playerId) {

    }
}
