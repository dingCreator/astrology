package com.dingCreator.astrology.util;

import cn.hutool.core.collection.CollectionUtil;
import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.cache.SkillCache;
import com.dingCreator.astrology.cache.TeamCache;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.dto.*;
import com.dingCreator.astrology.dto.equipment.EquipmentBarDTO;
import com.dingCreator.astrology.dto.organism.OrganismDTO;
import com.dingCreator.astrology.dto.organism.OrganismInfoDTO;
import com.dingCreator.astrology.dto.organism.monster.MonsterDTO;
import com.dingCreator.astrology.dto.organism.player.PlayerInfoDTO;
import com.dingCreator.astrology.dto.skill.SkillBarDTO;
import com.dingCreator.astrology.entity.EquipmentBelongTo;
import com.dingCreator.astrology.entity.base.Monster;
import com.dingCreator.astrology.enums.BelongToEnum;
import com.dingCreator.astrology.enums.BuffTypeEnum;
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
import com.dingCreator.astrology.service.EquipmentBelongToService;
import com.dingCreator.astrology.service.MonsterService;
import com.dingCreator.astrology.template.ExtraBattleProcessTemplate;
import com.dingCreator.astrology.util.function.FunctionExecutor;
import com.dingCreator.astrology.vo.BattleResultVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
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

        PlayerCache.flush(initiatorIds);
        PlayerCache.flush(recipientIds);

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
        return battlePVE(playerId, getOrganismByMonsterId(monsterIdList), allowTeam, null);
    }

    /**
     * PVE
     *
     * @param playerId    发起人ID
     * @param monsterList 怪物列表
     * @return 战斗结果
     */
    public static BattleResultVO battlePVE(Long playerId, List<OrganismInfoDTO> monsterList, boolean allowTeam,
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

        if (playerList.stream().map(OrganismInfoDTO::getOrganismDTO)
                .anyMatch(o -> (float) o.getHpWithAddition() / (float) o.getMaxHpWithAddition() < 0.1)) {
            throw BattleExceptionEnum.INITIATOR_LOW_HP.getException();
        }

        BattleResultVO vo = battle(playerList, monsterList);
        if (Objects.nonNull(functionExecutor)) {
            ThreadPoolUtil.execute(functionExecutor);
        }
        PlayerCache.flush(playerIds);
        // 异步写对战详情到缓存（大概率没人看的，异步执行就可以了）
        ThreadPoolUtil.executeBiConsumer(BattleUtil::writeBattleProcess, playerIds, vo.getInfo());
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
        playerIds.forEach(playerId -> BATTLE_PROCESS_RECORD.put(playerId, battleMsg));
    }

    /**
     * 获取怪物信息
     *
     * @param ids 怪物ID
     * @return 怪物信息
     */
    private static List<OrganismInfoDTO> getOrganismByMonsterId(List<Long> ids) {
        List<Monster> monsterList = MonsterService.getInstance().getMonsterByIds(ids);
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
            organismInfoDTO.setOrganismDTO(playerInfoDTO.getPlayerDTO());
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
            List<EquipmentBelongTo> equipmentBelongToList = EquipmentBelongToService.getInstance()
                    .getBelongToIdEquip(BelongToEnum.PLAYER.getBelongTo(), id, true);
            EquipmentBarDTO equipmentBarDTO = new EquipmentBarDTO();
            equipmentBelongToList.forEach(e -> {
                EquipmentEnum equipmentEnum = EquipmentEnum.getById(e.getEquipmentId());
                if (Objects.nonNull(equipmentEnum)) {
                    EquipmentUtil.setEquipmentBarDTO(equipmentBarDTO, equipmentEnum, e);
                }
            });
            organismInfoDTO.setEquipmentBarDTO(equipmentBarDTO);
            return organismInfoDTO;
        }).collect(Collectors.toList());
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
        long totalBehaviorSpeed = 0;
        for (OrganismInfoDTO o : initiator) {
            totalBehaviorSpeed += o.getOrganismDTO().getBehaviorSpeed();
            initiatorBattleTmp.add(buildBattleDTO(o));
        }
        for (OrganismInfoDTO o : recipient) {
            totalBehaviorSpeed += o.getOrganismDTO().getBehaviorSpeed();
            recipientBattleTmp.add(buildBattleDTO(o));
        }

        int maxRound = (initiator.size() + recipient.size()) * 50;
        // 初始化插入结算列表
        List<ExtraBattleProcessTemplate> extraBattleProcessList = new ArrayList<>();
        // 插入被动技能效果结算
        initiatorBattleTmp.forEach(initiatorObj -> initExtraProcess(initiatorObj, initiatorBattleTmp, recipientBattleTmp,
                extraBattleProcessList));
        recipientBattleTmp.forEach(recipientObj -> initExtraProcess(recipientObj, recipientBattleTmp, initiatorBattleTmp,
                extraBattleProcessList));
        return battle(initiatorBattleTmp, recipientBattleTmp, totalBehaviorSpeed, maxRound, extraBattleProcessList);
    }

    /**
     * 战斗
     *
     * @param initiatorBattleTmp 发起方战斗信息
     * @param recipientBattleTmp 接收方战斗信息
     * @return 战斗结果
     */
    public static BattleResultVO battle(List<BattleDTO> initiatorBattleTmp, List<BattleDTO> recipientBattleTmp,
                                        final long totalBehavior, final int maxRound,
                                        List<ExtraBattleProcessTemplate> extraBattleProcessList) {
        long startTime = System.currentTimeMillis();
        final AtomicInteger round = new AtomicInteger(0);
        List<String> battleMsg = new ArrayList<>(maxRound + 2);
        // 战斗前
        extraBattleProcessList.forEach(ext -> ext.beforeBattle(battleMsg));
        // 战斗中
        while (round.get() < maxRound
                && initiatorBattleTmp.stream()
                .mapToLong(o -> o.getOrganismInfoDTO().getOrganismDTO().getHpWithAddition()).sum() > 0
                && recipientBattleTmp.stream()
                .mapToLong(o -> o.getOrganismInfoDTO().getOrganismDTO().getHpWithAddition()).sum() > 0) {
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
        from.getOrganismInfoDTO().getInactiveSkills().forEach(skill -> {
            ExtraBattleProcessTemplate process = skill.getExtraProcess();
            process.setFrom(from);
            process.setOur(our);
            process.setEnemy(enemy);
            extraBattleProcessList.add(process);
        });

        // 主动技能
        SkillBarDTO skillBarDTO = from.getOrganismInfoDTO().getSkillBarDTO();
        do {
            ExtraBattleProcessTemplate process = SkillEnum.getById(skillBarDTO.getSkillId()).getExtraProcess();
            process.setFrom(from);
            process.setOur(our);
            process.setEnemy(enemy);
            extraBattleProcessList.add(process);
            skillBarDTO = skillBarDTO.getNext();
        } while (Objects.nonNull(skillBarDTO));
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
                    long behavior = o.getBehavior() + BuffUtil.getSpeed(o.getOrganismInfoDTO().getOrganismDTO().getBehaviorSpeed(),
                            o.getBuffMap());
                    if (behavior > totalBehavior && o.getOrganismInfoDTO().getOrganismDTO().getHpWithAddition() > 0) {
                        extraBattleProcessList.forEach(ext -> ext.beforeEachRound(battleMsg));
                        extraBattleProcessList.stream().filter(ext -> o.equals(ext.getFrom()))
                                .forEach(ext -> ext.beforeMyRound(battleMsg));
                        attackBehavior(o, from, to, battleMsg, extraBattleProcessList);
                        behavior -= totalBehavior;
                        round.addAndGet(1);
                        // buff轮次-1 并清除所有过期buff
                        o.getBuffMap().values().stream()
                                .peek(buffList -> buffList.forEach(buff -> buff.setRound(buff.getRound() - 1)))
                                .forEach(buffList -> buffList.removeIf(buffDTO -> buffDTO.getRound() < 0));
                        extraBattleProcessList.stream().filter(ext -> o.equals(ext.getFrom()))
                                .forEach(ext -> ext.afterMyRound(battleMsg));
                        extraBattleProcessList.forEach(ext -> ext.afterEachRound(battleMsg));
                    }
                    o.setBehavior(behavior);
                });
    }

    /**
     * 战斗过程
     *
     * @param from  使用技能者
     * @param our   使用技能者队友
     * @param enemy 使用技能者敌人
     */
    private static void attackBehavior(BattleDTO from, List<BattleDTO> our, List<BattleDTO> enemy, List<String> battleMsg,
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

        builder.append(from.getOrganismInfoDTO().getOrganismDTO().getName());
        // 获取技能信息
        SkillEnum skillEnum = SkillEnum.getById(bar.getSkillId());
        // 扣取mp
        long mp = from.getOrganismInfoDTO().getOrganismDTO().getMpWithAddition();
        // mp < 0表示无限mp
        if (mp > 0) {
            if (mp < skillEnum.getMp()) {
                builder.append("蓝量不足");
                // 蓝不够 替换为默认技能 若没有默认技能 则原地罚站
                if ((skillEnum = from.getOrganismInfoDTO().getDefaultSkill()) == null) {
                    return;
                }
                builder.append("，改为");
            } else {
                mp -= skillEnum.getMp();
                from.getOrganismInfoDTO().getOrganismDTO().setMpWithAddition(mp);
            }
        }
        builder.append("使用技能【").append(skillEnum.getName()).append("】");

        final SkillEnum nowSkill = skillEnum;
        skillEnum.getSkillEffects().forEach(skillEffect -> {
            // 选择技能目标
            List<BattleDTO> target = getSkillTarget(skillEffect.getSkillTargetEnum(), from, our, enemy);
            if (target.isEmpty()) {
                return;
            }
            // 获取目标方最高阶级
            long tarHighestRank = target.stream().mapToLong(b -> b.getOrganismInfoDTO().getOrganismDTO().getRank())
                    .max().orElse(0L);
            target.forEach(tar -> {
                OrganismDTO fromOrganism = from.getOrganismInfoDTO().getOrganismDTO();
                OrganismDTO tarOrganism = tar.getOrganismInfoDTO().getOrganismDTO();

                if (skillEffect.getSkillTargetEnum().isEnemy()
                        && !isHit(fromOrganism, tarOrganism, from, tar, extraBattleProcessList, nowSkill, builder)) {
                    // 技能目标为敌方，计算是否命中
                    return;
                }
                long damage = getDamage(from, tar, fromOrganism, tarOrganism, skillEffect.getDamageTypeEnum(),
                        tarHighestRank, skillEffect.getDamageRate());
                final long finalDamage = getCriticalDamage(damage, fromOrganism, tarOrganism, from, tar, builder);
                // 命中插入结算
                extraBattleProcessList.forEach(ext -> ext.ifHit(from, tar, nowSkill, finalDamage, builder));
                long hp = tarOrganism.getHpWithAddition() - finalDamage;
                hp = hp > 0 ? hp : 0;
                tarOrganism.setHpWithAddition(hp);

                if (skillEffect.getDamageRate() > 0) {
                    // 没有伤害倍率的技能不需要此段信息
                    builder.append("，对").append(tarOrganism.getName())
                            .append("造成").append(finalDamage).append("点伤害")
                            .append("，").append(tarOrganism.getName())
                            .append("生命值：").append(tarOrganism.getHpWithAddition())
                            .append("/").append(tarOrganism.getMaxHpWithAddition());
                }
                // 若对方仍有存活，且出手方身上有反伤buff，计算反伤
                List<BattleBuffDTO> reflectDamageBuffList = from.getBuffMap().get(BuffTypeEnum.REFLECT_DAMAGE);
                if (CollectionUtil.isNotEmpty(reflectDamageBuffList)
                        && enemy.stream().anyMatch(battleDTO ->
                        battleDTO.getOrganismInfoDTO().getOrganismDTO().getHpWithAddition() > 0)) {
                    long fromHp = fromOrganism.getHpWithAddition();

                }
                // 若对方没死，计算buff
                if (hp > 0) {
                    List<GiveBuffDTO> giveBuffDTOList = skillEffect.getGiveBuffDTOList();
                    if (CollectionUtil.isNotEmpty(giveBuffDTOList)) {
                        // 概率附加buff
                        skillEffect.getGiveBuffDTOList().stream()
                                .filter(buff -> {
                                    // todo buff上限
                                    return true;
                                })
                                .filter(buff -> RandomUtil.isHit(buff.getEffectedRate()))
                                .forEach(buff -> BuffUtil.addBuff(tar, buildBuffDTO(buff, from), buff.getRound(), builder));
                    }
                }
            });
        });
        // 对所有效果进行结算
        // 写入战斗信息
        battleMsg.add(builder.toString());
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
    private static List<BattleDTO> getSkillTarget(SkillTargetEnum skillTargetEnum, BattleDTO from,
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

        // 如果对方全员0血，不再继续往下计算
        if (target.isEmpty()) {
            return target;
        }

        if (SkillTargetEnum.ANY_ENEMY.equals(skillTargetEnum)) {
            // 任意敌方，需要计算嘲讽
            List<BattleDTO> taunt = target.stream().filter(t -> {
                List<BattleBuffDTO> buffList = t.getBuffMap().getOrDefault(BuffTypeEnum.TAUNT, null);
                return CollectionUtil.isNotEmpty(buffList);
            }).collect(Collectors.toList());

            if (taunt.size() > 0) {
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
     * 构建buff
     *
     * @param buff 给予buff配置
     * @param from 给予者
     * @return buff
     */
    private static BuffDTO buildBuffDTO(GiveBuffDTO buff, BattleDTO from) {
        BuffDTO buffDTO;
        if (Objects.isNull(buff.getDependsBuffType())) {
            buffDTO = new BuffDTO(buff.getBuffType(), buff.getBuffName(), buff.getValue(), buff.getRate());
        } else {
            OrganismDTO organism = from.getOrganismInfoDTO().getOrganismDTO();
            long buffVal;
            BuffTypeEnum dependsBuffType = buff.getDependsBuffType();
            if (BuffTypeEnum.ATK.equals(dependsBuffType)) {
                buffVal = Math.round(buff.getRate() * BuffUtil.getAtk(organism.getAtk(), from.getBuffMap()));
            } else if (BuffTypeEnum.DEF.equals(dependsBuffType)) {
                buffVal = Math.round(buff.getRate() * BuffUtil.getDef(organism.getDef(), from.getBuffMap()));
            } else if (BuffTypeEnum.MAGIC_ATK.equals(dependsBuffType)) {
                buffVal = Math.round(buff.getRate() * BuffUtil.getMagicAtk(organism.getMagicAtk(), from.getBuffMap()));
            } else if (BuffTypeEnum.MAGIC_DEF.equals(dependsBuffType)) {
                buffVal = Math.round(buff.getRate() * BuffUtil.getMagicDef(organism.getMagicDef(), from.getBuffMap()));
            } else if (BuffTypeEnum.SPEED.equals(dependsBuffType)) {
                buffVal = Math.round(buff.getRate() * BuffUtil.getSpeed(organism.getBehaviorSpeed(), from.getBuffMap()));
            } else {
                buffVal = 0L;
            }
            buffDTO = new BuffDTO(buff.getBuffType(), buff.getBuffName(), buff.getValue() + buffVal);
        }
        return buffDTO;
    }

    /**
     * 计算伤害值
     *
     * @param from           来源方
     * @param tar            目标方
     * @param fromOrganism   来源方生物信息
     * @param tarOrganism    目标方生物信息
     * @param damageTypeEnum 伤害类型
     * @param highestRank    最高rank
     * @param damageRate     伤害倍率
     * @return 伤害值
     */
    private static long getDamage(BattleDTO from, BattleDTO tar, OrganismDTO fromOrganism,
                                  OrganismDTO tarOrganism, DamageTypeEnum damageTypeEnum,
                                  long highestRank, float damageRate) {
        // 1.计算伤害
        long realAtk, realDef;
        float realPenetrate;
        // 根据伤害类型区分
        if (DamageTypeEnum.ATK.equals(damageTypeEnum)) {
            // 1.基础属性
            realAtk = fromOrganism.getAtk();
            realPenetrate = fromOrganism.getPenetrate();
            realDef = tarOrganism.getDef();
            // 2.装备加成
            realAtk = EquipmentUtil.getLongVal(realAtk, EquipmentPropertiesTypeEnum.ATK,
                    from.getOrganismInfoDTO().getEquipmentBarDTO());
            realDef = EquipmentUtil.getLongVal(realDef, EquipmentPropertiesTypeEnum.DEF,
                    tar.getOrganismInfoDTO().getEquipmentBarDTO());
            // 3.buff
            realAtk = BuffUtil.getAtk(realAtk, from.getBuffMap());
            realDef = BuffUtil.getDef(realDef, tar.getBuffMap());
        } else if (DamageTypeEnum.MAGIC.equals(damageTypeEnum)) {
            // 1.基础属性
            realAtk = fromOrganism.getMagicAtk();
            realPenetrate = fromOrganism.getMagicPenetrate();
            realDef = tarOrganism.getMagicDef();
            // 2.装备加成
            realAtk = EquipmentUtil.getLongVal(realAtk, EquipmentPropertiesTypeEnum.MAGIC_ATK,
                    from.getOrganismInfoDTO().getEquipmentBarDTO());
            realDef = EquipmentUtil.getLongVal(realDef, EquipmentPropertiesTypeEnum.MAGIC_DEF,
                    tar.getOrganismInfoDTO().getEquipmentBarDTO());
            // 3.buff
            realAtk = BuffUtil.getMagicAtk(realAtk, from.getBuffMap());
            realDef = BuffUtil.getMagicDef(realDef, tar.getBuffMap());
        } else {
            return 0;
        }
        // 境界压制
        realAtk = RankUtil.getRankSupression(fromOrganism.getRank(), highestRank, realAtk);
        realDef = RankUtil.getRankSupression(tarOrganism.getRank(),
                fromOrganism.getRank(), realDef);
        // 计算公式 damage = realAtk - realDef * (1 - realPenetrate)
        // 技能倍率
        long damage = Math.round((realAtk - realDef * (1 - realPenetrate)) * damageRate);
        // 伤害最低为0
        return Math.max(damage, 0L);
    }

    private static boolean isHit(OrganismDTO fromOrganism, OrganismDTO tarOrganism, BattleDTO from, BattleDTO tar,
                                 List<ExtraBattleProcessTemplate> extraBattleProcessList,
                                 SkillEnum nowSkill, StringBuilder builder) {
        // 命中率 = 1 + (命中 - 闪避) / 闪避
        float fromHit = BuffUtil.getHit(
                EquipmentUtil.getLongVal(
                        fromOrganism.getHit(),
                        EquipmentPropertiesTypeEnum.HIT,
                        from.getOrganismInfoDTO().getEquipmentBarDTO()
                ), from.getBuffMap()
        );
        float targetDodge = BuffUtil.getDodge(
                EquipmentUtil.getLongVal(
                        tarOrganism.getDodge(),
                        EquipmentPropertiesTypeEnum.DODGE,
                        tar.getOrganismInfoDTO().getEquipmentBarDTO()
                ), tar.getBuffMap()
        );
        float hitRate = 1 + (fromHit - targetDodge) / targetDodge;
        boolean isHit = RandomUtil.isHit(RandomUtil.format(hitRate, 4));
        if (!isHit) {
            // 未命中插入结算
            extraBattleProcessList.forEach(ext -> ext.ifNotHit(nowSkill, builder));
            builder.append(",没有命中").append(tarOrganism.getName());
        }
        return isHit;
    }

    private static long getCriticalDamage(long damage, OrganismDTO fromOrganism, OrganismDTO tarOrganism,
                                          BattleDTO from, BattleDTO tar, StringBuilder builder) {
        // 若伤害不为0，计算是否暴击
        if (damage > 0) {
            float criticalRate = EquipmentUtil.getFloatVal(
                    fromOrganism.getCriticalRate(),
                    EquipmentPropertiesTypeEnum.CRITICAL_RATE,
                    from.getOrganismInfoDTO().getEquipmentBarDTO()
            );
            float criticalReductionRate = EquipmentUtil.getFloatVal(
                    tarOrganism.getCriticalReductionRate(),
                    EquipmentPropertiesTypeEnum.CRITICAL_REDUCTION_RATE,
                    tar.getOrganismInfoDTO().getEquipmentBarDTO()
            );

            if (RandomUtil.isHit(Math.max(criticalRate - criticalReductionRate, 0))) {
                float criticalDamage = EquipmentUtil.getFloatVal(
                        fromOrganism.getCriticalDamage(),
                        EquipmentPropertiesTypeEnum.CRITICAL_DAMAGE,
                        from.getOrganismInfoDTO().getEquipmentBarDTO()
                );
                float criticalDamageReduction = EquipmentUtil.getFloatVal(
                        tarOrganism.getCriticalDamageReduction(),
                        EquipmentPropertiesTypeEnum.CRITICAL_DAMAGE_REDUCTION,
                        tar.getOrganismInfoDTO().getEquipmentBarDTO()
                );
                float realCriticalDamage = criticalDamage - criticalDamageReduction;
                builder.append("，造成暴击，伤害提升至").append(realCriticalDamage * 100).append("%");
                damage *= realCriticalDamage;
            }
        }
        return damage;
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
