package com.dingCreator.immortal.cache;

import com.dingCreator.immortal.data.PlayerData;
import com.dingCreator.immortal.dto.PlayerDTO;
import com.dingCreator.immortal.entity.Player;
import com.dingCreator.immortal.enums.exception.PlayerExceptionEnum;

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

    private PlayerData playerData;

    public void createPlayer(Player player) {
        if (PLAYER_MAP.containsKey(player.getId())) {
            throw PlayerExceptionEnum.PLAYER_EXIST.getException();
        }

        // todo 持久化数据
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
            playerDTO = playerData.getPlayerById(id);
            if (Objects.isNull(playerDTO)) {
                throw PlayerExceptionEnum.PLAYER_EXIST.getException();
            }
            PLAYER_MAP.put(id, playerDTO);
        }
        return playerDTO;
    }

    public void flush(List<Long> ids) {
        List<Player> players = ids.stream().map(PLAYER_MAP::get).map(PlayerDTO::getPlayer).collect(Collectors.toList());
        playerData.batchUpdateById(players);
    }
}
