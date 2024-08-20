package com.dingCreator.astrology.behavior;

import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.cache.TeamCache;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.dto.organism.player.PlayerDTO;
import com.dingCreator.astrology.dto.organism.player.PlayerInfoDTO;
import com.dingCreator.astrology.dto.TeamDTO;
import com.dingCreator.astrology.entity.*;
import com.dingCreator.astrology.enums.LootBelongToEnum;
import com.dingCreator.astrology.enums.PlayerStatusEnum;
import com.dingCreator.astrology.enums.RankEnum;
import com.dingCreator.astrology.enums.exception.BattleExceptionEnum;
import com.dingCreator.astrology.enums.exception.DungeonExceptionEnum;
import com.dingCreator.astrology.exception.BusinessException;
import com.dingCreator.astrology.service.DungeonBossService;
import com.dingCreator.astrology.service.DungeonRecordService;
import com.dingCreator.astrology.service.DungeonService;
import com.dingCreator.astrology.util.BattleUtil;
import com.dingCreator.astrology.util.CdUtil;
import com.dingCreator.astrology.util.LootUtil;
import com.dingCreator.astrology.util.MapUtil;
import com.dingCreator.astrology.vo.BattleResultVO;
import com.dingCreator.astrology.vo.DungeonResultVO;
import com.dingCreator.astrology.vo.DungeonVO;

import java.util.*;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ding
 * @date 2024/2/20
 */
public class DungeonBehavior {

    /**
     * 探索副本
     *
     * @param playerId    玩家ID
     * @param dungeonName 副本名称
     * @return 探索结果
     */
    public DungeonResultVO exploreDungeon(Long playerId, String dungeonName) {
        PlayerInfoDTO playerInfoDTO = PlayerCache.getPlayerById(playerId);
        PlayerDTO playerDTO = playerInfoDTO.getPlayerDTO();

        PlayerBehavior.getInstance().flushStatus(playerDTO);
        // 判断玩家状态
        if (!PlayerStatusEnum.FREE.getCode().equals(PlayerBehavior.getInstance().getStatus(playerDTO))) {
            throw DungeonExceptionEnum.PLAYER_NOT_FREE.getException();
        }
        // 查询副本信息
        Dungeon dungeon = DungeonService.getByName(playerDTO.getMapId(), dungeonName);
        if (Objects.isNull(dungeon)) {
            throw DungeonExceptionEnum.DUNGEON_NOT_FOUND.getException();
        }
        if (playerDTO.getRank() > dungeon.getMaxRank()) {
            throw DungeonExceptionEnum.RANK_OVER_LIMIT.getException();
        }
        List<Long> playerIds;
        // CD计算
        if (playerInfoDTO.getTeam()) {
            TeamBehavior.getInstance().captainOnlyValidate(playerId);
            TeamDTO teamDTO = TeamCache.getTeamByPlayerId(playerId);
            playerIds = teamDTO.getMembers();
        } else {
            playerIds = Collections.singletonList(playerId);
        }
        List<DungeonRecord> dungeonRecordList = DungeonRecordService.queryList(playerIds, dungeon.getId());
        if (Objects.nonNull(dungeonRecordList) && !dungeonRecordList.isEmpty()) {
            dungeonRecordList.forEach(rec -> {
                long cd = CdUtil.getDuration(rec.getLastExploreTime(), dungeon.getFlushTime());
                if (cd > 0) {
                    throw new BusinessException(Constants.CD_EXCEPTION_PREFIX + "001", "CD:" + cd + "s");
                }
            });
        }
        // 新增探索记录
        DungeonRecordService.insertOrUpdate(playerIds, dungeon.getId(), new Date());
        // 处理对战
        List<DungeonBoss> bossList = DungeonBossService.getByDungeonId(dungeon.getId());
        DungeonResultVO dungeonResultVO = new DungeonResultVO();
        List<DungeonResultVO.DungeonBossResult> dungeonBossResultList = new ArrayList<>(bossList.size());

        Map<LootBelongToEnum, List<Long>> lootMap = new HashMap<>(bossList.size() + 1);
        boolean lose = false;
        for (DungeonBoss boss : bossList) {
            BattleResultVO resultVO;
            try {
                resultVO = BattleUtil.battlePVE(playerId, Collections.singletonList(boss.getMonsterId()), true);
            } catch (BusinessException e) {
                // 后续还有boss的情况下，已经重伤了，直接判负
                if (e.equals(BattleExceptionEnum.INITIATOR_LOW_HP.getException())) {
                    lose = true;
                    break;
                }
                throw e;
            }
            DungeonResultVO.DungeonBossResult bossResult = new DungeonResultVO.DungeonBossResult();
            bossResult.setBattleResultVO(resultVO);
            dungeonBossResultList.add(bossResult);

            if (BattleResultVO.BattleResult.WIN.equals(resultVO.getBattleResult())) {
                List<Long> belongToIdList = lootMap.getOrDefault(LootBelongToEnum.DungeonBoss, new ArrayList<>());
                belongToIdList.add(boss.getId());
                if (!lootMap.containsKey(LootBelongToEnum.DungeonBoss)) {
                    lootMap.put(LootBelongToEnum.DungeonBoss, belongToIdList);
                }
            } else {
                dungeonResultVO.setExploreResult(BattleResultVO.BattleResult.LOSE);
                lose = true;
                break;
            }
        }
        dungeonResultVO.setExploreResult(lose ? BattleResultVO.BattleResult.LOSE : BattleResultVO.BattleResult.WIN);
        if (!lose) {
            List<Long> belongToIdList = lootMap.getOrDefault(LootBelongToEnum.Dungeon, new ArrayList<>());
            belongToIdList.add(dungeon.getId());
            if (!lootMap.containsKey(LootBelongToEnum.Dungeon)) {
                lootMap.put(LootBelongToEnum.Dungeon, belongToIdList);
            }
        }
        dungeonResultVO.setLootMap(LootUtil.getLoot(lootMap, playerIds));
        dungeonResultVO.setDungeonBossResultList(dungeonBossResultList);
        return dungeonResultVO;
    }

