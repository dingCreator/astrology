package com.dingCreator.astrology.cache;

import cn.hutool.core.bean.BeanUtil;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.dto.equipment.EquipmentBarDTO;
import com.dingCreator.astrology.dto.organism.player.PlayerDTO;
import com.dingCreator.astrology.dto.organism.player.PlayerInfoDTO;
import com.dingCreator.astrology.entity.Player;
import com.dingCreator.astrology.enums.exception.PlayerExceptionEnum;
import com.dingCreator.astrology.service.PlayerService;
import com.dingCreator.astrology.util.LockUtil;
import com.dingCreator.astrology.util.ThreadPoolUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author ding
 * @date 2024/2/1
 */
public class PlayerCache {
    /**
     * 玩家信息缓存
     */
    private static final Map<Long, PlayerInfoDTO> PLAYER_MAP = new ConcurrentHashMap<>();

    /**
     * 创建新玩家
     *
     * @param player 新玩家
     */
    public static void createPlayer(Player player) {
        if (PLAYER_MAP.containsKey(player.getId()) || Objects.nonNull(PlayerService.getPlayerById(player.getId()))) {
            throw PlayerExceptionEnum.PLAYER_EXIST.getException();
        }
        LockUtil.execute(Constants.PLAYER_LOCK_PREFIX + player.getId(), () -> {
            if (PLAYER_MAP.containsKey(player.getId()) || Objects.nonNull(PlayerService.getPlayerById(player.getId()))) {
                throw PlayerExceptionEnum.PLAYER_EXIST.getException();
            }
            if (!PlayerService.createPlayer(player)) {
                throw new IllegalStateException("创建角色失败");
            }
            PlayerInfoDTO playerInfoDTO = new PlayerInfoDTO();
            playerInfoDTO.setEquipmentBarDTO(new EquipmentBarDTO());
            playerInfoDTO.setTeam(false);

            PlayerDTO playerDTO = new PlayerDTO();
            playerDTO.copyProperties(player);
            playerInfoDTO.setPlayerDTO(playerDTO);
            PLAYER_MAP.put(player.getId(), playerInfoDTO);
        });
    }

    /**
     * 懒加载机制读取玩家数据到缓存
     *
     * @param id 玩家ID
     * @return 玩家数据
     */
    public static PlayerInfoDTO getPlayerById(Long id) {
        PlayerInfoDTO playerInfoDTO = PLAYER_MAP.getOrDefault(id, null);
        if (Objects.isNull(playerInfoDTO)) {
            return LockUtil.execute(Constants.PLAYER_LOCK_PREFIX + id, () -> {
                PlayerInfoDTO temp = PLAYER_MAP.getOrDefault(id, null);
                if (Objects.isNull(temp)) {
                    temp = PlayerService.getPlayerDTOById(id);
                    if (Objects.isNull(temp.getPlayerDTO())) {
                        throw PlayerExceptionEnum.PLAYER_NOT_FOUND.getException();
                    }
                    PLAYER_MAP.put(id, temp);
                }
                return temp;
            });
        }
        return playerInfoDTO;
    }

    /**
     * 更新玩家数据
     * 允许一段时间内缓存与数据库不一致的情况，保证最终一致性即可
     *
     * @param id 玩家ID
     */
    public static void flush(Long id) {
        flush(Collections.singletonList(id));
    }

    /**
     * 更新玩家数据
     * 允许一段时间内缓存与数据库不一致的情况，保证最终一致性即可
     *
     * @param ids 玩家ID
     */
    public static void flush(List<Long> ids) {
        List<Player> players = ids.stream()
                .map(PLAYER_MAP::get)
                .map(PlayerInfoDTO::getPlayerDTO)
                .map(playerDTO -> {
                    Player player = new Player();
                    BeanUtil.copyProperties(playerDTO, player, true);
                    return player;
                }).collect(Collectors.toList());
        players.forEach(player -> ThreadPoolUtil.executeConsumer(PlayerService::updatePlayerById, player));
    }

    /**
     * 清除缓存
     */
    public static void clearCache() {
        PLAYER_MAP.clear();
    }
}
