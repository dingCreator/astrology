package com.dingCreator.astrology.behavior;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.cache.TeamCache;
import com.dingCreator.astrology.cache.WorldBossCache;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.dto.organism.OrganismInfoDTO;
import com.dingCreator.astrology.entity.WorldBossRecord;
import com.dingCreator.astrology.enums.exception.WorldBossExceptionEnum;
import com.dingCreator.astrology.service.MonsterService;
import com.dingCreator.astrology.service.WorldBossRecordService;
import com.dingCreator.astrology.util.BattleUtil;
import com.dingCreator.astrology.util.CdUtil;
import com.dingCreator.astrology.vo.WorldBossChartVO;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ding
 * @date 2024/2/21
 */
public class WorldBossBehavior {

    private static final Object MONITOR = new Object();

    /**
     * 讨伐世界boss
     *
     * @param playerId 玩家ID
     */
    public long attackWorldBoss(Long playerId) {
        long seconds = CdUtil.getAndSetCd(Constants.CD_WORLD_BOSS_PREFIX + playerId, Constants.ATTACK_WORLD_BOSS_CD);
        if (seconds > 0) {
            throw WorldBossExceptionEnum.IN_CD.getException(seconds);
        }

        WorldBossRecord worldBossRecord;
        long damage;
        synchronized (MONITOR) {
            // 血量状态校验
            WorldBossCache.BossPropDTO prop = WorldBossCache.getBossProp();
            if (Objects.isNull(prop)) {
                throw WorldBossExceptionEnum.NOT_EXIST.getException();
            }
            LocalDateTime now = LocalDateTime.now();
            if (now.isBefore(prop.getStartTime())) {
                throw WorldBossExceptionEnum.TOO_EARLY.getException();
            } else if (now.isAfter(prop.getEndTime())) {
                throw WorldBossExceptionEnum.TOO_LATE.getException();
            }
            if (WorldBossCache.getDefeated()) {
                throw WorldBossExceptionEnum.DEFEATED.getException();
            }
            List<Long> playerIdList;
            if (PlayerCache.getPlayerById(playerId).getTeam()) {
                TeamBehavior.getInstance().captainOnlyValidate(playerId);
                playerIdList = TeamCache.getTeamByPlayerId(playerId).getMembers();
            } else {
                playerIdList = Collections.singletonList(playerId);
            }
            worldBossRecord = new WorldBossRecord();
            worldBossRecord.setCreateTime(LocalDateTime.now());
            worldBossRecord.setPlayerIdList(playerIdList.stream().map(Object::toString)
                    .reduce((id1, id2) -> id1 + "|" + id2).orElseThrow(() -> new IllegalArgumentException("id拼接错误")));
            // 攻击次数
            WorldBossCache.increaseAtkTimes(playerIdList);
            // 战斗前boss血量总值
            long hpBeforeBattle = prop.getMonsterMap().values().stream().mapToLong(o -> o.getOrganismDTO()
                    .getHpWithAddition()).sum();
            // 开打
            BattleUtil.battlePVE(playerId, new ArrayList<>(prop.getMonsterMap().values()), true, () ->
                    prop.getMonsterMap().forEach((key, value) ->
                            MonsterService.updateHpById(key, value.getOrganismDTO().getHp()))
            );
            WorldBossCache.commitAtkTimes();
            // 战斗后boss血量总值
            long hpAfterBattle = prop.getMonsterMap().values().stream().mapToLong(o -> o.getOrganismDTO()
                    .getHpWithAddition()).sum();
            damage = hpBeforeBattle - hpAfterBattle;
            worldBossRecord.setDamage(damage);
        }
        WorldBossRecordService.insert(worldBossRecord);
        return damage;
    }

    /**
     * 查询我的排名
     *
     * @return 当前排名
     */
    public List<WorldBossChartVO> queryPlayerCharts(Long playerId) {
        List<WorldBossChartVO> voList = new ArrayList<>();
        List<WorldBossRecord> worldBossRecordList = WorldBossRecordService.queryTodayWorldBossRecordList();
        if (CollectionUtils.isEmpty(worldBossRecordList)) {
            return voList;
        }
        for (int i = 0; i < worldBossRecordList.size(); i++) {
            WorldBossRecord record = worldBossRecordList.get(i);
            if (record.getPlayerIdList().contains(playerId.toString())) {
                List<String> memberNameList = Arrays.stream(record.getPlayerIdList().split("\\|"))
                        .map(id -> PlayerCache.getPlayerById(Long.parseLong(id)).getPlayerDTO().getName())
                        .collect(Collectors.toList());
                WorldBossChartVO vo = WorldBossChartVO.builder()
                        .rank(i + 1)
                        .memberNames(memberNameList)
                        .damage(record.getDamage())
                        .build();
                voList.add(vo);
            }
        }
        return voList;
    }

    /**
     * 查询排行榜
     *
     * @return
     */
    public List<WorldBossChartVO> queryWorldBossCharts(int pageIndex, int pageSize) {
        List<WorldBossChartVO> voList = new ArrayList<>();
        List<WorldBossRecord> worldBossRecordList = WorldBossRecordService.queryTodayWorldBossRecordList();
        if (CollectionUtils.isEmpty(worldBossRecordList)) {
            return voList;
        }

        Page<WorldBossRecord> page = WorldBossRecordService.queryTodayWorldBossRecordPage(pageIndex, pageSize);
        List<WorldBossRecord> recordList = page.getRecords();
        for (int i = 0; i < recordList.size(); i++) {
            WorldBossRecord record = worldBossRecordList.get(i);
            List<String> memberNameList = Arrays.stream(record.getPlayerIdList().split("\\|"))
                    .map(id -> PlayerCache.getPlayerById(Long.parseLong(id)).getPlayerDTO().getName())
                    .collect(Collectors.toList());
            WorldBossChartVO vo = WorldBossChartVO.builder()
                    .rank((pageIndex - 1) * pageSize + i + 1)
                    .memberNames(memberNameList)
                    .damage(record.getDamage())
                    .build();
            voList.add(vo);
        }
        return voList;
    }

    private static class Holder {
        private static final WorldBossBehavior BEHAVIOR = new WorldBossBehavior();
    }

    private WorldBossBehavior() {

    }

    public static WorldBossBehavior getInstance() {
        return WorldBossBehavior.Holder.BEHAVIOR;
    }
}
