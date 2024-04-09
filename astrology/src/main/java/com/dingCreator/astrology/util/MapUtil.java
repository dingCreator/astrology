package com.dingCreator.astrology.util;

import com.dingCreator.astrology.behavior.PlayerBehavior;
import com.dingCreator.astrology.behavior.TeamBehavior;
import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.cache.TeamCache;
import com.dingCreator.astrology.dto.PlayerDTO;
import com.dingCreator.astrology.dto.TeamDTO;
import com.dingCreator.astrology.entity.Map;
import com.dingCreator.astrology.entity.Player;
import com.dingCreator.astrology.enums.PlayerStatusEnum;
import com.dingCreator.astrology.enums.exception.MapExceptionEnum;
import com.dingCreator.astrology.service.MapService;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author ding
 * @date 2024/2/1
 */
public class MapUtil {

    public static Map getMapById(long mapId) {
        return MapService.getMapById(mapId);
    }

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
     * @return 地图ID 如果不是移动中，则返回0
     */
    public static Long getTargetLocation(long playerId) {
        Player player = PlayerCache.getPlayerById(playerId).getPlayer();
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

    public static long moveTime(Long playerId) {
        if (!moving(playerId)) {
            throw MapExceptionEnum.NOT_MOVING.getException();
        }
        return moveTime(playerId, getNowLocation(playerId), getTargetLocation(playerId));
    }

    public static long moveTime(Long playerId, Long endMapId) {
        return moveTime(playerId, getNowLocation(playerId), endMapId);
    }

    /**
     * 计算移动时间
     *
     * @param endMapId   终点地图ID
     * @return 移动时间（s）
     */
    public static long moveTime(Long playerId, Long startMapId, Long endMapId) {
        PlayerDTO playerDTO = PlayerCache.getPlayerById(playerId);
        if (endMapId == 0L) {
            throw MapExceptionEnum.NOT_MOVING.getException();
        }
        long speed;
        if (playerDTO.getTeam()) {
            // 组队情况下，根据最慢的人计算
            TeamDTO teamDTO = TeamCache.getTeamByPlayerId(playerId);
            speed = teamDTO.getMembers().stream().map(PlayerCache::getPlayerById).map(PlayerDTO::getPlayer)
                    .mapToLong(Player::getBehaviorSpeed).min().orElse(1);
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
        String status = PlayerBehavior.getInstance().getStatus(PlayerCache.getPlayerById(playerId).getPlayer());
        return PlayerStatusEnum.MOVING.getCode().equals(status);
    }

    /**
     * 开始移动
     *
     * @param playerId 玩家ID
     */
    public static void startMove(long playerId, long mapId) {
        PlayerDTO playerDTO = PlayerCache.getPlayerById(playerId);
        Player player = playerDTO.getPlayer();
        if (!PlayerStatusEnum.FREE.getCode().equals(PlayerBehavior.getInstance().getStatus(player))) {
            throw MapExceptionEnum.NOT_FREE.getException();
        }
        TeamBehavior.getInstance().captainOnlyValidate(playerId);
        List<Player> playerList;
        if (playerDTO.getTeam()) {
            playerList = TeamCache.getTeamById(playerId).getMembers().stream().map(PlayerCache::getPlayerById)
                    .map(PlayerDTO::getPlayer).collect(Collectors.toList());
        } else {
            playerList = Collections.singletonList(player);
        }

        playerList.forEach(p -> {
            PlayerBehavior.getInstance().updatePlayerStatus(p, PlayerStatusEnum.MOVING,
                            () -> PlayerStatusEnum.FREE.getCode().equals(PlayerBehavior.getInstance().getStatus(p)),
                            MapExceptionEnum.NOT_FREE.getException());
            p.setMapId(getMovingMapId(p.getMapId(), mapId));
        });
        PlayerCache.flush(playerList.stream().map(Player::getId).collect(Collectors.toList()));
    }

    public static long getMovingMapId(long startMapId, long endMapId) {
        return (endMapId << 32) + startMapId;
    }
}
