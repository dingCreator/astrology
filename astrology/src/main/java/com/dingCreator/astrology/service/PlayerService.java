package com.dingCreator.astrology.service;

import com.dingCreator.astrology.cache.TeamCache;
import com.dingCreator.astrology.database.DatabaseProvider;
import com.dingCreator.astrology.dto.equipment.EquipmentBarDTO;
import com.dingCreator.astrology.dto.organism.player.PlayerDTO;
import com.dingCreator.astrology.dto.organism.player.PlayerInfoDTO;
import com.dingCreator.astrology.entity.Player;
import com.dingCreator.astrology.enums.BelongToEnum;
import com.dingCreator.astrology.enums.equipment.EquipmentEnum;
import com.dingCreator.astrology.mapper.PlayerDataMapper;
import com.dingCreator.astrology.util.EquipmentUtil;

import java.util.Objects;

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
        return DatabaseProvider.getInstance().executeReturn(sqlSession ->
                sqlSession.getMapper(PlayerDataMapper.class).getPlayerById(id));
    }

    /**
     * 获取玩家基本信息
     *
     * @param name 玩家名称
     * @return 玩家基本信息
     */
    public static Player getPlayerByName(String name) {
        return DatabaseProvider.getInstance().executeReturn(sqlSession ->
                sqlSession.getMapper(PlayerDataMapper.class).getPlayerByName(name));
    }

    /**
     * 获取玩家信息
     *
     * @param id 玩家ID
     * @return 玩家信息
     */
    public static PlayerInfoDTO getPlayerDTOById(Long id) {
        PlayerInfoDTO playerInfoDTO = new PlayerInfoDTO();
        // 初始化装备
        EquipmentBarDTO barDTO = new EquipmentBarDTO();
        EquipmentBelongToService.getBelongToIdEquip(BelongToEnum.Player.getBelongTo(), id, true)
                .forEach(equipmentBelongTo -> {
                    EquipmentEnum equipmentEnum = EquipmentEnum.getById(equipmentBelongTo.getEquipmentId());
                    EquipmentUtil.setEquipmentBarDTO(barDTO, equipmentEnum, equipmentBelongTo);
                });
        playerInfoDTO.setEquipmentBarDTO(barDTO);
        // 初始化称号

        Player player = getPlayerById(id);
        PlayerDTO playerDTO = new PlayerDTO();
        playerDTO.copyProperties(player);
        playerInfoDTO.setPlayerDTO(playerDTO);
        playerInfoDTO.setTeam(Objects.nonNull(TeamCache.getTeamById(id)));
        return playerInfoDTO;
    }

    /**
     * 创建新玩家
     *
     * @param player 玩家基础信息
     * @return 是否创建成功
     */
    public static synchronized boolean createPlayer(Player player) {
        return DatabaseProvider.getInstance().executeReturn(sqlSession ->
                sqlSession.getMapper(PlayerDataMapper.class).createPlayer(player));
    }

    /**
     * 根据ID更新玩家信息
     *
     * @param player 玩家基本信息
     */
    public static void updatePlayerById(Player player) {
        DatabaseProvider.getInstance().execute(sqlSession ->
                sqlSession.getMapper(PlayerDataMapper.class).updateById(player));
    }
}
