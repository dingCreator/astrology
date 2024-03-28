package com.dingCreator.astrology.util;

import cn.hutool.core.collection.CollectionUtil;
import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.cache.SkillCache;
import com.dingCreator.astrology.cache.TeamCache;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.dto.*;
import com.dingCreator.astrology.dto.skill.SkillBarDTO;
import com.dingCreator.astrology.dto.skill.SkillBuffDTO;
import com.dingCreator.astrology.enums.BelongToEnum;
import com.dingCreator.astrology.enums.BuffTypeEnum;
import com.dingCreator.astrology.enums.exception.PlayerExceptionEnum;
import com.dingCreator.astrology.enums.exception.TeamExceptionEnum;
import com.dingCreator.astrology.enums.skill.DamageTypeEnum;
import com.dingCreator.astrology.enums.skill.SkillEnum;
import com.dingCreator.astrology.enums.skill.SkillTargetEnum;
import com.dingCreator.astrology.exception.BusinessException;
import com.dingCreator.astrology.service.SkillBarItemService;
import com.dingCreator.astrology.vo.BattleResultVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
        long seconds = CdUtil.getCd(Constants.CD_BATTLE_PREFIX + initiatorId);
        if (seconds > 0) {
            throw new BusinessException(Constants.CD_EXCEPTION_PREFIX + "000", "CD:" + seconds + "s");
        }

        // 获取玩家信息
        PlayerDTO initiatorPlayer = PlayerCache.getPlayerById(initiatorId);
        PlayerDTO recipientPlayer = PlayerCache.getPlayerById(recipientId);

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

        // 锁
        acquired(initiatorId);
        acquired(recipientId);
        lock(initiatorTeamPlayers, initiatorId);
        lock(recipientTeamPlayers, initiatorId);
        // cd
        CdUtil.addCd(Constants.CD_BATTLE_PREFIX + initiatorId, Constants.BATTLE_CD);
        return Constants.ACCEPT_BATTLE_TIME_OUT;
    }

    /**
     * 接受决斗
     *
     * @param recipientId 接受玩家ID
     * @return 战斗结果
     */
    public static BattleResultVO acceptBattle(Long recipientId) {
        PlayerDTO playerDTO = PlayerCache.getPlayerById(recipientId);
        // 获取决斗信息
        BattleRequest request = BATTLE_REQUEST_MAP.get(recipientId);
        if (Objects.isNull(request)) {
            throw PlayerExceptionEnum.NO_BATTLE.getException();
        }

        List<Long> initiatorIds, recipientIds;
        // 获取接收方
        if (playerDTO.getTeam()) {
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
        TeamDTO initiatorTeam = TeamCache.getTeamById(request.initiatorId);
        if (Objects.nonNull(initiatorTeam)) {
            initiatorIds = initiatorTeam.getMembers();
        } else {
            initiatorIds = Collections.singletonList(request.getInitiatorId());
        }
        // 释放锁
        unlock(initiatorIds);
        unlock(recipientIds);
        Duration duration = Duration.between(request.getRequestTime(), LocalDateTime.now());
        if (duration.getSeconds() > Constants.ACCEPT_BATTLE_TIME_OUT) {
            throw PlayerExceptionEnum.BATTLE_EXPIRED.getException();
        }
        return battlePVP(request.getInitiatorId(), recipientId);
    }

    /**
     * 拒绝决斗
     * @param recipientId 接受者ID
     * @return 发起者ID
     */
    public static long refuseBattle(Long recipientId) {
        PlayerDTO playerDTO = PlayerCache.getPlayerById(recipientId);
        // 获取决斗信息
        BattleRequest request = BATTLE_REQUEST_MAP.get(recipientId);
        if (Objects.isNull(request)) {
            throw PlayerExceptionEnum.NO_BATTLE.getException();
        }

        List<Long> initiatorIds, recipientIds;
        // 获取接收方
        if (playerDTO.getTeam()) {
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
        TeamDTO initiatorTeam = TeamCache.getTeamById(request.initiatorId);
        if (Objects.nonNull(initiatorTeam)) {
            initiatorIds = initiatorTeam.getMembers();
        } else {
            initiatorIds = Collections.singletonList(request.getInitiatorId());
        }

        // 释放锁
        unlock(initiatorIds);
        unlock(recipientIds);
        Duration duration = Duration.between(request.getRequestTime(), LocalDateTime.now());
        if (duration.getSeconds() > Constants.ACCEPT_BATTLE_TIME_OUT) {
            throw PlayerExceptionEnum.BATTLE_EXPIRED.getException();
        }
        return request.getInitiatorId();
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
            // 决斗尚未超时
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
     * 锁
     *
     * @param playerIds   需要上锁的玩家ID
     * @param initiatorId 发起者ID
     */
    public static void lock(List<Long> playerIds, Long initiatorId) {
        playerIds.forEach(playerId ->
                BATTLE_REQUEST_MAP.put(playerId, new BattleRequest(LocalDateTime.now(), initiatorId)));
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
        PlayerDTO initiatorDTO = PlayerCache.getPlayerById(initiatorId);
        PlayerDTO recipientDTO = PlayerCache.getPlayerById(recipientId);

        List<OrganismDTO> initiatorList;
        List<OrganismDTO> recipientList;
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

        BattleResultVO vo = battle(initiatorList, recipientList);
        vo.setInitiatorId(initiatorId);
        vo.setRecipientId(recipientId);

        PlayerCache.flush(initiatorIds);
        PlayerCache.flush(recipientIds);
        return vo;
    }

    /**
     * PVE
     *
     * @param initiatorId         发起人ID
     * @param recipientBelongTo   归属类型
     * @param recipientBelongToId 归属ID
     * @return 战斗结果
     */
    public static BattleResultVO battlePVE(Long initiatorId, String recipientBelongTo, Long recipientBelongToId) {
        // 四种情况
        return null;
    }

    private static List<OrganismDTO> getOrganismByPlayerId(List<Long> ids) {
        return ids.stream().map(id -> {
            PlayerDTO playerDTO = PlayerCache.getPlayerById(id);
            OrganismDTO organismDTO = new OrganismDTO();
            organismDTO.setOrganism(playerDTO.getPlayer());
            organismDTO.setSkillBarDTO(SkillCache.getSkillBarItem(BelongToEnum.Player.getBelongTo(), playerDTO.getPlayer().getId()));
            return organismDTO;
        }).collect(Collectors.toList());
    }

    public static BattleResultVO battle(List<OrganismDTO> initiator, List<OrganismDTO> recipient) {
        List<BattleDTO> initiatorBattleTmp = new ArrayList<>(initiator.size());
        List<BattleDTO> recipientBattleTmp = new ArrayList<>(recipient.size());

        // 包装
        long totalBehaviorSpeed = 0;
        for (OrganismDTO o : initiator) {
            totalBehaviorSpeed += o.getOrganism().getBehaviorSpeed();
            initiatorBattleTmp.add(buildBattleDTO(o));
        }
        for (OrganismDTO o : recipient) {
            totalBehaviorSpeed += o.getOrganism().getBehaviorSpeed();
            recipientBattleTmp.add(buildBattleDTO(o));
        }

        int maxRound = (initiator.size() + recipient.size()) * 50;
        return battle(initiatorBattleTmp, recipientBattleTmp, totalBehaviorSpeed, maxRound);
    }

    /**
     * 战斗
     *
     * @param initiatorBattleTmp 发起方战斗信息
     * @param recipientBattleTmp 接收方战斗信息
     * @return 战斗结果
     */
    public static synchronized BattleResultVO battle(List<BattleDTO> initiatorBattleTmp,
                                                     List<BattleDTO> recipientBattleTmp,
                                                     final long totalBehavior, final int maxRound) {
        final AtomicInteger round = new AtomicInteger(0);
        List<String> battleMsg = new ArrayList<>(maxRound);
        while (round.get() < maxRound
                && initiatorBattleTmp.stream().mapToLong(o -> o.getOrganismDTO().getOrganism().getHp()).sum() > 0
                && recipientBattleTmp.stream().mapToLong(o -> o.getOrganismDTO().getOrganism().getHp()).sum() > 0) {
            battleProcess(initiatorBattleTmp, recipientBattleTmp, totalBehavior, round, battleMsg);
            battleProcess(recipientBattleTmp, initiatorBattleTmp, totalBehavior, round, battleMsg);
        }
        // 判断胜负
        long initiatorAliveNum = initiatorBattleTmp.stream().filter(o -> o.getOrganismDTO().getOrganism().getHp() > 0).count();
        long recipientAliveNum = recipientBattleTmp.stream().filter(o -> o.getOrganismDTO().getOrganism().getHp() > 0).count();

        BattleResultVO response = new BattleResultVO();
        response.setInfo(battleMsg);
        if (initiatorAliveNum > recipientAliveNum) {
            response.setBattleResult(BattleResultVO.BattleResult.WIN);
        } else if (initiatorAliveNum == recipientAliveNum) {
            response.setBattleResult(BattleResultVO.BattleResult.DRAW);
        } else {
            response.setBattleResult(BattleResultVO.BattleResult.LOSE);
        }
        return response;
    }

    /**
     * 构建战斗包装
     *
     * @param organismDTO 生物信息
     * @return 战斗包装
     */
    private static BattleDTO buildBattleDTO(OrganismDTO organismDTO) {
        BattleDTO battleDTO = new BattleDTO();
        battleDTO.setOrganismDTO(organismDTO);
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
                                      final long totalBehavior, final AtomicInteger round, List<String> battleMsg) {
        from.forEach(o -> {
            long behavior = o.getBehavior() + o.getOrganismDTO().getOrganism().getBehaviorSpeed();
            if (behavior > totalBehavior && o.getOrganismDTO().getOrganism().getHp() > 0) {
                attackBehavior(o, from, to, battleMsg);
                behavior -= totalBehavior;
                round.addAndGet(1);
                // buff轮次-1 并清除所有过期buff
                o.getBuffMap().values().stream()
                        .peek(buffList -> buffList.forEach(buff -> buff.setRound(buff.getRound() - 1)))
                        .forEach(buffList -> buffList.removeIf(buffDTO -> buffDTO.getRound() <= 0));
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
    private static void attackBehavior(BattleDTO from, List<BattleDTO> our, List<BattleDTO> enemy, List<String> battleMsg) {
        StringBuilder builder = new StringBuilder();
        // 获取技能
        SkillBarDTO bar = from.getOrganismDTO().getSkillBarDTO();
        // 技能轮转
        if (bar.getNext() != null) {
            from.getOrganismDTO().setSkillBarDTO(bar.getNext());
        } else {
            from.getOrganismDTO().setSkillBarDTO(bar.getHead());
        }
        // 获取技能信息
        SkillEnum skillEnum = SkillEnum.getById(bar.getSkillId());
        // 扣取mp
        long mp = from.getOrganismDTO().getOrganism().getMp();
        // mp < 0表示无限mp
        if (mp > 0) {
            if (mp < skillEnum.getMp()) {
                // 蓝不够 替换为默认技能 若没有默认技能 则原地罚站
                if ((skillEnum = from.getOrganismDTO().getDefaultSkill()) == null) {
                    return;
                }
            } else {
                mp -= skillEnum.getMp();
                from.getOrganismDTO().getOrganism().setMp(mp);
            }
        }
        builder.append(from.getOrganismDTO().getOrganism().getName())
                .append("使用技能【")
                .append(skillEnum.getName())
                .append("】");

        skillEnum.getSkillEffects().forEach(skillEffect -> {
            // 选择技能目标
            List<BattleDTO> target = getSkillTarget(skillEffect.getSkillTargetEnum(), from, our, enemy);
            // 获取目标方最高阶级
            long highestRank = target.stream().mapToLong(b -> b.getOrganismDTO().getOrganism().getRank()).max().orElse(0L);
            target.forEach(tar -> {
                if (skillEffect.getSkillTargetEnum().isEnemy()) {
                    // 技能目标位地方，计算是否命中
                    // 命中率 = 1 + (命中 - 闪避) / 闪避
                    float fromHit = BuffUtil.getHit(from.getOrganismDTO().getOrganism().getHit(), from.getBuffMap());
                    float targetDodge = BuffUtil.getDodge(tar.getOrganismDTO().getOrganism().getDodge(), tar.getBuffMap());
                    float hitRate = 1 + (fromHit - targetDodge) / targetDodge;
                    boolean isHit = RandomUtil.isHit(RandomUtil.format(hitRate, 4));
                    if (!isHit) {
                        builder.append(",没有命中").append(tar.getOrganismDTO().getOrganism().getName());
                        return;
                    }
                }

                // 1.计算伤害
                long realAtk, realDef;
                float realPenetrate;
                // 根据伤害类型区分
                if (DamageTypeEnum.ATK.equals(skillEffect.getDamageTypeEnum())) {
                    // 1.基础属性
                    realAtk = from.getOrganismDTO().getOrganism().getAtk();
                    realPenetrate = from.getOrganismDTO().getOrganism().getPenetrate();
                    realDef = tar.getOrganismDTO().getOrganism().getDef();
                    // 2.装备加成
                    // todo
                    // 3.buff
                    realAtk = BuffUtil.getAtk(realAtk, from.getBuffMap());
                    realDef = BuffUtil.getDef(realDef, tar.getBuffMap());
                } else if (DamageTypeEnum.MAGIC.equals(skillEffect.getDamageTypeEnum())) {
                    // 1.基础属性
                    realAtk = from.getOrganismDTO().getOrganism().getMagicAtk();
                    realPenetrate = from.getOrganismDTO().getOrganism().getMagicPenetrate();
                    realDef = tar.getOrganismDTO().getOrganism().getMagicDef();
                    // 2.装备加成
                    // todo
                    // 3.buff
                    realAtk = BuffUtil.getMagicAtk(realAtk, from.getBuffMap());
                    realDef = BuffUtil.getMagicDef(realDef, tar.getBuffMap());
                } else {
                    return;
                }

                // 技能倍率
                realAtk = Math.round(realAtk * skillEffect.getDamageRate());
                // 境界压制
                realAtk = RankUtil.getRankSupression(from.getOrganismDTO().getOrganism().getRank(), highestRank, realAtk);
                realDef = RankUtil.getRankSupression(tar.getOrganismDTO().getOrganism().getRank(),
                        from.getOrganismDTO().getOrganism().getRank(), realDef);

                // damage = realAtk - realDef * (1 - realPenetrate)
                long damage = Math.round(realAtk - realDef * (1 - realPenetrate));
                long hp = tar.getOrganismDTO().getOrganism().getHp() - damage;
                hp = hp > 0 ? hp : 0;
                tar.getOrganismDTO().getOrganism().setHp(hp);

                if (skillEffect.getDamageRate() > 0) {
                    // 没有伤害的不需要此段信息
                    builder.append(",对").append(tar.getOrganismDTO().getOrganism().getName())
                            .append("造成").append(damage).append("点伤害")
                            .append("，").append(tar.getOrganismDTO().getOrganism().getName())
                            .append("剩余生命值：").append(tar.getOrganismDTO().getOrganism().getHp());
                }
                // 若对方没死，计算buff
                if (hp > 0) {
                    List<SkillBuffDTO> skillBuffDTOList = skillEffect.getSkillBuffDTOList();
                    if (CollectionUtil.isNotEmpty(skillBuffDTOList)) {
                        // 概率附加buff
                        skillEffect.getSkillBuffDTOList().stream().filter(buff -> RandomUtil.isHit(buff.getEffectRate()))
                                .forEach(buff -> {
                                    builder.append("");
                                    BuffUtil.addBuff(tar, buff.getBuffDTO(), buff.getRound());
                                });
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
            target = enemy.stream().filter(b -> b.getOrganismDTO().getOrganism().getHp() > 0).collect(Collectors.toList());
        } else {
            target = our.stream().filter(b -> b.getOrganismDTO().getOrganism().getHp() > 0).collect(Collectors.toList());
        }

        if (SkillTargetEnum.ANY_ENEMY.equals(skillTargetEnum)) {
            // 任意敌方，需要计算嘲讽
            List<BattleDTO> taunt = target.stream().filter(t -> {
                List<BattleBuffDTO> buffList = t.getBuffMap().getOrDefault(BuffTypeEnum.TAUNT.getName(), null);
                return CollectionUtil.isNotEmpty(buffList);
            }).collect(Collectors.toList());

            if (taunt.size() > 0) {
                target = Collections.singletonList(
                        taunt.stream().max(Comparator.comparing(t -> t.getOrganismDTO().getOrganism().getHp())).get());
            } else {
                target = Collections.singletonList(target.get(0));
            }
        } else if (SkillTargetEnum.ALL_ENEMY.equals(skillTargetEnum)) {
            // 全体敌方
            // 无需处理
        } else if (SkillTargetEnum.ANY_OUR.equals(skillTargetEnum)) {
            // 任意我方
            // todo
        } else if (SkillTargetEnum.ALL_OUR.equals(skillTargetEnum)) {
            // 全体我方
            // 无需处理
        } else if (SkillTargetEnum.ME.equals(skillTargetEnum)) {
            // 自己
            target = Collections.singletonList(from);
        }
        return target;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BattleRequest implements Serializable {
        /**
         * 发起决斗的时间
         */
        private LocalDateTime requestTime;
        /**
         * 发起者ID
         */
        private Long initiatorId;
    }
}
