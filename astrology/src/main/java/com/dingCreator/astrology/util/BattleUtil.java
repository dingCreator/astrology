package com.dingCreator.astrology.util;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.cache.SkillCache;
import com.dingCreator.astrology.cache.TeamCache;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.dto.*;
import com.dingCreator.astrology.dto.equipment.EquipmentBarDTO;
import com.dingCreator.astrology.dto.equipment.EquipmentDTO;
import com.dingCreator.astrology.dto.organism.OrganismDTO;
import com.dingCreator.astrology.dto.organism.OrganismInfoDTO;
import com.dingCreator.astrology.dto.organism.monster.MonsterDTO;
import com.dingCreator.astrology.dto.organism.player.PlayerDTO;
import com.dingCreator.astrology.dto.organism.player.PlayerInfoDTO;
import com.dingCreator.astrology.dto.skill.SkillBarDTO;
import com.dingCreator.astrology.dto.skill.SkillEffectDTO;
import com.dingCreator.astrology.entity.base.Monster;
import com.dingCreator.astrology.enums.BelongToEnum;
import com.dingCreator.astrology.enums.BuffTypeEnum;
import com.dingCreator.astrology.enums.OrganismPropertiesEnum;
import com.dingCreator.astrology.enums.equipment.EquipmentEnum;
import com.dingCreator.astrology.enums.equipment.EquipmentPropertiesTypeEnum;
import com.dingCreator.astrology.enums.exception.BattleExceptionEnum;
import com.dingCreator.astrology.enums.exception.PlayerExceptionEnum;
import com.dingCreator.astrology.enums.exception.TeamExceptionEnum;
import com.dingCreator.astrology.enums.job.JobInitPropertiesEnum;
import com.dingCreator.astrology.enums.skill.DamageTypeEnum;
import com.dingCreator.astrology.enums.skill.SkillEnum;
import com.dingCreator.astrology.enums.skill.SkillTargetEnum;
import com.dingCreator.astrology.exception.BusinessException;
import com.dingCreator.astrology.service.MonsterService;
import com.dingCreator.astrology.util.function.FunctionExecutor;
import com.dingCreator.astrology.util.template.ExtraBattleProcessTemplate;
import com.dingCreator.astrology.vo.BattleResultVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * @author ding
 * @date 2024/2/3
 */
public class BattleUtil {

    /**
     * 决斗请求
     */
    private static final Map<Long, BattleRequest> BATTLE_REQUEST_MAP = new ConcurrentHashMap<>();

    /**
     * 战斗过程缓存
     */
    private static final Map<Long, List<String>> BATTLE_PROCESS_RECORD = new HashMap<>(256);

    /**
     * 日志打印
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BattleUtil.class);

    /**
     * 清除锁缓存
     */
    public static void clearLock() {
        BATTLE_REQUEST_MAP.clear();
    }

    /**
     * 发起决斗
     *
     * @param initiatorId 发起者ID
     * @return 决斗超时时间
     */
    public static int createBattle(Long initiatorId, Long recipientId) {
        long seconds = CdUtil.getAndSetCd(Constants.CD_BATTLE_PREFIX + initiatorId, Constants.BATTLE_CD);
        if (seconds > 0) {
            throw new BusinessException(Constants.CD_EXCEPTION_PREFIX + "000", "CD:" + seconds + "s");
        }

        // 获取玩家信息
        PlayerInfoDTO initiatorPlayer = PlayerCache.getPlayerById(initiatorId);
        PlayerInfoDTO recipientPlayer;
        try {
            recipientPlayer = PlayerCache.getPlayerById(recipientId);
        } catch (BusinessException e) {
            if (e.equals(PlayerExceptionEnum.PLAYER_NOT_FOUND.getException())) {
                throw PlayerExceptionEnum.RECIPIENT_NOT_FOUND.getException();
            }
            throw e;
        }

        // 发起方
        List<Long> initiatorTeamPlayers = new ArrayList<>(3);
        if (initiatorPlayer.getTeam()) {
            TeamDTO teamDTO = TeamCache.getTeamById(initiatorId);
            // 组队情况下非队长不可发起决斗
            if (Objects.isNull(teamDTO)) {
                throw TeamExceptionEnum.NOT_CAPTAIN.getException();
            }
            initiatorTeamPlayers.addAll(teamDTO.getMembers());
        } else {
            initiatorTeamPlayers.add(initiatorId);
        }

        // 接收方
        List<Long> recipientTeamPlayers = new ArrayList<>(3);
        if (recipientPlayer.getTeam()) {
            TeamDTO teamDTO = TeamCache.getTeamByPlayerId(recipientId);
            recipientTeamPlayers.addAll(teamDTO.getMembers());
        } else {
            recipientTeamPlayers.add(recipientId);
        }

        // 尝试获取决斗锁
        if (tryAcquired(initiatorId)) {
            // 获取成功，表明可以决斗，尝试上锁
            addBattleRequest(initiatorId, recipientId, initiatorTeamPlayers, recipientTeamPlayers);
            // 返回决斗响应时间
            return Constants.ACCEPT_BATTLE_TIME_OUT;
        }
        throw PlayerExceptionEnum.IN_BATTLE.getException();
    }

    /**
     * 接受决斗
     *
     * @param recipientId 接受玩家ID
     * @return 战斗结果
     */
    public static BattleResultVO acceptBattle(Long recipientId) {
        BattleRequest request = validateAndUnlock(recipientId);
        return battlePVP(request.getOpponentId(), recipientId);
    }

    /**
     * 拒绝决斗
     *
     * @param recipientId 接受者ID
     * @return 发起者ID
     */
    public static long refuseBattle(Long recipientId) {
        BattleRequest request = validateAndUnlock(recipientId);
        return request.getOpponentId();
    }

    /**
     * 校验与释放锁
     *
     * @param recipientId 接受方ID
     * @return 决斗信息
     */
    private static BattleRequest validateAndUnlock(long recipientId) {
        PlayerInfoDTO playerInfoDTO = PlayerCache.getPlayerById(recipientId);
        // 获取决斗信息
        BattleRequest request = BATTLE_REQUEST_MAP.get(recipientId);
        if (Objects.isNull(request)) {
            throw PlayerExceptionEnum.NO_BATTLE.getException();
        }

        List<Long> initiatorIds, recipientIds;
        // 获取接收方
        if (playerInfoDTO.getTeam()) {
            TeamDTO teamDTO = TeamCache.getTeamById(recipientId);
            // 若为组队状态
            if (Objects.isNull(teamDTO)) {
                throw TeamExceptionEnum.NOT_CAPTAIN.getException();
            }
            recipientIds = teamDTO.getMembers();
        } else {
            recipientIds = Collections.singletonList(recipientId);
        }
        // 获取发起方
        TeamDTO initiatorTeam = TeamCache.getTeamById(request.getOpponentId());
        if (Objects.nonNull(initiatorTeam)) {
            initiatorIds = initiatorTeam.getMembers();
        } else {
            initiatorIds = Collections.singletonList(request.getOpponentId());
        }

        // 释放锁
        unlock(initiatorIds);
        unlock(recipientIds);
        Duration duration = Duration.between(request.getRequestTime(), LocalDateTime.now());
        if (duration.getSeconds() > Constants.ACCEPT_BATTLE_TIME_OUT) {
            throw PlayerExceptionEnum.BATTLE_EXPIRED.getException();
        }
        return request;
    }

