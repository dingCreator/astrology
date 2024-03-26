package com.dingCreator.astrology.service;

import com.dingCreator.astrology.database.DatabaseProvider;
import com.dingCreator.astrology.mapper.PlayerDataMapper;
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
        return (Player) DatabaseProvider.getInstance().doExecute(sqlSession ->
                sqlSession.getMapper(PlayerDataMapper.class).getPlayerById(id));
    }

    /**
     * 获取玩家基本信息
     *
     * @param name 玩家名称
     * @return 玩家基本信息
     */
    public static Player getPlayerByName(String name) {
        return (Player) DatabaseProvider.getInstance().doExecute(sqlSession ->
                sqlSession.getMapper(PlayerDataMapper.class).getPlayerByName(name));
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
        return (Boolean) DatabaseProvider.getInstance().doExecute(sqlSession ->
                sqlSession.getMapper(PlayerDataMapper.class).createPlayer(player));
    }

    /**
     * 根据ID更新玩家信息
     *
     * @param player 玩家基本信息
     * @return 是否更新成功
     */
    public static boolean updatePlayerById(Player player) {
        return (Boolean) DatabaseProvider.getInstance().doExecute(sqlSession ->
                sqlSession.getMapper(PlayerDataMapper.class).updateById(player));
    }
}