    /**
     * 获取副本列表
     *
     * @param playerId 玩家ID
     * @return 副本信息
     */
    public List<String> listDungeon(Long playerId) {
        PlayerBehavior.getInstance().flushStatus(PlayerCache.getPlayerById(playerId).getPlayerDTO());
        List<Dungeon> dungeonList = DungeonService.list(MapUtil.getNowLocation(playerId));
        return dungeonList.stream().map(d -> d.getName() + " 冷却时间：" + d.getFlushTime() + "s")
                .collect(Collectors.toList());
    }

    /**
     * 获取副本信息
     *
     * @param playerId    玩家ID
     * @param dungeonName 副本名称
     * @return 副本信息
     */
    public DungeonVO getDungeonInfoByName(Long playerId, String dungeonName) {
        PlayerDTO playerDTO = PlayerCache.getPlayerById(playerId).getPlayerDTO();
        PlayerBehavior.getInstance().flushStatus(playerDTO);
        Dungeon dungeon = DungeonService.getByName(MapUtil.getNowLocation(playerId), dungeonName);
        if (Objects.isNull(dungeon)) {
            throw DungeonExceptionEnum.DUNGEON_NOT_FOUND.getException();
        }
        DungeonVO vo = new DungeonVO();
        vo.setDungeonName(dungeon.getName());
        vo.setFlushTime(dungeon.getFlushTime());
        vo.setMaxRank(RankEnum.getEnum(playerDTO.getJob(), dungeon.getMaxRank()).getRankName());
        return vo;
    }

    /**
     * 创建副本
     *
     * @param mapName     地图名称
     * @param dungeonName 副本名称
     * @param maxRank     最高阶级
     * @param flushTime   刷新时间
     */
    public void createDungeon(String mapName, String dungeonName, Integer maxRank, Long flushTime) {

    }

    public void addLoot(String mapName, String dungeonName, Integer rate, String lootType) {

    }

    public void deleteLoot() {

    }

    private static class Holder {
        private static final DungeonBehavior BEHAVIOR = new DungeonBehavior();
    }

    private DungeonBehavior() {

    }

    public static DungeonBehavior getInstance() {
        return Holder.BEHAVIOR;
    }
}