    /**
     * 尝试获取决斗锁
     *
     * @param playerId 玩家ID
     */
    public static boolean tryAcquired(Long playerId) {
        BattleRequest request = BATTLE_REQUEST_MAP.get(playerId);
        if (Objects.nonNull(request)) {
            Duration duration = Duration.between(request.getRequestTime(), LocalDateTime.now());
            // 决斗超时获取锁成功
            return duration.getSeconds() > Constants.ACCEPT_BATTLE_TIME_OUT;
        }
        return true;
    }

    /**
     * 获取决斗锁
     * 获取失败表明在决斗中
     *
     * @param playerId 玩家ID
     */
    public static void acquired(Long playerId) {
        BattleRequest request = BATTLE_REQUEST_MAP.get(playerId);
        if (Objects.nonNull(request)) {
            Duration duration = Duration.between(request.getRequestTime(), LocalDateTime.now());
            // 决斗尚未超时
            if (duration.getSeconds() <= Constants.ACCEPT_BATTLE_TIME_OUT) {
                throw PlayerExceptionEnum.IN_BATTLE.getException();
            }
        }
    }

    /**
     * 决斗信息
     *
     * @param initiatorId            发起者ID
     * @param recipientId            接受者ID
     * @param initiatorTeamMemberIds 发起者小队
     * @param recipientTeamMemberIds 接受者小队
     */
    public static synchronized void addBattleRequest(Long initiatorId, Long recipientId,
                                                     List<Long> initiatorTeamMemberIds, List<Long> recipientTeamMemberIds) {
        acquired(initiatorId);
        acquired(recipientId);

        initiatorTeamMemberIds.forEach(playerId ->
                BATTLE_REQUEST_MAP.put(playerId, BattleRequest.builder()
                        .requestTime(LocalDateTime.now())
                        .opponentId(recipientId).build()));

        recipientTeamMemberIds.forEach(playerId ->
                BATTLE_REQUEST_MAP.put(playerId, BattleRequest.builder()
                        .requestTime(LocalDateTime.now())
                        .opponentId(initiatorId).build()));
    }

    /**
     * 释放锁
     *
     * @param playerIds 玩家ID
     */
    public static void unlock(List<Long> playerIds) {
        playerIds.forEach(BATTLE_REQUEST_MAP::remove);
    }

    /**
     * PVP
     *
     * @param initiatorId 发起人ID
     * @param recipientId 接收者ID
     * @return 战斗结果
     */
    public static BattleResultVO battlePVP(Long initiatorId, Long recipientId) {
        PlayerInfoDTO initiatorDTO = PlayerCache.getPlayerById(initiatorId);
        PlayerInfoDTO recipientDTO = PlayerCache.getPlayerById(recipientId);

        List<OrganismInfoDTO> initiatorList;
        List<OrganismInfoDTO> recipientList;
        List<Long> initiatorIds;
        List<Long> recipientIds;
        if (initiatorDTO.getTeam()) {
            TeamDTO initiatorTeam = TeamCache.getTeamByPlayerId(initiatorId);
            initiatorList = getOrganismByPlayerId(initiatorTeam.getMembers());
            initiatorIds = initiatorTeam.getMembers();
        } else {
            initiatorList = getOrganismByPlayerId(Collections.singletonList(initiatorId));
            initiatorIds = Collections.singletonList(initiatorId);
        }

        if (recipientDTO.getTeam()) {
            TeamDTO recipientTeam = TeamCache.getTeamByPlayerId(recipientId);
            recipientList = getOrganismByPlayerId(recipientTeam.getMembers());
            recipientIds = recipientTeam.getMembers();
        } else {
            recipientList = getOrganismByPlayerId(Collections.singletonList(recipientId));
            recipientIds = Collections.singletonList(recipientId);
        }

        if (initiatorList.stream().map(OrganismInfoDTO::getOrganismDTO)
                .anyMatch(o -> (float) o.getHpWithAddition() / (float) o.getMaxHpWithAddition() < 0.1)) {
            // 因为是艾特接受决斗的人，所以此处应该是对方处于重伤状态
            throw BattleExceptionEnum.RECIPIENT_LOW_HP.getException();
        }

        if (recipientList.stream().map(OrganismInfoDTO::getOrganismDTO)
                .anyMatch(o -> (float) o.getHpWithAddition() / (float) o.getMaxHpWithAddition() < 0.1)) {
            // 因为是艾特接受决斗的人，所以此处应该是己方处于重伤状态
            throw BattleExceptionEnum.INITIATOR_LOW_HP.getException();
        }

        BattleResultVO vo = battle(initiatorList, recipientList);
        vo.setInitiatorId(initiatorId);
        vo.setRecipientId(recipientId);

        flush(initiatorList);
        flush(recipientList);

        // 异步写对战详情到缓存（大概率没人看的，异步执行就可以了）
        ThreadPoolUtil.executeBiConsumer(BattleUtil::writeBattleProcess, initiatorIds, vo.getInfo());
        ThreadPoolUtil.executeBiConsumer(BattleUtil::writeBattleProcess, recipientIds, vo.getInfo());
        return vo;
    }

    /**
     * PVE
     *
     * @param playerId 发起人ID
     * @return 战斗结果
     */
    public static BattleResultVO battlePVE(Long playerId, Long monsterId) {
        return battlePVE(playerId, Collections.singletonList(monsterId), true);
    }

    /**
     * PVE
     *
     * @param playerId      发起人ID
     * @param monsterIdList 怪物ID列表
     * @return 战斗结果
     */
    public static BattleResultVO battlePVE(Long playerId, List<Long> monsterIdList) {
        return battlePVE(playerId, monsterIdList, true);
    }

    /**
     * PVE
     *
     * @param playerId      发起人ID
     * @param monsterIdList 怪物ID列表
     * @param allowTeam     是否允许组队
     * @return 战斗结果
     */
    public static BattleResultVO battlePVE(Long playerId, List<Long> monsterIdList, boolean allowTeam) {
        return battlePVE(playerId, getOrganismByMonsterId(monsterIdList), allowTeam, true, null);
    }

    /**
     * PVE
     *
     * @param playerId      发起人ID
     * @param monsterIdList 怪物ID列表
     * @param allowTeam     是否允许组队
     * @return 战斗结果
     */
    public static BattleResultVO battlePVE(Long playerId, List<Long> monsterIdList, boolean allowTeam, boolean checkLowHp) {
        return battlePVE(playerId, getOrganismByMonsterId(monsterIdList), allowTeam, checkLowHp, null);
    }

    /**
     * PVE
     *
     * @param playerId    发起人ID
     * @param monsterList 怪物列表
     * @return 战斗结果
     */
    public static BattleResultVO battlePVE(Long playerId, List<OrganismInfoDTO> monsterList,
                                           boolean allowTeam, boolean checkLowHp,
                                           FunctionExecutor functionExecutor) {
        PlayerInfoDTO playerInfoDTO = PlayerCache.getPlayerById(playerId);
        List<Long> playerIds;
        if (allowTeam && playerInfoDTO.getTeam()) {
            TeamDTO playerTeam = TeamCache.getTeamByPlayerId(playerId);
            playerIds = playerTeam.getMembers();
        } else {
            playerIds = Collections.singletonList(playerId);
        }
        List<OrganismInfoDTO> playerList = getOrganismByPlayerId(playerIds);

        if (checkLowHp && playerList.stream().map(OrganismInfoDTO::getOrganismDTO)
                .anyMatch(o -> (float) o.getHpWithAddition() / (float) o.getMaxHpWithAddition() < 0.1)) {
            throw BattleExceptionEnum.INITIATOR_LOW_HP.getException();
        }

        BattleResultVO vo = battle(playerList, monsterList);
        if (Objects.nonNull(functionExecutor)) {
            ThreadPoolUtil.execute(functionExecutor);
        }
        flush(playerList);
        // 异步写对战详情到缓存（大概率没人看的，异步执行就可以了）
        ThreadPoolUtil.executeBiConsumer(BattleUtil::writeBattleProcess, playerIds, vo.getInfo());
        return vo;
    }

