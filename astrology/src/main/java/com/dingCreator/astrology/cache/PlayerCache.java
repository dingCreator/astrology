package com.dingCreator.astrology.cache;

import com.dingCreator.astrology.database.DatabaseContext;
import com.dingCreator.astrology.database.DatabaseProvider;
import com.dingCreator.astrology.database.PlayerDataMapper;
import com.dingCreator.astrology.dto.PlayerDTO;
import com.dingCreator.astrology.entity.Player;
import com.dingCreator.astrology.enums.exception.PlayerExceptionEnum;
import com.dingCreator.astrology.service.PlayerService;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author ding
 * @date 2024/2/1
 */
public class PlayerCache {

    private static final Map<Long, PlayerDTO> PLAYER_MAP = new ConcurrentHashMap<>();

    public void createPlayer(Player player) {
        if (PLAYER_MAP.containsKey(player.getId())) {
            throw PlayerExceptionEnum.PLAYER_EXIST.getException();
        }
        if (!PlayerService.createPlayer(player)) {
            throw new IllegalArgumentException("创建角色失败");
        }
        PlayerDTO playerDTO = new PlayerDTO();
        playerDTO.setPlayer(player);
        PLAYER_MAP.put(player.getId(), playerDTO);
    }

    /**
     * 懒加载机制读取玩家数据到缓存
     *
     * @param id 玩家ID
     * @return 玩家数据
     */
    public PlayerDTO getPlayerById(Long id) {
        PlayerDTO playerDTO = PLAYER_MAP.getOrDefault(id, null);
        if (Objects.isNull(playerDTO)) {
            playerDTO = PlayerService.getPlayerDTOById(id);
            if (Objects.isNull(playerDTO.getPlayer())) {
                throw PlayerExceptionEnum.PLAYER_NOT_FOUND.getException();
            }
            PLAYER_MAP.put(id, playerDTO);
        }
        return playerDTO;
    }

    public void flush(List<Long> ids) {
        List<Player> players = ids.stream().map(PLAYER_MAP::get).map(PlayerDTO::getPlayer).collect(Collectors.toList());
        players.forEach(PlayerService::updatePlayerById);
    }
}
