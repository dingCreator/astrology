package com.dingCreator.astrology.service;

import com.dingCreator.astrology.database.DatabaseProvider;
import com.dingCreator.astrology.dto.PlayerDTO;
import com.dingCreator.astrology.entity.Player;

/**
 * @author ding
 * @date 2024/2/20
 */
public class PlayerService {
    /**
     * 获取玩家基本信息
     *
     * @param id 玩家ID
     * @return 玩家基本信息
     */
    public static Player getPlayerById(Long id) {
        return DatabaseProvider.getInstance().getPlayerDataMapper().getPlayerById(id);
    }

    /**
     * 获取玩家信息
     *
     * @param id 玩家ID
     * @return 玩家信息
     */
    public static PlayerDTO getPlayerDTOById(Long id) {
        PlayerDTO playerDTO = new PlayerDTO();
        Player player = getPlayerById(id);
        playerDTO.setPlayer(player);
        return playerDTO;
    }

    /**
     * 创建新玩家
     *
     * @param player 玩家基础信息
     * @return 是否创建成功
     */
    public static boolean createPlayer(Player player) {
        player.setEnabled(true);
        player.setExp(0L);
        // todo 新手村地图ID
        player.setMapId(1L);
        player.setLevel(1);
        player.setRank(1);
        return DatabaseProvider.getInstance().getPlayerDataMapper().createPlayer(player);
    }

    /**
     * 根据ID更新玩家信息
     *
     * @param player 玩家基本信息
     * @return 是否更新成功
     */
    public static boolean updatePlayerById(Player player) {
        return DatabaseProvider.getInstance().getPlayerDataMapper().createPlayer(player);
    }
}