    /**
     * 斗蛐蛐
     *
     * @param monsterIdList1 怪物列表1
     * @param monsterIdList2 怪物列表2
     * @return 斗蛐蛐结果
     */
    public static BattleResultVO battleEVE(Long playerId, List<Long> monsterIdList1, List<Long> monsterIdList2) {
        BattleResultVO vo = battle(getOrganismByMonsterId(monsterIdList1), getOrganismByMonsterId(monsterIdList2));
        ThreadPoolUtil.executeBiConsumer(BattleUtil::writeBattleProcess, Collections.singletonList(playerId), vo.getInfo());
        return vo;
    }

    /**
     * 获取最后一次对战详情
     *
     * @param playerId 玩家ID
     * @return 对战详情
     */
    public static List<String> getBattleProcess(Long playerId) {
        return BATTLE_PROCESS_RECORD.get(playerId);
    }

    /**
     * 对战详情写入缓存
     *
     * @param playerIds 玩家ID
     * @param battleMsg 对战详情
     */
    private static void writeBattleProcess(List<Long> playerIds, List<String> battleMsg) {
        List<String> battleMsgClearBlank = battleMsg.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
        playerIds.forEach(playerId -> BATTLE_PROCESS_RECORD.put(playerId, battleMsgClearBlank));
    }

    /**
     * 获取怪物信息
     *
     * @param ids 怪物ID
     * @return 怪物信息
     */
    private static List<OrganismInfoDTO> getOrganismByMonsterId(List<Long> ids) {
        List<Monster> monsterList = MonsterService.getInstance().getMonsterByIds(ids);
        if (CollectionUtils.isEmpty(monsterList)) {
            throw BattleExceptionEnum.BOSS_NOT_FOUND.getException();
        }
        return monsterList.stream().map(monster -> {
            MonsterDTO monsterDTO = new MonsterDTO();
            monsterDTO.copyProperties(monster);

            OrganismInfoDTO organismInfoDTO = new OrganismInfoDTO();
            organismInfoDTO.setOrganismDTO(monsterDTO);
            // 怪物不设置默认技能，没蓝直接原地罚站
            // 技能栏
            organismInfoDTO.setSkillBarDTO(SkillCache.getSkillBarItem(BelongToEnum.MONSTER.getBelongTo(), monster.getId()));
            // 被动技能
            List<Long> inactiveSkills = SkillCache.getInactiveSkill(BelongToEnum.MONSTER.getBelongTo(), monster.getId());
            organismInfoDTO.setInactiveSkills(CollectionUtil.isEmpty(inactiveSkills) ? new ArrayList<>() :
                    inactiveSkills.stream().map(SkillEnum::getById).collect(Collectors.toList()));
            return organismInfoDTO;
        }).collect(Collectors.toList());
    }

    /**
     * 获取玩家信息
     *
     * @param ids 玩家ID
     * @return 玩家信息
     */
    private static List<OrganismInfoDTO> getOrganismByPlayerId(List<Long> ids) {
        return ids.stream().map(id -> {
            PlayerInfoDTO playerInfoDTO = PlayerCache.getPlayerById(id);
            OrganismInfoDTO organismInfoDTO = new OrganismInfoDTO();
            organismInfoDTO.setOrganismDTO(CopyUtil.copyNewInstance(playerInfoDTO.getPlayerDTO()));
            // 技能栏
            organismInfoDTO.setSkillBarDTO(SkillCache.getSkillBarItem(BelongToEnum.PLAYER.getBelongTo(), id));
            // 默认技能
            organismInfoDTO.setDefaultSkill(JobInitPropertiesEnum.getByCode(playerInfoDTO.getPlayerDTO().getJob())
                    .getDefaultSkillEnum());
            // 被动技能
            List<Long> inactiveSkills = SkillCache.getInactiveSkill(BelongToEnum.PLAYER.getBelongTo(), id);
            organismInfoDTO.setInactiveSkills(CollectionUtil.isEmpty(inactiveSkills) ? new ArrayList<>() :
                    inactiveSkills.stream().map(SkillEnum::getById).collect(Collectors.toList()));
            // 获取已穿戴的装备
            EquipmentBarDTO equipmentBarDTO = playerInfoDTO.getEquipmentBarDTO();
            organismInfoDTO.setEquipmentBarDTO(equipmentBarDTO);
            return organismInfoDTO;
        }).collect(Collectors.toList());
    }

    private static void flush(List<OrganismInfoDTO> playerList) {
        List<Long> playerIds = playerList.stream()
                .map(OrganismInfoDTO::getOrganismDTO)
                .map(organism -> (PlayerDTO) organism)
                .peek(player -> {
                    PlayerDTO cachePlayer = PlayerCache.getPlayerById(player.getId()).getPlayerDTO();
                    cachePlayer.getMaxHpWithAddition();
                    cachePlayer.getMaxMpWithAddition();
                    cachePlayer.setHpWithAddition(player.getHpWithAddition());
                    cachePlayer.setMpWithAddition(player.getMpWithAddition());
                })
                .map(PlayerDTO::getId)
                .collect(Collectors.toList());
        PlayerCache.save(playerIds);
    }

    /**
     * 战斗
     *
     * @param initiator 发起方
     * @param recipient 接收方
     * @return 战斗信息
     */
    public static BattleResultVO battle(List<OrganismInfoDTO> initiator, List<OrganismInfoDTO> recipient) {
        List<BattleDTO> initiatorBattleTmp = new ArrayList<>(initiator.size());
        List<BattleDTO> recipientBattleTmp = new ArrayList<>(recipient.size());
        // 包装
        for (OrganismInfoDTO o : initiator) {
            initiatorBattleTmp.add(buildBattleDTO(o));
        }
        for (OrganismInfoDTO o : recipient) {
            recipientBattleTmp.add(buildBattleDTO(o));
        }

        int maxRound = (initiator.size() + recipient.size()) * 50;
        // 初始化插入结算列表
        List<ExtraBattleProcessTemplate> tmpBattleProcessList = new ArrayList<>();
        List<ExtraBattleProcessTemplate> extraBattleProcessList = new ArrayList<>();
        // 插入被动技能效果结算
        initiatorBattleTmp.forEach(initiatorObj -> initExtraProcess(initiatorObj, initiatorBattleTmp, recipientBattleTmp,
                tmpBattleProcessList));
        recipientBattleTmp.forEach(recipientObj -> initExtraProcess(recipientObj, recipientBattleTmp, initiatorBattleTmp,
                tmpBattleProcessList));
        // 优先级排序
        if (CollectionUtils.isNotEmpty(tmpBattleProcessList)) {
            extraBattleProcessList = tmpBattleProcessList.stream()
                    .sorted(Comparator.comparing(ExtraBattleProcessTemplate::getPriority)).collect(Collectors.toList());
        }
        return battle(initiatorBattleTmp, recipientBattleTmp, maxRound, extraBattleProcessList);
    }

