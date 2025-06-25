package com.dingCreator.astrology.service;

import cn.hutool.core.collection.CollectionUtil;
import com.dingCreator.astrology.behavior.TeamBehavior;
import com.dingCreator.astrology.cache.DungeonCache;
import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.cache.TeamCache;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.database.DatabaseProvider;
import com.dingCreator.astrology.dto.DungeonConfigSettingsDTO;
import com.dingCreator.astrology.dto.loot.LootDTO;
import com.dingCreator.astrology.dto.TeamDTO;
import com.dingCreator.astrology.dto.loot.LootQueryDTO;
import com.dingCreator.astrology.dto.organism.player.PlayerInfoDTO;
import com.dingCreator.astrology.entity.Dungeon;
import com.dingCreator.astrology.entity.DungeonConfig;
import com.dingCreator.astrology.entity.DungeonRecord;
import com.dingCreator.astrology.entity.Loot;
import com.dingCreator.astrology.enums.DungeonExploreStatusEnum;
import com.dingCreator.astrology.enums.LootBelongToEnum;
import com.dingCreator.astrology.enums.PlayerStatusEnum;
import com.dingCreator.astrology.enums.exception.DungeonExceptionEnum;
import com.dingCreator.astrology.exception.BusinessException;
import com.dingCreator.astrology.mapper.DungeonConfigMapper;
import com.dingCreator.astrology.mapper.DungeonMapper;
import com.dingCreator.astrology.util.*;
import com.dingCreator.astrology.vo.BattleResultVO;
import com.dingCreator.astrology.vo.DungeonResultVO;
import com.dingCreator.astrology.vo.LootVO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author ding
 * @date 2024/4/4
 */
public class DungeonService {

    /**
     * 通过ID获取副本
     *
     * @param dungeonId 副本ID
     * @return 副本信息
     */
    public Dungeon getById(Long dungeonId) {
        return DatabaseProvider.getInstance().executeReturn(sqlSession ->
                sqlSession.getMapper(DungeonMapper.class).getById(dungeonId));
    }

    /**
     * 通过名称获取副本
     *
     * @param dungeonName 副本名称
     * @return 副本信息
     */
    public Dungeon getByName(String dungeonName) {
        return DatabaseProvider.getInstance().executeReturn(sqlSession ->
                sqlSession.getMapper(DungeonMapper.class).getByName(dungeonName));
    }

    /**
     * 获取副本
     *
     * @return 副本信息
     */
    public List<Dungeon> list() {
        return DatabaseProvider.getInstance().transactionExecuteReturn(sqlSession ->
                sqlSession.getMapper(DungeonMapper.class).list());
    }

    /**
     * 开始探索
     *
     * @param playerId 玩家ID
     * @param index    副本编号
     * @return 探索结果
     */
    public DungeonResultVO startExploreDungeon(Long playerId, int index) {
        return DatabaseProvider.getInstance().transactionExecuteReturn(sqlSession -> {
            // 获取探索玩家id
            List<Long> playerIds = getExplorePlayerIds(playerId);
            // 判断玩家状态
            playerIds.forEach(id -> {
                if (!PlayerStatusEnum.FREE.getCode().equals(PlayerCache.getPlayerById(id).getPlayerDTO().getStatus())) {
                    throw DungeonExceptionEnum.PLAYER_NOT_FREE.getException();
                }
            });
            // 查询副本信息
            Dungeon dungeon = getDungeon(playerIds, index);
            if (RandomUtil.isHit(dungeon.getPassRate().floatValue())) {
                DungeonResultVO vo = exploreSuccess(playerIds, dungeon.getId(), -1, false, new HashMap<>());
                vo.setExploreResult(DungeonResultVO.ExploreResult.PASS);
                return vo;
            }
            return doExplore(playerId, playerIds, dungeon);
        });
    }

    /**
     * 继续探索副本
     *
     * @param playerId 玩家ID
     * @return 探索结果
     */
    public DungeonResultVO continueExploreDungeon(Long playerId) {
        return DatabaseProvider.getInstance().transactionExecuteReturn(sqlSession -> {
            // 获取探索玩家id
            List<Long> playerIds = getExplorePlayerIds(playerId);
            // 判断玩家状态
            playerIds.forEach(id -> {
                if (!PlayerStatusEnum.EXPLORE.getCode().equals(PlayerCache.getPlayerById(id).getPlayerDTO().getStatus())) {
                    throw DungeonExceptionEnum.NOT_EXPLORING.getException();
                }
            });
            // 查询正在进行的副本信息
            DungeonRecord dungeonRecord = DungeonRecordService.getInstance().queryLastExplore(playerId);
            if (Objects.isNull(dungeonRecord)) {
                throw DungeonExceptionEnum.NOT_EXPLORING.getException();
            }
            Dungeon dungeon = DungeonService.getInstance().getById(dungeonRecord.getDungeonId());
            return doExplore(playerId, playerIds, dungeon);
        });
    }

