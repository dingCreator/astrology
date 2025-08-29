package com.dingCreator.astrology.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dingCreator.astrology.cache.TeamCache;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.database.DatabaseProvider;
import com.dingCreator.astrology.dto.equipment.EquipmentBarDTO;
import com.dingCreator.astrology.dto.organism.player.PlayerDTO;
import com.dingCreator.astrology.dto.organism.player.PlayerInfoDTO;
import com.dingCreator.astrology.dto.organism.player.PlayerAssetDTO;
import com.dingCreator.astrology.entity.Player;
import com.dingCreator.astrology.entity.PlayerAsset;
import com.dingCreator.astrology.entity.SkillBarItem;
import com.dingCreator.astrology.enums.AssetTypeEnum;
import com.dingCreator.astrology.enums.BelongToEnum;
import com.dingCreator.astrology.enums.equipment.EquipmentEnum;
import com.dingCreator.astrology.enums.exception.PlayerExceptionEnum;
import com.dingCreator.astrology.enums.job.JobEnum;
import com.dingCreator.astrology.enums.skill.SkillEnum;
import com.dingCreator.astrology.mapper.PlayerAssetMapper;
import com.dingCreator.astrology.mapper.PlayerDataMapper;
import com.dingCreator.astrology.response.PageResponse;
import com.dingCreator.astrology.util.EquipmentUtil;
import com.dingCreator.astrology.util.LockUtil;
import com.dingCreator.astrology.util.PageUtil;
import com.dingCreator.astrology.util.SkillUtil;
import com.dingCreator.astrology.vo.PlayerLevelChartVO;
import org.apache.ibatis.session.SqlSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        List<PlayerAsset> assetList = getAssetByPlayerId(id);
        playerInfoDTO.setAssetList(assetList.stream().map(PlayerAsset::convert).collect(Collectors.toList()));

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
    public synchronized boolean createPlayer(Player player, List<PlayerAsset> assetList) {
        return DatabaseProvider.getInstance().transactionExecuteReturn(sqlSession -> {
            sqlSession.getMapper(PlayerDataMapper.class).insert(player);
            assetList.forEach(asset -> sqlSession.getMapper(PlayerAssetMapper.class).insert(asset));
            // 赠送默认技能
            Long defaultSkillId = SkillEnum.getDefaultSkillByJob(player.getJob()).getId();
            SkillBelongToService.getInstance().createSkillBelongTo(BelongToEnum.PLAYER.getBelongTo(), player.getId(), defaultSkillId);
            // 赠送被动技能
            SkillBelongToService.getInstance().createSkillBelongTo(BelongToEnum.PLAYER.getBelongTo(), player.getId(),
                    JobEnum.getByCode(player.getJob()).getDefaultInactiveSkillId());
            // 将默认技能装备到技能栏
            List<Long> skillIds = new ArrayList<>();
            skillIds.add(SkillEnum.getDefaultSkillByJob(player.getJob()).getId());
            SkillBarItem skillBarItem = SkillUtil.buildSkillBarItemChain(skillIds, BelongToEnum.PLAYER, player.getId());
            SkillBarItemService.getInstance().addSkillBarItem(skillBarItem);
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
     *
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

    public List<PlayerAsset> getAssetByPlayerId(Long playerId) {
        return DatabaseProvider.getInstance().executeReturn(sqlSession -> sqlSession.getMapper(PlayerAssetMapper.class)
                .selectList(new QueryWrapper<PlayerAsset>().eq(PlayerAsset.PLAYER_ID, playerId)));
    }

    public void changeAsset(PlayerInfoDTO infoDTO, List<PlayerAssetDTO> changeList) {
        LockUtil.execute(Constants.CHANGE_ASSET_LOCK_PREFIX + infoDTO.getPlayerDTO().getId(), () ->
                DatabaseProvider.getInstance().batchTransactionExecute(sqlSession -> {
                    List<PlayerAssetDTO> nonZeroChangeList = changeList.stream()
                            .filter(change -> change.getAssetCnt() != 0).collect(Collectors.toList());
                    if (CollectionUtils.isEmpty(nonZeroChangeList)) {
                        return;
                    }
                    // 获取资产
                    PlayerAssetMapper playerAssetMapper = sqlSession.getMapper(PlayerAssetMapper.class);
                    List<PlayerAsset> assetList = playerAssetMapper.selectList(
                            new QueryWrapper<PlayerAsset>().eq(PlayerAsset.PLAYER_ID, infoDTO.getPlayerDTO().getId()));
                    Map<String, PlayerAsset> assetMap = assetList.stream()
                            .collect(Collectors.toMap(PlayerAsset::getAssetType, Function.identity()));
                    nonZeroChangeList.forEach(change -> {
                        PlayerAsset playerAsset = assetMap.get(change.getAssetType());
                        if (Objects.isNull(playerAsset)) {
                            playerAsset = PlayerAsset.builder().playerId(infoDTO.getPlayerDTO().getId())
                                    .assetType(change.getAssetType()).assetCnt(0L).build();
                            if (change.getAssetCnt() > 0) {
                                playerAsset.setAssetCnt(change.getAssetCnt());
                            }
                            playerAssetMapper.insert(playerAsset);
                            assetList.add(playerAsset);
                            if (change.getAssetCnt() < 0) {
                                throw AssetTypeEnum.getByCode(change.getAssetType()).getNotEnoughException().getException();
                            }
                        } else {
                            long val = playerAsset.getAssetCnt() + change.getAssetCnt();
                            if (val < 0) {
                                throw AssetTypeEnum.getByCode(change.getAssetType()).getNotEnoughException().getException();
                            }
                            playerAsset.setAssetCnt(val);
                            // 持久化
                            playerAssetMapper.updateById(playerAsset);
                        }
                    });
                    // 更新缓存
                    infoDTO.setAssetList(assetList.stream().map(PlayerAsset::convert).collect(Collectors.toList()));
                })
        );
    }

    public PageResponse<PlayerLevelChartVO> queryChartPage(int pageIndex, int pageSize, Long playerId) {
        int index = pageIndex - 1;
        return DatabaseProvider.getInstance().executeReturn(sqlSession -> {
            QueryWrapper<Player> wrapper = new QueryWrapper<>();
            int count = sqlSession.getMapper(PlayerDataMapper.class).selectCount(wrapper);
            List<PlayerLevelChartVO> list = sqlSession.getMapper(PlayerDataMapper.class).queryChartPage(
                    index * pageSize, pageSize, playerId);
            return PageUtil.addPageDesc(list, pageIndex, pageSize, count);
        });
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
