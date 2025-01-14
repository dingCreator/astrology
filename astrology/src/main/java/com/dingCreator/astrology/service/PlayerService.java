package com.dingCreator.astrology.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dingCreator.astrology.cache.TeamCache;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.database.DatabaseProvider;
import com.dingCreator.astrology.dto.equipment.EquipmentBarDTO;
import com.dingCreator.astrology.dto.organism.player.PlayerDTO;
import com.dingCreator.astrology.dto.organism.player.PlayerInfoDTO;
import com.dingCreator.astrology.dto.organism.player.PlayerAssetDTO;
import com.dingCreator.astrology.entity.Player;
import com.dingCreator.astrology.entity.PlayerAsset;
import com.dingCreator.astrology.enums.AssetTypeEnum;
import com.dingCreator.astrology.enums.BelongToEnum;
import com.dingCreator.astrology.enums.equipment.EquipmentEnum;
import com.dingCreator.astrology.enums.exception.PlayerExceptionEnum;
import com.dingCreator.astrology.mapper.PlayerAssetMapper;
import com.dingCreator.astrology.mapper.PlayerDataMapper;
import com.dingCreator.astrology.util.EquipmentUtil;
import com.dingCreator.astrology.util.LockUtil;
import org.apache.ibatis.session.SqlSession;

import java.util.List;
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
    public Player getPlayerById(Long id) {
        return DatabaseProvider.getInstance().executeReturn(sqlSession ->
                sqlSession.getMapper(PlayerDataMapper.class).getPlayerById(id));
    }

    /**
     * 获取玩家基本信息
     *
     * @param name 玩家名称
     * @return 玩家基本信息
     */
    public Player getPlayerByName(String name) {
        return DatabaseProvider.getInstance().executeReturn(sqlSession ->
                sqlSession.getMapper(PlayerDataMapper.class).getPlayerByName(name));
    }

    /**
     * 获取玩家信息
     *
     * @param id 玩家ID
     * @return 玩家信息
     */
    public PlayerInfoDTO getPlayerDTOById(Long id) {
        Player player = getPlayerById(id);
        if (Objects.isNull(player)) {
            return null;
        }

        PlayerInfoDTO playerInfoDTO = new PlayerInfoDTO();
        // 初始化装备
        EquipmentBarDTO barDTO = new EquipmentBarDTO();
        EquipmentBelongToService.getInstance().getBelongToIdEquip(BelongToEnum.PLAYER.getBelongTo(), id, true)
                .forEach(equipmentBelongTo -> {
                    EquipmentEnum equipmentEnum = EquipmentEnum.getById(equipmentBelongTo.getEquipmentId());
                    EquipmentUtil.setEquipmentBarDTO(barDTO, equipmentEnum, equipmentBelongTo);
                });
        playerInfoDTO.setEquipmentBarDTO(barDTO);
        // 初始化称号

        // 初始化资产
        PlayerAsset asset = getAssetByPlayerId(id);
        playerInfoDTO.setPlayerAssetDTO(asset.convert());

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
    public synchronized boolean createPlayer(Player player, PlayerAsset asset) {
        return DatabaseProvider.getInstance().transactionExecuteReturn(sqlSession -> {
            sqlSession.getMapper(PlayerDataMapper.class).insert(player);
            sqlSession.getMapper(PlayerAssetMapper.class).insert(asset);
            return true;
        });
    }

    /**
     * 根据ID更新玩家信息
     *
     * @param player 玩家基本信息
     */
    public void updatePlayerById(Player player) {
        DatabaseProvider.getInstance().execute(sqlSession ->
                sqlSession.getMapper(PlayerDataMapper.class).updateById(player));
    }

    /**
     * 根据ID更新玩家信息
     * @param playerList 玩家列表
     */
    public void updatePlayerByIds(List<Player> playerList) {
        DatabaseProvider.getInstance().batchTransactionExecute(sqlSession -> {
            PlayerDataMapper mapper = sqlSession.getMapper(PlayerDataMapper.class);
            for (Player player : playerList) {
                mapper.updateById(player);
            }
        });
    }

    public PlayerAsset getAssetByPlayerId(Long playerId) {
        return DatabaseProvider.getInstance().executeReturn(sqlSession -> sqlSession.getMapper(PlayerAssetMapper.class)
                .selectOne(new QueryWrapper<PlayerAsset>().eq(PlayerAsset.PLAYER_ID, playerId)));
    }

    public void changeAsset(PlayerInfoDTO infoDTO, PlayerAssetDTO change) {
        LockUtil.execute(Constants.CHANGE_ASSET_LOCK_PREFIX + infoDTO.getPlayerDTO().getId(), () ->
                DatabaseProvider.getInstance().batchTransactionExecute(sqlSession -> {
                    PlayerAssetMapper playerAssetMapper = sqlSession.getMapper(PlayerAssetMapper.class);
                    PlayerAsset asset = playerAssetMapper.selectOne(
                            new QueryWrapper<PlayerAsset>().eq(PlayerAsset.PLAYER_ID, infoDTO.getPlayerDTO().getId()));
                    // 获取货币
                    long astrologyCoin = asset.getAstrologyCoin();
                    long diamond = asset.getDiamond();
                    // 圣星币
                    if (Objects.nonNull(change.getAstrologyCoin())) {
                        astrologyCoin += change.getAstrologyCoin();
                        if (astrologyCoin < 0) {
                            throw PlayerExceptionEnum.NOT_ENOUGH_ASTROLOGY_COIN.getException();
                        }
                        asset.setAstrologyCoin(astrologyCoin);
                    }
                    // 缘石
                    if (Objects.nonNull(change.getDiamond())) {
                        diamond += change.getDiamond();
                        if (diamond < 0) {
                            throw PlayerExceptionEnum.NOT_ENOUGH_DIAMOND.getException();
                        }
                        asset.setDiamond(diamond);
                    }
                    // 持久化
                    playerAssetMapper.updateById(asset);
                    // 更新缓存
                    infoDTO.getPlayerAssetDTO().setAstrologyCoin(astrologyCoin);
                    infoDTO.getPlayerAssetDTO().setDiamond(diamond);
                })
        );
    }

    private static class Holder {
        private static final PlayerService SERVICE = new PlayerService();
    }

    private PlayerService() {

    }

    public static PlayerService getInstance() {
        return PlayerService.Holder.SERVICE;
    }
}