    public DungeonResultVO stopExploreDungeon(Long playerId) {
        return DatabaseProvider.getInstance().transactionExecuteReturn(sqlSession -> {
            // 获取探索玩家id
            List<Long> playerIds = getExplorePlayerIds(playerId);
            // 判断玩家状态
            playerIds.forEach(id -> {
                if (!PlayerStatusEnum.EXPLORE.getCode().equals(PlayerCache.getPlayerById(id).getPlayerDTO().getStatus())) {
                    throw DungeonExceptionEnum.NOT_EXPLORING.getException();
                }
            });
            // 查询正在进行的副本信息
            DungeonRecord dungeonRecord = DungeonRecordService.getInstance().queryLastExplore(playerId);
            if (Objects.isNull(dungeonRecord)) {
                throw DungeonExceptionEnum.NOT_EXPLORING.getException();
            }
            Dungeon dungeon = DungeonService.getInstance().getById(dungeonRecord.getDungeonId());
            return exploreFail(playerIds, dungeon.getId(), dungeonRecord.getFloor() + 1, new HashMap<>());
        });
    }

    /**
     * 获取探索人员
     *
     * @param playerId 发起探索者
     * @return 探索人员
     */
    private List<Long> getExplorePlayerIds(Long playerId) {
        PlayerInfoDTO playerInfoDTO = PlayerCache.getPlayerById(playerId);
        List<Long> playerIds;
        if (playerInfoDTO.getTeam()) {
            TeamBehavior.getInstance().captainOnlyValidate(playerId);
            TeamDTO teamDTO = TeamCache.getTeamById(playerId);
            playerIds = teamDTO.getMembers();
        } else {
            playerIds = Collections.singletonList(playerId);
        }
        return playerIds;
    }

    /**
     * 获取副本
     *
     * @param playerIds 玩家ID
     * @param index     下标
     * @return 副本
     */
    private Dungeon getDungeon(List<Long> playerIds, int index) {
        // 获取副本
        List<Dungeon> dungeonList = list();
        if (CollectionUtil.isEmpty(dungeonList)) {
            throw DungeonExceptionEnum.NO_DUNGEON_CAN_EXPLORE.getException();
        }
        if (index < 0 || index > dungeonList.size()) {
            throw DungeonExceptionEnum.DUNGEON_NOT_FOUND.getException();
        }
        Dungeon dungeon;
        if (index == 0) {
            dungeon = dungeonList.stream()
                    .filter(d -> playerIds.stream().allMatch(id ->
                            d.getMaxRank() >= PlayerCache.getPlayerById(id).getPlayerDTO().getRank()))
                    .filter(d -> getCd(playerIds, d) <= 0)
                    .findFirst().orElse(null);
            if (Objects.isNull(dungeon)) {
                throw DungeonExceptionEnum.NO_DUNGEON_CAN_EXPLORE.getException();
            }
        } else {
            dungeon = dungeonList.get(index - 1);
            if (playerIds.stream()
                    .anyMatch(id -> PlayerCache.getPlayerById(id).getPlayerDTO().getRank() > dungeon.getMaxRank())) {
                throw DungeonExceptionEnum.RANK_OVER_LIMIT.getException();
            }
            // CD计算
            long cd = getCd(playerIds, dungeon);
            if (cd > 0) {
                throw new BusinessException(Constants.CD_EXCEPTION_PREFIX + "001", "CD:" + cd + "s");
            }
        }
        return dungeon;
    }

    /**
     * 获取探索CD
     *
     * @param playerIds 玩家ID
     * @param dungeon   副本
     * @return 剩余CD
     */
    private long getCd(List<Long> playerIds, Dungeon dungeon) {
        AtomicLong maxCd = new AtomicLong(0L);
        List<Long> noCachePlayerIds = playerIds.stream().filter(playerId -> {
            LocalDateTime lastExploreTime = DungeonCache.getLastExploreTime(playerId, dungeon.getId());
            if (Objects.isNull(lastExploreTime)) {
                return true;
            }
            long cd = CdUtil.getDuration(lastExploreTime, dungeon.getFlushTime());
            if (cd > 0) {
                maxCd.set(Math.max(maxCd.get(), cd));
            }
            return false;
        }).collect(Collectors.toList());

        if (CollectionUtil.isNotEmpty(noCachePlayerIds)) {
            List<DungeonRecord> dungeonRecordList =
                    DungeonRecordService.getInstance().queryLastExplore(noCachePlayerIds, dungeon.getId());
            if (Objects.nonNull(dungeonRecordList) && !dungeonRecordList.isEmpty()) {
                // 未完成的探索无需计算CD
                dungeonRecordList.stream()
                        .filter(rec -> !rec.getExploreStatus().equals(DungeonExploreStatusEnum.EXPLORE.getCode()))
                        .forEach(rec -> {
                            long cd = CdUtil.getDuration(rec.getLastExploreTime(), dungeon.getFlushTime());
                            if (cd > 0) {
                                maxCd.set(Math.max(maxCd.get(), cd));
                            }
                        });
            }
        }
        return maxCd.get();
    }