    /**
     * 战斗
     *
     * @param initiatorBattleTmp 发起方战斗信息
     * @param recipientBattleTmp 接收方战斗信息
     * @return 战斗结果
     */
    public static BattleResultVO battle(List<BattleDTO> initiatorBattleTmp, List<BattleDTO> recipientBattleTmp,
                                        final int maxRound, List<ExtraBattleProcessTemplate> extraBattleProcessList) {
        long startTime = System.currentTimeMillis();
        final AtomicInteger round = new AtomicInteger(0);
        List<String> battleMsg = new ArrayList<>(maxRound + 2);
        // 战斗前
        // 境界压制法则
        // 获取最高境界
        long initiatorHighestRank = initiatorBattleTmp.stream().mapToLong(b -> b.getOrganismInfoDTO().getOrganismDTO().getRank()).max().orElse(0L);
        long recipientHighestRank = recipientBattleTmp.stream().mapToLong(b -> b.getOrganismInfoDTO().getOrganismDTO().getRank()).max().orElse(0L);
        // 境界压制
        getRankSuppression(initiatorBattleTmp, recipientHighestRank, battleMsg);
        getRankSuppression(recipientBattleTmp, initiatorHighestRank, battleMsg);
        // 战斗前插入事件结算
        extraBattleProcessList.forEach(ext -> ext.beforeBattle(battleMsg));
        long totalBehavior =
                initiatorBattleTmp.stream()
                        .mapToLong(o -> getLongProperty(
                                o.getOrganismInfoDTO().getOrganismDTO().getBehaviorSpeed(),
                                OrganismPropertiesEnum.BEHAVIOR_SPEED.getFieldName(), o
                        )).sum() +
                        recipientBattleTmp.stream()
                                .mapToLong(o -> getLongProperty(
                                        o.getOrganismInfoDTO().getOrganismDTO().getBehaviorSpeed(),
                                        OrganismPropertiesEnum.BEHAVIOR_SPEED.getFieldName(), o
                                )).sum();
        // 战斗中
        while (initiatorBattleTmp.stream().mapToLong(o -> o.getOrganismInfoDTO().getOrganismDTO().getHpWithAddition()).sum() > 0
                && recipientBattleTmp.stream().mapToLong(o -> o.getOrganismInfoDTO().getOrganismDTO().getHpWithAddition()).sum() > 0
                && round.get() < maxRound) {
            battleProcess(initiatorBattleTmp, recipientBattleTmp, totalBehavior, round, battleMsg, extraBattleProcessList);
            battleProcess(recipientBattleTmp, initiatorBattleTmp, totalBehavior, round, battleMsg, extraBattleProcessList);
        }
        // 战斗后
        extraBattleProcessList.forEach(ext -> ext.afterBattle(battleMsg));
        // 判断胜负
        long initiatorAliveNum = initiatorBattleTmp.stream()
                .filter(o -> o.getOrganismInfoDTO().getOrganismDTO().getHpWithAddition() > 0).count();
        long recipientAliveNum = recipientBattleTmp.stream()
                .filter(o -> o.getOrganismInfoDTO().getOrganismDTO().getHpWithAddition() > 0).count();
        BattleResultVO response = new BattleResultVO();
        response.setInfo(battleMsg);
        if (initiatorAliveNum > recipientAliveNum) {
            response.setBattleResult(BattleResultVO.BattleResult.WIN);
        } else if (initiatorAliveNum == recipientAliveNum) {
            response.setBattleResult(BattleResultVO.BattleResult.DRAW);
        } else {
            response.setBattleResult(BattleResultVO.BattleResult.LOSE);
        }
        LOGGER.info("battle process cost: {} ms", System.currentTimeMillis() - startTime);
        return response;
    }

    private static void getRankSuppression(List<BattleDTO> battleTmp, long highestRank, List<String> battleMsg) {
        StringBuilder builder = new StringBuilder();
        long count = battleTmp.stream()
                .filter(i -> i.getOrganismInfoDTO().getOrganismDTO().getRank() < highestRank)
                .peek(r -> {
                    OrganismDTO organismDTO = r.getOrganismInfoDTO().getOrganismDTO();
                    RuleUtil.addRule(r, OrganismPropertiesEnum.ATK, "境界压制",
                            RankUtil.getRankSuppression(organismDTO.getRank(), highestRank), builder);
                    RuleUtil.addRule(r, OrganismPropertiesEnum.MAGIC_ATK, "境界压制",
                            RankUtil.getRankSuppression(organismDTO.getRank(), highestRank), builder);
                    RuleUtil.addRule(r, OrganismPropertiesEnum.DEF, "境界压制",
                            RankUtil.getRankSuppression(organismDTO.getRank(), highestRank), builder);
                    RuleUtil.addRule(r, OrganismPropertiesEnum.MAGIC_DEF, "境界压制",
                            RankUtil.getRankSuppression(organismDTO.getRank(), highestRank), builder);
                    RuleUtil.addRule(r, OrganismPropertiesEnum.HIT, "境界压制",
                            RankUtil.getRankSuppression(organismDTO.getRank(), highestRank), builder);
                    RuleUtil.addRule(r, OrganismPropertiesEnum.DODGE, "境界压制",
                            RankUtil.getRankSuppression(organismDTO.getRank(), highestRank), builder);
                    RuleUtil.addRule(r, OrganismPropertiesEnum.BEHAVIOR_SPEED, "境界压制",
                            RankUtil.getRankSuppression(organismDTO.getRank(), highestRank), builder);
                }).count();
        if (count > 0) {
            battleMsg.add("※" + builder);
        }
    }

    /**
     * 初始化插入结算
     *
     * @param from  当前目标
     * @param our   友方
     * @param enemy 敌方
     */
    private static void initExtraProcess(BattleDTO from, List<BattleDTO> our, List<BattleDTO> enemy,
                                         List<ExtraBattleProcessTemplate> extraBattleProcessList) {
        // 被动技能
        for (SkillEnum skill : from.getOrganismInfoDTO().getInactiveSkills()) {
            ExtraBattleProcessTemplate process = CopyUtil.copyNewInstance(skill.getGlobalExtraProcess());
            process.setFrom(from);
            process.setOur(our);
            process.setEnemy(enemy);
            extraBattleProcessList.add(process);
        }

        // 主动技能
        SkillBarDTO skillBarDTO = from.getOrganismInfoDTO().getSkillBarDTO();
        do {
            ExtraBattleProcessTemplate process
                    = CopyUtil.copyNewInstance(SkillEnum.getById(skillBarDTO.getSkillId()).getGlobalExtraProcess());
            process.setFrom(from);
            process.setOur(our);
            process.setEnemy(enemy);
            extraBattleProcessList.add(process);
            skillBarDTO = skillBarDTO.getNext();
        } while (Objects.nonNull(skillBarDTO));

        // 装备技能
        EquipmentBarDTO bar = from.getOrganismInfoDTO().getEquipmentBarDTO();
        if (Objects.nonNull(bar)) {
            initEquipmentExtraProcess(from, our, enemy, bar.getArmor(), extraBattleProcessList);
            initEquipmentExtraProcess(from, our, enemy, bar.getJewelry(), extraBattleProcessList);
            initEquipmentExtraProcess(from, our, enemy, bar.getWeapon(), extraBattleProcessList);
        }
    }

