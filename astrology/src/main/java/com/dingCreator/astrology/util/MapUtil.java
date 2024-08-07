package com.dingCreator.astrology.util;

import com.dingCreator.astrology.behavior.PlayerBehavior;
import com.dingCreator.astrology.behavior.TeamBehavior;
import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.cache.TeamCache;
import com.dingCreator.astrology.dto.player.PlayerDTO;
import com.dingCreator.astrology.dto.player.PlayerInfoDTO;
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
     * 移动中但未到达的，视为还在原处
     *
     * @param playerId 玩家ID
     * @return 地图ID
     */
    public static Long getNowLocation(long playerId) {
        PlayerDTO playerDTO = PlayerCache.getPlayerById(playerId).getPlayerDTO();
        long mapId = playerDTO.getMapId();
        return mapId << 32 >> 32;
    }

    /**
     * 获取玩家正在前往的位置
     *
     * @param playerId 玩家ID
     * @return 地图ID 如果不是移动中，则返回0
     */
    public static Long getTargetLocation(long playerId) {
        PlayerDTO playerDTO = PlayerCache.getPlayerById(playerId).getPlayerDTO();
        long mapId = playerDTO.getMapId();
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
        if (Objects.isNull(startMap) || Objects.isNull(endMap)) {
            return 0;
        }
        return Math.abs(startMap.getXPos() - endMap.getXPos()) + Math.abs(startMap.getYPos() - endMap.getYPos());
    }

    public static long moveTime(long playerId) {
        return moveTime(playerId, getNowLocation(playerId), getTargetLocation(playerId));
    }

    public static long moveTime(long playerId, long endMapId) {
        return moveTime(playerId, getNowLocation(playerId), endMapId);
    }

    /**
     * 计算移动时间
     *
     * @param endMapId   终点地图ID
     * @return 移动时间（s）
     */
    public static long moveTime(long playerId, long startMapId, long endMapId) {
        PlayerInfoDTO playerInfoDTO = PlayerCache.getPlayerById(playerId);
        if (endMapId == 0L) {
            return 0;
        }
        long speed;
        if (playerInfoDTO.getTeam()) {
            // 组队情况下，根据最慢的人计算
            TeamDTO teamDTO = TeamCache.getTeamByPlayerId(playerId);
            speed = teamDTO.getMembers().stream().map(PlayerCache::getPlayerById).map(PlayerInfoDTO::getPlayerDTO)
                    .mapToLong(PlayerDTO::getBehaviorSpeed).min().orElse(1);
        } else {
            speed = playerInfoDTO.getPlayerDTO().getBehaviorSpeed();
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
        String status = PlayerBehavior.getInstance().getStatus(PlayerCache.getPlayerById(playerId).getPlayerDTO());
        return PlayerStatusEnum.MOVING.getCode().equals(status);
    }

    /**
     * 开始移动
     *
     * @param playerId 玩家ID
     */
    public static void startMove(long playerId, long mapId) {
        PlayerInfoDTO playerInfoDTO = PlayerCache.getPlayerById(playerId);
        PlayerDTO playerDTO = playerInfoDTO.getPlayerDTO();
        if (!PlayerStatusEnum.FREE.getCode().equals(PlayerBehavior.getInstance().getStatus(playerDTO))) {
            throw MapExceptionEnum.NOT_FREE.getException();
        }
        TeamBehavior.getInstance().captainOnlyValidate(playerId);
        List<PlayerDTO> playerList;
        if (playerInfoDTO.getTeam()) {
            playerList = TeamCache.getTeamById(playerId).getMembers().stream().map(PlayerCache::getPlayerById)
                    .map(PlayerInfoDTO::getPlayerDTO).collect(Collectors.toList());
        } else {
            playerList = Collections.singletonList(playerDTO);
        }

        playerList.forEach(p -> {
            PlayerBehavior.getInstance().updatePlayerStatus(p, PlayerStatusEnum.MOVING,
                            () -> PlayerStatusEnum.FREE.getCode().equals(PlayerBehavior.getInstance().getStatus(p)),
                            MapExceptionEnum.NOT_FREE.getException());
            p.setMapId(getMovingMapId(p.getMapId(), mapId));
        });
        PlayerCache.flush(playerList.stream().map(PlayerDTO::getId).collect(Collectors.toList()));
    }

    public static long getMovingMapId(long startMapId, long endMapId) {
        return (endMapId << 32) + startMapId;
    }
}