    private DungeonResultVO doExplore(Long playerId, List<Long> playerIds, Dungeon dungeon) {
        // boss解析
        List<DungeonConfig> configList = DungeonConfigService.getInstance().getByDungeonId(dungeon.getId());
        if (CollectionUtil.isEmpty(configList)) {
            throw DungeonExceptionEnum.DUNGEON_NOT_FOUND.getException();
        }
        int maxFloor = configList.stream().mapToInt(DungeonConfig::getFloor)
                .max().orElseThrow(() -> new IllegalArgumentException("副本配置有误"));
        // 探索过程
        // 初始化掉落物
        Map<Long, LootVO> totalLootMap = new HashMap<>();
        // 1. 获取探索记录 因为只允许队长操作，所以只查询队长的记录即可
        DungeonRecord record = DungeonRecordService.getInstance().queryLastExplore(playerId, dungeon.getId());
        // 2. 获取层数和结果
        int floor;
        if (Objects.isNull(record)) {
            floor = 0;
        } else {
            floor = DungeonExploreStatusEnum.EXPLORE.getCode().equals(record.getExploreStatus()) ? record.getFloor() : 0;
        }
        if (floor >= maxFloor) {
            return exploreSuccess(playerIds, dungeon.getId(), maxFloor, false, totalLootMap);
        }
        int nextFloor = floor + 1;
        // 3. 开始探索
        List<DungeonConfig> floorConfigList = configList.stream().filter(c -> c.getFloor().equals(nextFloor))
                .collect(Collectors.toList());
        if (CollectionUtil.isEmpty(floorConfigList)) {
            return exploreSuccess(playerIds, dungeon.getId(), nextFloor, true, totalLootMap);
        }

        Map<Integer, List<DungeonConfig>> waveConfigMap = floorConfigList.stream()
                .sorted(Comparator.comparing(DungeonConfig::getWave))
                .collect(Collectors.groupingBy(
                        DungeonConfig::getWave, TreeMap::new, Collectors.mapping(Function.identity(), Collectors.toList())
                ));
        Map<Integer, LootQueryDTO> waveLootMap = LootService.getInstance()
                .getByBelongToId(LootBelongToEnum.DUNGEON_WAVE.getBelongTo(), dungeon.getId()).stream()
                .filter(loot -> {
                    String[] args = loot.getExtInfo().split(Constants.COMMA);
                    return String.valueOf(nextFloor).equals(args[0]);
                })
                .collect(Collectors.toMap(loot -> {
                    String[] args = loot.getExtInfo().split(Constants.COMMA);
                    return Integer.parseInt(args[1]);
                }, Function.identity()));
        boolean pass = waveConfigMap.values().stream().allMatch(waveConfigs -> {
            List<Long> monsterIds = waveConfigs.stream().map(DungeonConfig::getMonsterId).collect(Collectors.toList());
            BattleResultVO resultVO = BattleUtil.battlePVE(playerId, monsterIds, true, false);
            boolean innerPass = BattleResultVO.BattleResult.WIN.equals(resultVO.getBattleResult());
            if (innerPass) {
                LootQueryDTO lootQueryDTO = waveLootMap.get(waveConfigs.get(0).getWave());
                if (Objects.nonNull(lootQueryDTO)) {
                    totalLootMap.putAll(LootUtil.sendLoot(lootQueryDTO, playerIds));
                }
            }
            return innerPass;
        });
        if (pass) {
            return exploreSuccess(playerIds, dungeon.getId(), nextFloor, nextFloor < maxFloor, totalLootMap);
        } else {
            return exploreFail(playerIds, dungeon.getId(), nextFloor, totalLootMap);
        }
    }