    /**
     * 初始化装备插入结算
     *
     * @param from                   来源
     * @param our                    友方
     * @param enemy                  敌方
     * @param equipmentDTO           装备
     * @param extraBattleProcessList 插入结算列表
     */
    private static void initEquipmentExtraProcess(BattleDTO from, List<BattleDTO> our, List<BattleDTO> enemy,
                                                  EquipmentDTO equipmentDTO,
                                                  List<ExtraBattleProcessTemplate> extraBattleProcessList) {
        if (Objects.isNull(equipmentDTO)) {
            return;
        }
        EquipmentEnum equipmentEnum = EquipmentEnum.getById(equipmentDTO.getEquipmentId());
        ExtraBattleProcessTemplate process = CopyUtil.copyNewInstance(equipmentEnum.getExtraBattleProcessTemplate());
        process.setFrom(from);
        process.setOur(our);
        process.setEnemy(enemy);
        extraBattleProcessList.add(process);
    }

    /**
     * 构建战斗包装
     *
     * @param organismInfoDTO 生物信息
     * @return 战斗包装
     */
    private static BattleDTO buildBattleDTO(OrganismInfoDTO organismInfoDTO) {
        BattleDTO battleDTO = new BattleDTO();
        battleDTO.setOrganismInfoDTO(organismInfoDTO);
        battleDTO.setBehavior(0L);
        battleDTO.setBuffMap(new HashMap<>());
        battleDTO.setMarkMap(new HashMap<>());
        battleDTO.setRuleList(new ArrayList<>());
        battleDTO.setRound(0L);
        return battleDTO;
    }

    /**
     * 战斗过程
     *
     * @param from          出手方
     * @param to            敌方
     * @param totalBehavior 行动值上限
     * @param round         轮次
     */
    private static void battleProcess(List<BattleDTO> from, List<BattleDTO> to,
                                      final long totalBehavior, final AtomicInteger round, List<String> battleMsg,
                                      List<ExtraBattleProcessTemplate> extraBattleProcessList) {
        from.stream().filter(f -> to.stream()
                        .mapToLong(t -> t.getOrganismInfoDTO().getOrganismDTO().getHpWithAddition()).sum() > 0)
                .forEach(o -> {
                    long speed = o.getOrganismInfoDTO().getOrganismDTO().getBehaviorSpeed();
                    long behavior = o.getBehavior() + getLongProperty(speed, BuffTypeEnum.SPEED.getName(), o);
                    if (behavior > totalBehavior && o.getOrganismInfoDTO().getOrganismDTO().getHpWithAddition() > 0) {
                        // 每回合
                        extraBattleProcessList.forEach(ext -> ext.beforeEachRound(battleMsg));
                        // 我方每回合
                        extraBattleProcessList.stream().filter(ext -> ext.getOur().contains(o))
                                .forEach(ext -> ext.beforeOurRound(battleMsg));
                        // 敌方每回合
                        extraBattleProcessList.stream().filter(ext -> ext.getOur().contains(o))
                                .forEach(ext -> ext.beforeEnemyRound(battleMsg));
                        // 我每回合
                        extraBattleProcessList.stream().filter(ext -> o.equals(ext.getFrom()))
                                .forEach(ext -> ext.beforeMyRound(battleMsg));
                        // 回合内动作
                        executeRound(o, from, to, battleMsg, extraBattleProcessList);
                        // 我每回合
                        extraBattleProcessList.stream().filter(ext -> o.equals(ext.getFrom()))
                                .forEach(ext -> ext.afterMyRound(battleMsg));
                        // 敌方每回合
                        extraBattleProcessList.stream().filter(ext -> o.equals(ext.getFrom()))
                                .forEach(ext -> ext.afterEnemyRound(battleMsg));
                        // 我方每回合
                        extraBattleProcessList.stream().filter(ext -> ext.getOur().contains(o))
                                .forEach(ext -> ext.afterOurRound(battleMsg));
                        // 每回合
                        extraBattleProcessList.forEach(ext -> ext.afterEachRound(battleMsg));
                        behavior -= totalBehavior;
                        round.addAndGet(1);
                        o.setRound(o.getRound() + 1);
                        // buff轮次-1 并清除所有过期buff
                        o.getBuffMap().values().stream()
                                .peek(buffList -> buffList.forEach(buff -> buff.setRound(buff.getRound() - 1)))
                                .forEach(buffList -> buffList.removeIf(buffDTO -> buffDTO.getRound() < 0));
                    }
                    o.setBehavior(behavior);
                });
    }

    /**
     * 回合
     *
     * @param from  使用技能者
     * @param our   使用技能者队友
     * @param enemy 使用技能者敌人
     */
    private static void executeRound(BattleDTO from, List<BattleDTO> our, List<BattleDTO> enemy, List<String> battleMsg,
                                     List<ExtraBattleProcessTemplate> extraBattleProcessList) {
        // 初始化战斗过程记录
        StringBuilder builder = new StringBuilder();
        // 获取技能
        SkillBarDTO bar = from.getOrganismInfoDTO().getSkillBarDTO();
        // 技能轮转
        if (bar.getNext() != null) {
            from.getOrganismInfoDTO().setSkillBarDTO(bar.getNext());
        } else {
            from.getOrganismInfoDTO().setSkillBarDTO(bar.getHead());
        }
        builder.append("-->").append(from.getOrganismInfoDTO().getOrganismDTO().getName());
        List<BattleBuffDTO> pauseBuff = from.getBuffMap().get(BuffTypeEnum.PAUSE);
        if (CollectionUtils.isNotEmpty(pauseBuff)) {
            String pauseBuffName = pauseBuff.stream()
                    .map(battleBuff -> battleBuff.getBuffDTO().getBuffName() + battleBuff.getRound() + "回合")
                    .reduce((name1, name2) -> name1 + "，" + name2).orElse("未知");
            builder.append("状态：").append(pauseBuffName);
            battleMsg.add(builder.toString());
            return;
        }
        // 获取技能信息
        SkillEnum skillEnum = SkillEnum.getById(bar.getSkillId());
        // 扣取mp
        long mp = from.getOrganismInfoDTO().getOrganismDTO().getMpWithAddition();
        // mp < 0表示无限mp
        if (mp >= 0) {
            if (mp < skillEnum.getMp()) {
                builder.append("蓝量不足");
                // 蓝不够 替换为默认技能 若没有默认技能 则原地罚站
                if ((skillEnum = from.getOrganismInfoDTO().getDefaultSkill()) == null) {
                    builder.append("，使用技能失败");
                    battleMsg.add(builder.toString());
                    return;
                }
                builder.append("，改为");
            } else {
                mp -= skillEnum.getMp();
                from.getOrganismInfoDTO().getOrganismDTO().setMpWithAddition(mp);
            }
        }
        builder.append("使用技能【").append(skillEnum.getName()).append("】");
        builder.append("，蓝量：").append(from.getOrganismInfoDTO().getOrganismDTO().getMpWithAddition())
                .append("/").append(from.getOrganismInfoDTO().getOrganismDTO().getMaxMpWithAddition());

        skillEnum.getThisBehaviorExtraProcess().beforeThisRound(from, our, enemy, builder);
        final SkillEnum nowSkill = skillEnum;
        skillEnum.getSkillEffects().forEach(skillEffect ->
                executeSingleEffectBehavior(skillEffect, nowSkill, from, our, enemy, extraBattleProcessList, builder));
        enemy.forEach(e -> builder.append("，").append(e.getOrganismInfoDTO().getOrganismDTO().getName())
                .append("生命值：").append(e.getOrganismInfoDTO().getOrganismDTO().getHpWithAddition())
                .append("/").append(e.getOrganismInfoDTO().getOrganismDTO().getMaxHpWithAddition()));
        skillEnum.getThisBehaviorExtraProcess().afterThisRound(from, our, enemy, builder);
        // 写入战斗信息
        battleMsg.add(builder.toString());
    }

