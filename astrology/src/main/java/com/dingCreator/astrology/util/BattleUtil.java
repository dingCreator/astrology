package com.dingCreator.astrology.util;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.cache.SkillCache;
import com.dingCreator.astrology.cache.TeamCache;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.dto.TeamDTO;
import com.dingCreator.astrology.dto.battle.*;
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
import com.dingCreator.astrology.enums.EffectTypeEnum;
import com.dingCreator.astrology.enums.equipment.EquipmentEnum;
import com.dingCreator.astrology.enums.equipment.EquipmentPropertiesTypeEnum;
import com.dingCreator.astrology.enums.exception.BattleExceptionEnum;
import com.dingCreator.astrology.enums.exception.PlayerExceptionEnum;
import com.dingCreator.astrology.enums.exception.TeamExceptionEnum;
import com.dingCreator.astrology.enums.job.JobInitPropertiesEnum;
import com.dingCreator.astrology.enums.skill.DamageTypeEnum;
import com.dingCreator.astrology.enums.skill.SkillEnum;
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
     * 战斗回合缓存
     */
    private static final Map<Long, List<BattleRoundRecordDTO>> BATTLE_ROUND_RECORD = new HashMap<>(256);

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
        ThreadPoolUtil.executeBiConsumer(BattleUtil::addBattleRoundRecord, initiatorIds, vo.getRoundRecordList());
        ThreadPoolUtil.executeBiConsumer(BattleUtil::addBattleRoundRecord, recipientIds, vo.getRoundRecordList());
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
        ThreadPoolUtil.executeBiConsumer(BattleUtil::addBattleRoundRecord, playerIds, vo.getRoundRecordList());
        return vo;
    }

    /**
     * 斗蛐蛐
     *
     * @param monsterIdList1 怪物列表1
     * @param monsterIdList2 怪物列表2
     * @return 斗蛐蛐结果
     */
    public static BattleResultVO battleEVE(Long playerId, List<Long> monsterIdList1, List<Long> monsterIdList2, int maxRound) {
        BattleResultVO vo;
        if (maxRound > 0) {
            vo = battle(getOrganismByMonsterId(monsterIdList1), getOrganismByMonsterId(monsterIdList2), maxRound);
        } else {
            vo = battle(getOrganismByMonsterId(monsterIdList1), getOrganismByMonsterId(monsterIdList2));
        }
        ThreadPoolUtil.executeBiConsumer(BattleUtil::writeBattleProcess, Collections.singletonList(playerId), vo.getInfo());
        ThreadPoolUtil.executeBiConsumer(BattleUtil::addBattleRoundRecord, Collections.singletonList(playerId), vo.getRoundRecordList());
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
        return battle(initiator, recipient, (initiator.size() + recipient.size()) * 50);
    }

    /**
     * 战斗
     *
     * @param initiator 发起方
     * @param recipient 接收方
     * @return 战斗信息
     */
    public static BattleResultVO battle(List<OrganismInfoDTO> initiator, List<OrganismInfoDTO> recipient, int maxRound) {
        List<BattleDTO> initiatorBattleTmp = new ArrayList<>(initiator.size());
        List<BattleDTO> recipientBattleTmp = new ArrayList<>(recipient.size());
        // 包装
        for (OrganismInfoDTO o : initiator) {
            initiatorBattleTmp.add(buildBattleDTO(o, false));
        }
        for (OrganismInfoDTO o : recipient) {
            recipientBattleTmp.add(buildBattleDTO(o, false));
        }
        // 初始化插入结算列表
        List<ExtraBattleProcessTemplate> tmpBattleProcessList = new ArrayList<>();
        List<ExtraBattleProcessTemplate> extraBattleProcessList = new ArrayList<>();
        // 插入被动技能效果结算
        initiatorBattleTmp.forEach(initiatorObj -> initExtraProcess(initiatorObj, initiatorBattleTmp, recipientBattleTmp, tmpBattleProcessList));
        recipientBattleTmp.forEach(recipientObj -> initExtraProcess(recipientObj, recipientBattleTmp, initiatorBattleTmp, tmpBattleProcessList));
        // 优先级排序
        if (CollectionUtils.isNotEmpty(tmpBattleProcessList)) {
            extraBattleProcessList = tmpBattleProcessList.stream()
                    .sorted(Comparator.comparing(ExtraBattleProcessTemplate::getPriority)).collect(Collectors.toList());
        }
        BattleFieldDTO field = BattleFieldDTO.builder()
                .initiatorList(initiatorBattleTmp)
                .recipientList(recipientBattleTmp)
                .maxRound(maxRound)
                .roundRecordList(new ArrayList<>())
                .extraBattleProcessTemplateList(extraBattleProcessList).build();
        return battle(field);
    }

    /**
     * 战斗
     *
     * @param field 战场
     * @return 战斗结果
     */
    public static BattleResultVO battle(BattleFieldDTO field) {
        long startTime = System.currentTimeMillis();
        BattleResultVO response = field.startBattle();
        LOGGER.info("battle process cost: {} ms", System.currentTimeMillis() - startTime);
        return response;
    }

    public static void getRankSuppression(List<BattleDTO> battleTmp, long highestRank, List<String> battleMsg) {
        StringBuilder builder = new StringBuilder();
        long count = battleTmp.stream()
                .filter(i -> i.getOrganismInfoDTO().getOrganismDTO().getRank() < highestRank)
                .peek(r -> {
                    OrganismDTO organismDTO = r.getOrganismInfoDTO().getOrganismDTO();
                    float rate = RankUtil.getRankSuppression(organismDTO.getRank(), highestRank);
                    RuleUtil.addRule(r, EffectTypeEnum.ATK, "境界压制", rate, builder);
                    RuleUtil.addRule(r, EffectTypeEnum.MAGIC_ATK, "境界压制", rate, builder);
                    RuleUtil.addRule(r, EffectTypeEnum.DEF, "境界压制", rate, builder);
                    RuleUtil.addRule(r, EffectTypeEnum.MAGIC_DEF, "境界压制", rate, builder);
                    RuleUtil.addRule(r, EffectTypeEnum.HIT, "境界压制", rate, builder);
                    RuleUtil.addRule(r, EffectTypeEnum.DODGE, "境界压制", rate, builder);
                    RuleUtil.addRule(r, EffectTypeEnum.SPEED, "境界压制", rate, builder);
                }).count();
        if (count > 0) {
            battleMsg.add("※" + builder);
        }
    }

    /**
     * 初始化插入结算
     *
     * @param from 当前目标
     */
    private static void initExtraProcess(BattleDTO from, List<BattleDTO> our, List<BattleDTO> enemy,
                                         List<ExtraBattleProcessTemplate> extraBattleProcessList) {
        // 被动技能
        for (SkillEnum skill : from.getOrganismInfoDTO().getInactiveSkills()) {
            ExtraBattleProcessTemplate process = CopyUtil.copyNewInstance(skill.getGlobalExtraProcess());
            process.setOwner(from);
            process.setOwnerOur(our);
            process.setOwnerEnemy(enemy);
            extraBattleProcessList.add(process);
        }

        // 主动技能
        SkillBarDTO skillBarDTO = from.getOrganismInfoDTO().getSkillBarDTO();
        Set<Long> distinctSet = new HashSet<>();
        do {
            if (!distinctSet.add(skillBarDTO.getSkillId())) {
                skillBarDTO = skillBarDTO.getNext();
                continue;
            }
            ExtraBattleProcessTemplate process
                    = CopyUtil.copyNewInstance(SkillEnum.getById(skillBarDTO.getSkillId()).getGlobalExtraProcess());
            process.setOwner(from);
            process.setOwnerOur(our);
            process.setOwnerEnemy(enemy);
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
     * @param equipmentDTO           装备
     * @param extraBattleProcessList 插入结算列表
     */
    private static void initEquipmentExtraProcess(BattleDTO from, List<BattleDTO> our, List<BattleDTO> enemy, EquipmentDTO equipmentDTO,
                                                  List<ExtraBattleProcessTemplate> extraBattleProcessList) {
        if (Objects.isNull(equipmentDTO)) {
            return;
        }
        EquipmentEnum equipmentEnum = EquipmentEnum.getById(equipmentDTO.getEquipmentId());
        ExtraBattleProcessTemplate process = CopyUtil.copyNewInstance(equipmentEnum.getExtraBattleProcessTemplate());
        process.setOwner(from);
        process.setOwnerOur(our);
        process.setOwnerEnemy(enemy);
        extraBattleProcessList.add(process);
    }

    /**
     * 构建战斗包装
     *
     * @param organismInfoDTO 生物信息
     * @return 战斗包装
     */
    public static BattleDTO buildBattleDTO(OrganismInfoDTO organismInfoDTO, boolean summoned) {
        BattleDTO battleDTO = new BattleDTO();
        battleDTO.setOrganismInfoDTO(organismInfoDTO);
        battleDTO.setBehavior(0L);
        battleDTO.setBuffMap(new HashMap<>());
        battleDTO.setMarkMap(new HashMap<>());
        battleDTO.setRuleList(new ArrayList<>());
        battleDTO.setRound(0L);
        battleDTO.setSummoned(summoned);
        return battleDTO;
    }

    public static long getDamage(BattleDTO from, BattleDTO tar, BattleRoundDTO battleRound, SkillEffectDTO skillEffect) {
        return getDamage(BattleEffectDTO.builder().from(from).tar(tar).battleRound(battleRound)
                .skillEffect(skillEffect).build());
    }

    /**
     * 计算伤害值
     *
     * @param battleEffect 技能单个效果单个对象流程
     * @return 伤害值
     */
    public static long getDamage(BattleEffectDTO battleEffect) {
        BattleDTO from = battleEffect.getFrom();
        BattleDTO tar = battleEffect.getTar();
        OrganismDTO fromOrganism = from.getOrganismInfoDTO().getOrganismDTO();
        OrganismDTO tarOrganism = tar.getOrganismInfoDTO().getOrganismDTO();
        BattleFieldDTO battleField = battleEffect.getBattleRound().getBattleField();
        // 1.计算伤害
        long realAtk, realDef;
        float realPenetrate;
        // 根据伤害类型区分
        DamageTypeEnum damageTypeEnum = battleEffect.getSkillEffect().getDamageTypeEnum();
        // 1.基础属性 2.装备加成 3.buff
        if (DamageTypeEnum.ATK.equals(damageTypeEnum)) {
            realAtk = getLongProperty(fromOrganism.getAtk(), EffectTypeEnum.ATK.getName(), from, battleField);
            realPenetrate = getRateProperty(fromOrganism.getPenetrate(), EffectTypeEnum.PENETRATE.getName(), from, battleField);
            realDef = getLongProperty(tarOrganism.getDef(), EffectTypeEnum.DEF.getName(), tar, battleField);
        } else if (DamageTypeEnum.MAGIC.equals(damageTypeEnum)) {
            realAtk = getLongProperty(fromOrganism.getMagicAtk(), EffectTypeEnum.MAGIC_ATK.getName(), from, battleField);
            realPenetrate = getRateProperty(fromOrganism.getMagicPenetrate(), EffectTypeEnum.MAGIC_PENETRATE.getName(), from, battleField);
            realDef = getLongProperty(tarOrganism.getMagicDef(), EffectTypeEnum.MAGIC_DEF.getName(), tar, battleField);
        } else {
            return 0;
        }
        // 穿甲最大为100%
        realPenetrate = Math.min(realPenetrate, 1);
        // 技能倍率
        BigDecimal damageRateDecimal = getDamageRate(battleEffect);
        // 计算公式 damage = realAtk - realDef * (1 - realPenetrate)
//        long damage = Math.round((realAtk - realDef * (1 - realPenetrate)) * damageRateDecimal.floatValue());
        // 测试公式 攻击*系数/（防御+系数）
        long damage = Math.round(realAtk * 1000 / (realDef * (1 - realPenetrate) + 1000) * damageRateDecimal.floatValue());
        // 伤害最低为0
        return Math.max(damage, 0L);
    }

    public static BigDecimal getDamageRate(BattleEffectDTO battleEffect) {
        List<ExtraBattleProcessTemplate> tplList = battleEffect.getBattleRound().getBattleField()
                .getExtraBattleProcessTemplateList();
        if (CollectionUtil.isNotEmpty(tplList)) {
            for (ExtraBattleProcessTemplate tpl : tplList) {
                if (tpl.getOwner().getOrganismInfoDTO().getOrganismDTO().getHpWithAddition() <= 0) {
                    continue;
                }
                tpl.executeChangeDamageRate(battleEffect);
            }
        }
        if (Objects.nonNull(battleEffect.getNowSkill())
                && Objects.nonNull(battleEffect.getNowSkill().getThisBehaviorExtraProcess())) {
            battleEffect.getNowSkill().getThisBehaviorExtraProcess().changeDamageRate(battleEffect);
        }
        if (Objects.nonNull(battleEffect.getSkillEffect())
                && Objects.nonNull(battleEffect.getSkillEffect().getTemplate())) {
            battleEffect.getSkillEffect().getTemplate().changeDamageRate(battleEffect);
        }
        return battleEffect.getDamageRate();
    }

    public static boolean isHit(BattleDTO from, BattleDTO tar, BattleFieldDTO field) {
        OrganismDTO fromOrganism = from.getOrganismInfoDTO().getOrganismDTO();
        OrganismDTO tarOrganism = tar.getOrganismInfoDTO().getOrganismDTO();
        // 命中率 = 1 + (命中 - 闪避) / 闪避
        float fromHit = getLongProperty(fromOrganism.getHit(), EffectTypeEnum.HIT.getName(), from, field);
        float targetDodge = getLongProperty(tarOrganism.getDodge(), EffectTypeEnum.DODGE.getName(), from, field);
        if (targetDodge == 0) {
            return true;
        }
        float hitRate = 1 + (fromHit - targetDodge) / targetDodge;
        return RandomUtil.isHit(RandomUtil.format(hitRate, 4));
    }

    public static boolean getCriticalDamage(BattleEffectDTO battleEffect) {
        OrganismDTO fromOrganism = battleEffect.getFrom().getOrganismInfoDTO().getOrganismDTO();
        OrganismDTO tarOrganism = battleEffect.getTar().getOrganismInfoDTO().getOrganismDTO();
        BattleFieldDTO battleField = battleEffect.getBattleRound().getBattleField();
        // 若伤害不为0，计算是否暴击
        if (battleEffect.getDamage().get() > 0) {
            float criticalRate = getRateProperty(fromOrganism.getCriticalRate(), EffectTypeEnum.CRITICAL.getName(),
                    battleEffect.getFrom(), battleField);
            float criticalReductionRate = getRateProperty(tarOrganism.getCriticalReductionRate(),
                    EffectTypeEnum.CRITICAL_REDUCTION.getName(), battleEffect.getFrom(), battleField);
            if (RandomUtil.isHit(Math.max(criticalRate - criticalReductionRate, 0))) {
                float criticalDamage = getRateProperty(fromOrganism.getCriticalDamage(),
                        EffectTypeEnum.CRITICAL_DAMAGE.getName(), battleEffect.getFrom(), battleField);
                float criticalDamageReduction = getRateProperty(tarOrganism.getCriticalDamageReduction(),
                        EffectTypeEnum.CRITICAL_DAMAGE_REDUCTION.getName(), battleEffect.getTar(), battleField);
                float realCriticalDamage = criticalDamage - criticalDamageReduction;
                battleEffect.getBattleRound().getBuilder()
                        .append("，暴击，伤害提升至").append(realCriticalDamage * 100).append("%");
                battleEffect.getDamage().set(Math.round(battleEffect.getDamage().get() * realCriticalDamage));
                return true;
            }
        }
        return false;
    }

    public static Long getLongProperty(long src, String propName, BattleDTO battleDTO, BattleFieldDTO field) {
        Long result = EquipmentUtil.getLongVal(src, EquipmentPropertiesTypeEnum.getByNameEn(propName),
                battleDTO.getOrganismInfoDTO().getEquipmentBarDTO());
        result = RuleUtil.getVal(battleDTO, EffectTypeEnum.getByName(propName), result);
        if (Objects.nonNull(field.getFieldEffectEnum())) {
            result = field.getFieldEffectEnum().getEffect().getVal(result, EffectTypeEnum.getByName(propName));
        }
        return Math.max(BuffUtil.getVal(result, EffectTypeEnum.getByName(propName), battleDTO), 0);
    }

    public static Float getRateProperty(float rate, String propName, BattleDTO battleDTO, BattleFieldDTO field) {
        Float result = EquipmentUtil.getFloatVal(rate, EquipmentPropertiesTypeEnum.getByNameEn(propName),
                battleDTO.getOrganismInfoDTO().getEquipmentBarDTO());
        result = RuleUtil.getRate(battleDTO, EffectTypeEnum.getByName(propName), result);
        if (Objects.nonNull(field.getFieldEffectEnum())) {
            result = field.getFieldEffectEnum().getEffect().getRate(result, EffectTypeEnum.getByName(propName));
        }
        return Math.max(BuffUtil.getRate(result, EffectTypeEnum.getByName(propName), battleDTO), 0F);
    }

    public static void doHealing(BattleDTO battleDTO, long heal, StringBuilder builder, BattleFieldDTO battleField) {
        OrganismDTO organismDTO = battleDTO.getOrganismInfoDTO().getOrganismDTO();
        if (organismDTO.getHpWithAddition() <= 0) {
            return;
        }
        long healAfterCal = heal;
        if (battleDTO.getBuffMap().containsKey(EffectTypeEnum.HEAL)) {
            healAfterCal = getLongProperty(heal, EffectTypeEnum.HEAL.getName(), battleDTO, battleField);
            float rate = getRateProperty(1F, EffectTypeEnum.HEAL.getName(), battleDTO, battleField);
            healAfterCal = Math.round(healAfterCal * rate);
        }
        long newHp = Math.min(organismDTO.getHpWithAddition() + healAfterCal, organismDTO.getMaxHpWithAddition());
        builder.append("，血量回复").append(newHp - organismDTO.getHpWithAddition());
        organismDTO.setHpWithAddition(newHp);
    }

    public static void doMpChange(BattleDTO battleDTO, long mp, StringBuilder builder) {
        OrganismDTO organismDTO = battleDTO.getOrganismInfoDTO().getOrganismDTO();
        if (organismDTO.getHpWithAddition() <= 0) {
            return;
        }
        long newMp = Math.min(organismDTO.getMpWithAddition() + mp, organismDTO.getMaxMpWithAddition());
        if (mp >= 0) {
            builder.append("，蓝量回复").append(newMp - organismDTO.getMpWithAddition());
        } else {
            builder.append("，蓝量减少").append(organismDTO.getMpWithAddition() - newMp);
        }
        organismDTO.setMpWithAddition(newMp);
    }

    public static void doRealDamage(BattleDTO target, long realDamage, StringBuilder builder) {
        long oldHp = target.getOrganismInfoDTO().getOrganismDTO().getHpWithAddition();
        if (oldHp <= 0) {
            return;
        }
        long newHp = Math.max(0, oldHp - realDamage);
        target.getOrganismInfoDTO().getOrganismDTO().setHpWithAddition(newHp);
        builder.append("，对").append(target.getOrganismInfoDTO().getOrganismDTO().getName())
                .append("造成").append(oldHp - newHp).append("真实伤害");
    }

    public static void doDamage(BattleEffectDTO battleEffect) {
        doDamage(battleEffect, true);
    }

    public static void doDamage(BattleEffectDTO battleEffect, boolean showDamageText) {
        BattleRoundDTO battleRound = battleEffect.getBattleRound();
        BattleFieldDTO battleField = battleRound.getBattleField();
        BattleDTO target = battleEffect.getTar();

        if (target.getOrganismInfoDTO().getOrganismDTO().getHpWithAddition() <= 0) {
            return;
        }
        DamageTypeEnum damageTypeEnum = Objects.nonNull(battleEffect.getDamageTypeEnum()) ?
                battleEffect.getDamageTypeEnum() : Objects.nonNull(battleEffect.getSkillEffect()) ?
                battleEffect.getSkillEffect().getDamageTypeEnum() : DamageTypeEnum.SPECIAL;
        // 计算伤害变更
        // 先计算造成伤害变化，再计算受到伤害变化
        if (DamageTypeEnum.ATK.equals(damageTypeEnum)) {
            long tmp = BattleUtil.getLongProperty(battleEffect.getDamage().get(), EffectTypeEnum.DO_DAMAGE.getName(),
                    battleEffect.getFrom(), battleField);
            tmp = BattleUtil.getLongProperty(tmp, EffectTypeEnum.DAMAGE.getName(), battleEffect.getTar(), battleField);
            battleEffect.getDamage().set(tmp);
        } else if (DamageTypeEnum.MAGIC.equals(damageTypeEnum)) {
            long tmp = BattleUtil.getLongProperty(battleEffect.getDamage().get(), EffectTypeEnum.DO_MAGIC_DAMAGE.getName(),
                    battleEffect.getFrom(), battleField);
            tmp = BattleUtil.getLongProperty(tmp, EffectTypeEnum.MAGIC_DAMAGE.getName(), battleEffect.getTar(), battleField);
            battleEffect.getDamage().set(tmp);
        }
        // 造成伤害前
        battleField.getExtraBattleProcessTemplateList().forEach(ext -> ext.executeBeforeDamage(battleEffect));
        long damageBf = battleEffect.getDamage().get();
        // 计算盾
        List<BattleBuffDTO> battleBuffList = battleEffect.getTar().getBuffMap().get(EffectTypeEnum.VAL_SHIELD);
        if (CollectionUtils.isNotEmpty(battleBuffList)) {
            battleBuffList.stream().peek(battleBuff -> {
                long shieldVal = battleBuff.getBuffDTO().getValue();
                long damage = battleEffect.getDamage().get();
                if (shieldVal > damage) {
                    battleEffect.getDamage().set(0L);
                    battleBuff.getBuffDTO().setValue(shieldVal - damage);
                } else {
                    battleEffect.getDamage().set(damage - shieldVal);
                    battleBuff.getBuffDTO().setValue(0L);
                }
            }).filter(battleBuff -> battleBuff.getBuffDTO().getValue() > 0).findFirst();
            battleBuffList.removeIf(battleBuff -> battleBuff.getBuffDTO().getValue() <= 0);
            battleRound.getBuilder().append("，被抵消").append(damageBf - battleEffect.getDamage().get()).append("点伤害");
        }
        // 如果是致命伤害，触发特殊结算
        if (battleEffect.getDamage().get() >= target.getOrganismInfoDTO().getOrganismDTO().getHpWithAddition()) {
            battleField.getExtraBattleProcessTemplateList().forEach(ext -> ext.executeBeforeDeath(battleEffect));
        }
        // 若造成了伤害
        if (battleEffect.getDamage().get() > 0) {
            float lifeStealRate = BattleUtil.getRateProperty(
                    battleEffect.getFrom().getOrganismInfoDTO().getOrganismDTO().getLifeStealing(),
                    EffectTypeEnum.LIFE_STEAL.getName(), battleEffect.getFrom(), battleField
            );
            if (lifeStealRate > 0) {
                long heal = Math.round(lifeStealRate * battleEffect.getDamage().get());
                BattleUtil.doHealing(battleEffect.getFrom(), heal, battleRound.getBuilder(), battleField);
            }
            battleField.getExtraBattleProcessTemplateList().forEach(ext -> ext.executeAfterDamage(battleEffect));
        }
        long newHp = Math.max(0, target.getOrganismInfoDTO().getOrganismDTO().getHpWithAddition() - battleEffect.getDamage().get());
        if (showDamageText) {
            battleRound.getBuilder().append("，对").append(target.getOrganismInfoDTO().getOrganismDTO().getName())
                    .append("造成").append(target.getOrganismInfoDTO().getOrganismDTO().getHpWithAddition() - newHp)
                    .append(damageTypeEnum.getTypeChnDesc()).append("伤害");
        }
        target.getOrganismInfoDTO().getOrganismDTO().setHpWithAddition(newHp);
    }

    /**
     * 写入数据
     *
     * @param playerIds       玩家ID
     * @param roundRecordList 回合
     */
    public static void addBattleRoundRecord(List<Long> playerIds, List<BattleRoundRecordDTO> roundRecordList) {
        playerIds.forEach(playerId -> BATTLE_ROUND_RECORD.put(playerId, roundRecordList));
    }

    /**
     * 根据玩家ID和回合数获取某一回合信息
     *
     * @param playerId 玩家ID
     * @param roundIdx 回合数
     * @return 该回合的详细信息
     */
    public static BattleRoundRecordDTO getByPlayerIdAndRound(Long playerId, int roundIdx) {
        List<BattleRoundRecordDTO> recordList = BATTLE_ROUND_RECORD.getOrDefault(playerId, new ArrayList<>());
        if (roundIdx <= 0 || recordList.size() < roundIdx) {
            throw BattleExceptionEnum.BATTLE_ROUND_NOT_FOUND.getException();
        }
        return recordList.get(roundIdx - 1);
    }

    public static void addBattleDTO(OrganismDTO summoner, OrganismDTO summon, SkillBarDTO bar, int count,
                                    List<BattleDTO> list, int index, StringBuilder builder) {
        if (count <= 0) {
            return;
        }
        OrganismInfoDTO undeadInfo = new OrganismInfoDTO();
        undeadInfo.setOrganismDTO(summon);
        undeadInfo.setInactiveSkills(new ArrayList<>());
        undeadInfo.setSkillBarDTO(bar);
        BattleDTO battleDTO = BattleUtil.buildBattleDTO(undeadInfo, true);
        list.add(index, battleDTO);
        builder.append("，").append(summoner.getName()).append("召唤了").append(count).append("只【")
                .append(summon.getName()).append("】");
    }
}