    private DungeonResultVO exploreSuccess(List<Long> playerIds, Long dungeonId, int floor, boolean hasNextFloor,
                                           Map<Long, LootVO> totalLootMap) {
        DungeonResultVO dungeonResultVO = new DungeonResultVO();
        // 层数奖励
        List<LootQueryDTO> floorLootList
                = LootService.getInstance().getByBelongToId(LootBelongToEnum.DUNGEON_FLOOR.getBelongTo(), dungeonId);
        if (CollectionUtil.isNotEmpty(floorLootList)) {
            floorLootList.stream()
                    .filter(loot -> String.valueOf(floor).equals(loot.getExtInfo()))
                    .peek(loot -> totalLootMap.putAll(LootUtil.sendLoot(loot, playerIds)))
                    .findFirst();
        }
        // 新增探索记录
        if (hasNextFloor) {
            DungeonRecordService.getInstance().insertOrUpdate(playerIds, dungeonId, LocalDateTime.now(), floor,
                    DungeonExploreStatusEnum.EXPLORE.getCode());
            dungeonResultVO.setExploreResult(DungeonResultVO.ExploreResult.COMPLETE);
            // 状态变更为探索中
            playerIds.forEach(id -> PlayerCache.getPlayerById(id).getPlayerDTO().setStatus(PlayerStatusEnum.EXPLORE.getCode()));
            PlayerCache.save(playerIds);
        } else {
            // 探索完成
            DungeonRecordService.getInstance().insertOrUpdate(playerIds, dungeonId, LocalDateTime.now(), floor,
                    DungeonExploreStatusEnum.COMPLETE.getCode());
            // 发奖
            List<LootQueryDTO> dungeonLootList
                    = LootService.getInstance().getByBelongToId(LootBelongToEnum.DUNGEON.getBelongTo(), dungeonId);
            if (CollectionUtil.isNotEmpty(dungeonLootList)) {
                totalLootMap.putAll(LootUtil.sendLoot(dungeonLootList.get(0), playerIds));
            }
            // 修改状态
            playerIds.forEach(id -> PlayerCache.getPlayerById(id).getPlayerDTO().setStatus(PlayerStatusEnum.FREE.getCode()));
            PlayerCache.save(playerIds);
            // 修改副本参与时间缓存
            playerIds.forEach(id -> DungeonCache.putLastExploreTime(id, dungeonId, LocalDateTime.now()));
            dungeonResultVO.setExploreResult(DungeonResultVO.ExploreResult.SUCCESS);
        }

        dungeonResultVO.setPlayerLootMap(totalLootMap);
        return dungeonResultVO;
    }

    private DungeonResultVO exploreFail(List<Long> playerIds, Long dungeonId, int floor, Map<Long, LootVO> totalLootMap) {
        // 修改副本参与时间缓存
        playerIds.forEach(id -> DungeonCache.putLastExploreTime(id, dungeonId, LocalDateTime.now()));
        DungeonRecordService.getInstance().insertOrUpdate(playerIds, dungeonId, LocalDateTime.now(), floor,
                DungeonExploreStatusEnum.FAIL.getCode());
        // 修改状态
        playerIds.forEach(id -> PlayerCache.getPlayerById(id).getPlayerDTO().setStatus(PlayerStatusEnum.FREE.getCode()));
        PlayerCache.save(playerIds);
        DungeonResultVO dungeonResultVO = new DungeonResultVO();
        dungeonResultVO.setExploreResult(DungeonResultVO.ExploreResult.FAIL);
        dungeonResultVO.setPlayerLootMap(totalLootMap);
        return dungeonResultVO;
    }

    public void createDungeon(String dungeonName, Integer maxRank, Long flushTime, BigDecimal passRate,
                              DungeonConfigSettingsDTO setting) {
        DatabaseProvider.getInstance().transactionExecute(sqlSession -> {
            Dungeon dungeon = new Dungeon();
            dungeon.setName(dungeonName);
            dungeon.setMaxRank(maxRank);
            dungeon.setFlushTime(flushTime);
            dungeon.setPassRate(passRate);
            sqlSession.getMapper(DungeonMapper.class).insert(dungeon);

            LootService.getInstance().createLoot(LootBelongToEnum.DUNGEON.getBelongTo(), dungeon.getId(), setting.getDungeonLoot());
            setting.getFloorList().forEach(floor -> {
                LootService.getInstance().createLoot(LootBelongToEnum.DUNGEON_FLOOR.getBelongTo(),
                        dungeon.getId(), floor.getFloorLoot());
                floor.getWaveList().forEach(wave -> {
                    LootService.getInstance().createLoot(LootBelongToEnum.DUNGEON_WAVE.getBelongTo(),
                            dungeon.getId(), wave.getWaveLoot());
                    wave.getMonsterIds().forEach(monsterId -> {
                        DungeonConfig config = new DungeonConfig();
                        config.setDungeonId(dungeon.getId());
                        config.setFloor(floor.getFloor());
                        config.setWave(wave.getWave());
                        config.setMonsterId(monsterId);
                        config.setCnt(1);
                        sqlSession.getMapper(DungeonConfigMapper.class).insert(config);
                    });
                });
            });
        });
    }

    private static class Holder {
        private static final DungeonService SERVICE = new DungeonService();
    }

    private DungeonService() {

    }

    public static DungeonService getInstance() {
        return DungeonService.Holder.SERVICE;
    }
}