    /**
     * 单个技能效果
     *
     * @param skillEffect            技能效果
     * @param nowSkill               当前技能
     * @param from                   来源
     * @param our                    来源方友方
     * @param enemy                  敌方
     * @param extraBattleProcessList 插入结算列表
     * @param builder                战斗信息
     */
    public static void executeSingleEffectBehavior(SkillEffectDTO skillEffect, SkillEnum nowSkill,
                                                   BattleDTO from, List<BattleDTO> our, List<BattleDTO> enemy,
                                                   List<ExtraBattleProcessTemplate> extraBattleProcessList, StringBuilder builder) {
        // 选择技能目标
        List<BattleDTO> target = getSkillTarget(skillEffect.getSkillTargetEnum(), from, our, enemy);
        if (target.isEmpty()) {
            return;
        }
        target.forEach(tar ->
                executeSingleTarBehavior(skillEffect, nowSkill, from, tar, our, enemy, extraBattleProcessList, builder));
    }

    /**
     * 单个目标效果
     *
     * @param skillEffect            技能效果
     * @param nowSkill               当前技能
     * @param from                   来源
     * @param tar                    目标
     * @param our                    来源方友方
     * @param enemy                  敌方
     * @param extraBattleProcessList 插入结算列表
     * @param builder                战斗信息
     */
    public static void executeSingleTarBehavior(SkillEffectDTO skillEffect, SkillEnum nowSkill,
                                                BattleDTO from, BattleDTO tar, List<BattleDTO> our, List<BattleDTO> enemy,
                                                List<ExtraBattleProcessTemplate> extraBattleProcessList, StringBuilder builder) {
        // 插入结算-我的行动前
        extraBattleProcessList.stream().filter(ext -> ext.getFrom().equals(from))
                .forEach(ext -> ext.beforeMyBehavior(tar, builder));
        nowSkill.getThisBehaviorExtraProcess().beforeEffect(from, tar, our, enemy, builder);
        // 行动过程
        executeBehavior(skillEffect, nowSkill, from, tar, our, enemy, extraBattleProcessList, builder);
        // 插入结算-我的行动后
        nowSkill.getThisBehaviorExtraProcess().afterEffect(from, tar, our, enemy, builder);
        extraBattleProcessList.stream().filter(ext -> ext.getFrom().equals(from))
                .forEach(ext -> ext.afterMyBehavior(tar, builder));
    }

    public static void executeBehavior(SkillEffectDTO skillEffect, SkillEnum nowSkill,
                                       BattleDTO from, BattleDTO tar, List<BattleDTO> our, List<BattleDTO> enemy,
                                       List<ExtraBattleProcessTemplate> extraBattleProcessList, StringBuilder builder) {
        OrganismDTO fromOrganism = from.getOrganismInfoDTO().getOrganismDTO();
        OrganismDTO tarOrganism = tar.getOrganismInfoDTO().getOrganismDTO();

        if (skillEffect.getSkillTargetEnum().isEnemy()
                && !isHit(from, tar, our, enemy, extraBattleProcessList, nowSkill, builder)) {
            // 技能目标为敌方，计算是否命中
            return;
        }
        // 命中的情况下，判断对方是否处于无敌状态
        List<BattleBuffDTO> invincibleBuffList = tar.getBuffMap().get(BuffTypeEnum.INVINCIBLE);
        if (CollectionUtil.isNotEmpty(invincibleBuffList)) {
            String status = tar.getBuffMap().get(BuffTypeEnum.INVINCIBLE).stream()
                    .map(buffType -> buffType.getBuffDTO().getBuffName())
                    .reduce((buffName1, buffName2) -> buffName1 + "，" + buffName2).orElse("未知");
            builder.append("，").append(tarOrganism.getName()).append("状态：").append(status).append("，无法造成伤害");
            return;
        }
        if (extraBattleProcessList.stream().anyMatch(ext -> !ext.canEffect(tar, skillEffect, builder))) {
            return;
        }
        long damage = getDamage(from, tar, our, enemy, skillEffect.getDamageTypeEnum(),
                skillEffect.getDamageRate(), nowSkill, extraBattleProcessList, builder);
        damage = Math.round(damage * RandomUtil.rangeFloatRandom(0.9F, 1.1F));
        AtomicLong atomicDamage = new AtomicLong(damage);
        // 计算暴击
        boolean critical = getCriticalDamage(atomicDamage, from, tar, builder);
        // 计算减伤
        if (DamageTypeEnum.ATK.equals(skillEffect.getDamageTypeEnum()) && tar.getBuffMap().containsKey(BuffTypeEnum.DAMAGE)) {
            float reduction = BuffUtil.getRate(0F, BuffTypeEnum.DAMAGE, from);
            atomicDamage.set(Math.round(atomicDamage.get() * (1 - reduction)));
        } else if (DamageTypeEnum.MAGIC.equals(skillEffect.getDamageTypeEnum())
                && tar.getBuffMap().containsKey(BuffTypeEnum.MAGIC_DAMAGE)) {
            float reduction = BuffUtil.getRate(0F, BuffTypeEnum.MAGIC_DAMAGE, from);
            atomicDamage.set(Math.round(atomicDamage.get() * (1 - reduction)));
        }
        // 命中插入结算
        extraBattleProcessList.forEach(ext -> ext.ifHit(from, tar, nowSkill, skillEffect, atomicDamage, critical, builder));
        nowSkill.getThisBehaviorExtraProcess().ifHit(from, tar, our, enemy, atomicDamage, critical, builder);
        // 如果是致命伤害，触发特殊结算
        if (atomicDamage.get() >= tarOrganism.getHpWithAddition()) {
            extraBattleProcessList.stream()
                    .filter(ext -> from.equals(ext.getFrom()))
                    .forEach(ext -> ext.beforeTargetDeath(from, tar, atomicDamage, builder));
            extraBattleProcessList.stream()
                    .filter(ext -> tar.equals(ext.getFrom()))
                    .forEach(ext -> ext.beforeMeDeath(from, tar, atomicDamage, builder));
        }
        // 计算生命值
        long hp = tarOrganism.getHpWithAddition() - atomicDamage.get();
        hp = hp > 0 ? hp : 0;
        tarOrganism.setHpWithAddition(hp);
        // 若造成了伤害，计算吸血
        if (atomicDamage.get() > 0) {
            float lifeStealRate = getRateProperty(fromOrganism.getLifeStealing(), BuffTypeEnum.LIFE_STEAL.getName(), from);
            if (lifeStealRate > 0) {
                long heal = Math.round(lifeStealRate * atomicDamage.get());
                doHealing(from, heal, builder);
            }
            extraBattleProcessList.forEach(ext -> ext.afterDamage(tar, atomicDamage, builder));
        }
        // 有伤害倍率的技能 插入文描
        if (skillEffect.getDamageRate() > 0) {
            builder.append("，对").append(tarOrganism.getName()).append("造成").append(atomicDamage.get()).append("点伤害");
        }
        // 若对方仍有存活，且出手方身上有反伤buff，计算反伤
        List<BattleBuffDTO> reflectDamageBuffList = from.getBuffMap().get(BuffTypeEnum.REFLECT_DAMAGE);
        if (CollectionUtil.isNotEmpty(reflectDamageBuffList)
                && enemy.stream().anyMatch(battleDTO ->
                battleDTO.getOrganismInfoDTO().getOrganismDTO().getHpWithAddition() > 0)) {
            for (BattleBuffDTO battleBuffDTO : reflectDamageBuffList) {
                long realDamage = BigDecimalUtil.multiply(atomicDamage.get(), battleBuffDTO.getBuffDTO().getRate());
                doRealDamage(from, realDamage, builder);
            }
        }
        // 若对方没死，计算buff
        if (hp > 0) {
            List<GiveBuffDTO> giveBuffDTOList = skillEffect.getGiveBuffDTOList();
            if (CollectionUtil.isNotEmpty(giveBuffDTOList)) {
                // 概率附加buff
                skillEffect.getGiveBuffDTOList().stream()
                        .filter(buff -> RandomUtil.isHit(buff.getEffectedRate().floatValue()))
                        .forEach(buff -> {
                            BuffDTO buffDTO = new BuffDTO(
                                    buff.getBuffType(),
                                    buff.getBuffName(),
                                    buff.getValue(),
                                    buff.getRate().floatValue(),
                                    buff.getAbnormal()
                            );
                            BuffUtil.addBuff(tar, buffDTO, buff.getRound(), builder);
                        });
            }
        } else {
            // 触发受到致命伤害后的结算
            extraBattleProcessList.stream().filter(ext -> tar.equals(ext.getFrom()))
                    .forEach(ext -> ext.afterMeDeath(from, tar, atomicDamage, builder));
            extraBattleProcessList.stream().filter(ext -> from.equals(ext.getFrom()))
                    .forEach(ext -> ext.afterTargetDeath(from, tar, atomicDamage, builder));
        }
    }

    /**
     * 获取目标
     *
     * @param skillTargetEnum 技能指定的目标
     * @param from            释放技能者
     * @param our             释放者友方
     * @param enemy           释放者敌方
     * @return 目标
     */
    public static List<BattleDTO> getSkillTarget(SkillTargetEnum skillTargetEnum, BattleDTO from,
                                                 List<BattleDTO> our, List<BattleDTO> enemy) {
        // 过滤0血角色
        List<BattleDTO> target;
        if (skillTargetEnum.isEnemy()) {
            target = enemy.stream().filter(b -> b.getOrganismInfoDTO().getOrganismDTO().getHpWithAddition() > 0)
                    .collect(Collectors.toList());
        } else {
            target = our.stream().filter(b -> b.getOrganismInfoDTO().getOrganismDTO().getHpWithAddition() > 0)
                    .collect(Collectors.toList());
        }

        // 如果目标方全员0血，不再继续往下计算
        if (target.isEmpty()) {
            return target;
        }

        if (SkillTargetEnum.ANY_ENEMY.equals(skillTargetEnum)) {
            // 任意敌方，需要计算嘲讽
            List<BattleDTO> taunt = target.stream().filter(t -> {
                List<BattleBuffDTO> buffList = t.getBuffMap().getOrDefault(BuffTypeEnum.TAUNT, null);
                return CollectionUtil.isNotEmpty(buffList);
            }).collect(Collectors.toList());

            if (!taunt.isEmpty()) {
                target = Collections.singletonList(
                        taunt.stream()
                                .max(Comparator.comparing(t -> t.getOrganismInfoDTO().getOrganismDTO().getHpWithAddition()))
                                .get()
                );
            } else {
                target = Collections.singletonList(target.get(0));
            }
        } else if (SkillTargetEnum.ALL_ENEMY.equals(skillTargetEnum)
                || SkillTargetEnum.ALL_OUR.equals(skillTargetEnum)) {
            // 全体敌方 & 全体我方
            // 无需处理
        } else if (SkillTargetEnum.ANY_OUR.equals(skillTargetEnum)) {
            // 任意我方
            // todo
        } else if (SkillTargetEnum.ME.equals(skillTargetEnum)) {
            // 自己
            target = Collections.singletonList(from);
        } else {
            throw new IllegalArgumentException("目标配置不正确");
        }
        return target;
    }

    /**
     * 计算伤害值
     *
     * @param from           来源方
     * @param tar            目标方
     * @param damageTypeEnum 伤害类型
     * @param damageRate     伤害倍率
     * @return 伤害值
     */
    public static long getDamage(BattleDTO from, BattleDTO tar,
                                 List<BattleDTO> our, List<BattleDTO> enemy,
                                 DamageTypeEnum damageTypeEnum, float damageRate, SkillEnum nowSkill,
                                 List<ExtraBattleProcessTemplate> templateList, StringBuilder builder) {
        OrganismDTO fromOrganism = from.getOrganismInfoDTO().getOrganismDTO();
        OrganismDTO tarOrganism = tar.getOrganismInfoDTO().getOrganismDTO();
        // 1.计算伤害
        long realAtk, realDef;
        float realPenetrate;
        // 根据伤害类型区分
        // 1.基础属性 2.装备加成 3.buff
        if (DamageTypeEnum.ATK.equals(damageTypeEnum)) {
            realAtk = getLongProperty(fromOrganism.getAtk(), BuffTypeEnum.ATK.getName(), from);
            realPenetrate = getRateProperty(fromOrganism.getPenetrate(), BuffTypeEnum.PENETRATE.getName(), from);
            realDef = getLongProperty(tarOrganism.getDef(), BuffTypeEnum.DEF.getName(), tar);
        } else if (DamageTypeEnum.MAGIC.equals(damageTypeEnum)) {
            realAtk = getLongProperty(fromOrganism.getMagicAtk(), BuffTypeEnum.MAGIC_ATK.getName(), from);
            realPenetrate = getRateProperty(fromOrganism.getMagicPenetrate(), BuffTypeEnum.MAGIC_PENETRATE.getName(), from);
            realDef = getLongProperty(tarOrganism.getMagicDef(), BuffTypeEnum.MAGIC_DEF.getName(), tar);
        } else {
            return 0;
        }
        // 技能倍率
        BigDecimal damageRateDecimal = getDamageRate(from, tar, our, enemy, damageRate, nowSkill, templateList, builder);
        // 计算公式 damage = realAtk - realDef * (1 - realPenetrate)
//        long damage = Math.round((realAtk - realDef * (1 - realPenetrate)) * damageRateDecimal.floatValue());
        // 测试公式 攻击*系数/（防御+系数）
        long damage = Math.round(realAtk * 1000 / (realDef * (1 - realPenetrate) + 1000) * damageRateDecimal.floatValue());
        if (DamageTypeEnum.ATK.equals(damageTypeEnum)) {
            BuffUtil.getVal(damage, BuffTypeEnum.DAMAGE, from);
        } else {
            BuffUtil.getVal(damage, BuffTypeEnum.MAGIC_DAMAGE, from);
        }
        // 伤害最低为0
        return Math.max(damage, 0L);
    }

    public static BigDecimal getDamageRate(BattleDTO from, BattleDTO tar,
                                           List<BattleDTO> our, List<BattleDTO> enemy,
                                           float src, SkillEnum nowSkill,
                                           List<ExtraBattleProcessTemplate> templateList, StringBuilder builder) {
        BigDecimal val = BigDecimal.valueOf(src);
        for (ExtraBattleProcessTemplate tpl : templateList) {
            val = tpl.changeDamageRate(from, tar, val, nowSkill, builder);
        }
        if (Objects.nonNull(nowSkill)) {
            val = nowSkill.getThisBehaviorExtraProcess().changeDamageRate(from, tar, our, enemy, val, builder);
        }
        return val;
    }

    public static boolean isHit(BattleDTO from, BattleDTO tar,
                                List<BattleDTO> our, List<BattleDTO> enemy,
                                List<ExtraBattleProcessTemplate> extraBattleProcessList,
                                SkillEnum nowSkill, StringBuilder builder) {
        OrganismDTO fromOrganism = from.getOrganismInfoDTO().getOrganismDTO();
        OrganismDTO tarOrganism = tar.getOrganismInfoDTO().getOrganismDTO();
        // 命中率 = 1 + (命中 - 闪避) / 闪避
        float fromHit = getLongProperty(fromOrganism.getHit(), BuffTypeEnum.HIT.getName(), from);
        float targetDodge = getLongProperty(tarOrganism.getDodge(), BuffTypeEnum.DODGE.getName(), from);
        float hitRate = 1 + (fromHit - targetDodge) / targetDodge;
        boolean isHit = RandomUtil.isHit(RandomUtil.format(hitRate, 4));
        if (!isHit) {
            // 未命中插入结算
            extraBattleProcessList.forEach(ext -> ext.ifNotHit(from, tar, nowSkill, builder));
            if (Objects.nonNull(nowSkill)) {
                nowSkill.getThisBehaviorExtraProcess().ifNotHit(from, tar, our, enemy, builder);
            }
            builder.append(",没有命中").append(tarOrganism.getName());
        }
        return isHit;
    }

    public static boolean getCriticalDamage(AtomicLong damage, BattleDTO from, BattleDTO tar, StringBuilder builder) {
        OrganismDTO fromOrganism = from.getOrganismInfoDTO().getOrganismDTO();
        OrganismDTO tarOrganism = tar.getOrganismInfoDTO().getOrganismDTO();
        // 若伤害不为0，计算是否暴击
        if (damage.get() > 0) {
            float criticalRate = getRateProperty(fromOrganism.getCriticalRate(), BuffTypeEnum.CRITICAL.getName(), from);
            float criticalReductionRate = getRateProperty(tarOrganism.getCriticalReductionRate(),
                    BuffTypeEnum.CRITICAL_REDUCTION.getName(), from);
            if (RandomUtil.isHit(Math.max(criticalRate - criticalReductionRate, 0))) {
                float criticalDamage = getRateProperty(fromOrganism.getCriticalDamage(),
                        BuffTypeEnum.CRITICAL_DAMAGE.getName(), from);
                float criticalDamageReduction = getRateProperty(tarOrganism.getCriticalDamageReduction(),
                        BuffTypeEnum.CRITICAL_DAMAGE_REDUCTION.getName(), from);
                float realCriticalDamage = criticalDamage - criticalDamageReduction;
                builder.append("，造成暴击，伤害提升至").append(realCriticalDamage * 100).append("%");
                damage.set(Math.round(damage.get() * realCriticalDamage));
                return true;
            }
        }
        return false;
    }

    public static Long getLongProperty(long src, String propName, BattleDTO battleDTO) {
        return BuffUtil.getVal(
                RuleUtil.getVal(battleDTO, OrganismPropertiesEnum.getByFieldName(propName),
                        EquipmentUtil.getLongVal(
                                src, EquipmentPropertiesTypeEnum.getByNameEn(propName),
                                battleDTO.getOrganismInfoDTO().getEquipmentBarDTO()
                        )
                ), BuffTypeEnum.getByName(propName), battleDTO
        );
    }

    public static Float getRateProperty(float rate, String propName, BattleDTO battleDTO) {
        return BuffUtil.getRate(
                RuleUtil.getRate(battleDTO, OrganismPropertiesEnum.getByFieldName(propName),
                        EquipmentUtil.getFloatVal(
                                rate, EquipmentPropertiesTypeEnum.getByNameEn(propName),
                                battleDTO.getOrganismInfoDTO().getEquipmentBarDTO()
                        )
                ), BuffTypeEnum.getByName(propName), battleDTO
        );
    }

    public static void doHealing(BattleDTO battleDTO, long heal, StringBuilder builder) {
        OrganismDTO organismDTO = battleDTO.getOrganismInfoDTO().getOrganismDTO();
        long healAfterCal = heal;
        if (battleDTO.getBuffMap().containsKey(BuffTypeEnum.HEAL)) {
            List<BattleBuffDTO> buffList = battleDTO.getBuffMap().get(BuffTypeEnum.HEAL);
            healAfterCal = buffList.stream().map(buff -> buff.getBuffDTO().getValue()).reduce(heal, Long::sum);
            BigDecimal rate = buffList.stream().map(buff -> buff.getBuffDTO().getRate()).reduce(BigDecimal.ONE, BigDecimal::add);
            healAfterCal = BigDecimal.valueOf(healAfterCal).multiply(rate).longValue();
        }

        long newHp = Math.min(organismDTO.getHpWithAddition() + healAfterCal, organismDTO.getMaxHpWithAddition());
        builder.append("，血量回复了").append(newHp - organismDTO.getHpWithAddition()).append("点");
        organismDTO.setHpWithAddition(newHp);
    }

    public static void doMpRecover(BattleDTO battleDTO, long mp, StringBuilder builder) {
        OrganismDTO organismDTO = battleDTO.getOrganismInfoDTO().getOrganismDTO();
        long newMp = Math.min(organismDTO.getMpWithAddition() + mp, organismDTO.getMaxMpWithAddition());
        if (mp >= 0) {
            builder.append("，蓝量回复了").append(newMp - organismDTO.getMpWithAddition()).append("点");
        } else {
            builder.append("，蓝量减少了").append(organismDTO.getMpWithAddition() - newMp).append("点");
        }
        organismDTO.setMpWithAddition(newMp);
    }

    public static void doRealDamage(BattleDTO target, long realDamage, StringBuilder builder) {
        long oldHp = target.getOrganismInfoDTO().getOrganismDTO().getHpWithAddition();
        long newHp = Math.max(0, oldHp - realDamage);
        target.getOrganismInfoDTO().getOrganismDTO().setHpWithAddition(newHp);
        builder.append("，对").append(target.getOrganismInfoDTO().getOrganismDTO().getName())
                .append("造成").append(oldHp - newHp).append("点真实伤害");
    }

    /**
     * 决斗信息类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BattleRequest implements Serializable {
        /**
         * 发起决斗的时间
         */
        private LocalDateTime requestTime;
        /**
         * 敌方ID
         */
        private Long opponentId;
    }
}
