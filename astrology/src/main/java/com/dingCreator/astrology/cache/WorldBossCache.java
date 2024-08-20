package com.dingCreator.astrology.cache;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.database.DatabaseProvider;
import com.dingCreator.astrology.dto.organism.OrganismInfoDTO;
import com.dingCreator.astrology.dto.organism.monster.MonsterDTO;
import com.dingCreator.astrology.entity.WorldBoss;
import com.dingCreator.astrology.entity.WorldBossRecord;
import com.dingCreator.astrology.entity.base.Monster;
import com.dingCreator.astrology.enums.BelongToEnum;
import com.dingCreator.astrology.enums.exception.WorldBossExceptionEnum;
import com.dingCreator.astrology.enums.skill.SkillEnum;
import com.dingCreator.astrology.mapper.WorldBossRecordMapper;
import com.dingCreator.astrology.service.MonsterService;
import com.dingCreator.astrology.service.WorldBossService;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author ding
 * @date 2024/8/7
 */
public class WorldBossCache {

    /**
     * 世界BOSS缓存
     */
    private static final Map<LocalDate, BossPropDTO> WORLD_BOSS_MAP = new HashMap<>();

    /**
     * 攻击次数缓存
     */
    private static final Map<LocalDate, Map<Long, Integer>> ATK_TIMES_MAP = new ConcurrentHashMap<>();

    /**
     * 待提交攻击次数
     */
    private static Map<Long, Integer> preAtkTimesMap = new HashMap<>();

    /**
     * 攻击次数缓存是否已初始化
     */
    private static volatile boolean atkTimesInitFlag = false;

    /**
     * 获取boss属性
     *
     * @return BOSS属性
     */
    public static BossPropDTO getBossProp() {
        if (!WORLD_BOSS_MAP.containsKey(LocalDate.now())) {
            initBoss();
        }
        return WORLD_BOSS_MAP.get(LocalDate.now());
    }

    /**
     * 获取BOSS是否已被击败
     *
     * @return 是/否
     */
    public static boolean getDefeated() {
        if (!WORLD_BOSS_MAP.containsKey(LocalDate.now())) {
            return true;
        }
        return WORLD_BOSS_MAP.get(LocalDate.now()).getMonsterMap().values().stream().allMatch(o ->
                o.getOrganismDTO().getHpWithAddition() <= 0);
    }

    /**
     * 初始化世界boss
     */
    private static synchronized void initBoss() {
        if (!WORLD_BOSS_MAP.containsKey(LocalDate.now())) {
            WorldBoss todayBoss = WorldBossService.getTodayBoss();
            List<Long> bossIds = Arrays.stream(todayBoss.getMonsterId().split(",")).map(Long::parseLong)
                    .collect(Collectors.toList());
            List<Monster> monsterList = MonsterService.getMonsterByIds(bossIds);
            if (CollectionUtil.isEmpty(monsterList)) {
                return;
            }

            BossPropDTO vo = new BossPropDTO();
            vo.setStartTime(todayBoss.getStartTime());
            vo.setEndTime(todayBoss.getEndTime());
            vo.setMonsterMap(monsterList.stream().collect(Collectors.toMap(
                    Monster::getId,
                    monster -> {
                        MonsterDTO monsterDTO = new MonsterDTO();
                        monsterDTO.copyProperties(monster);

                        OrganismInfoDTO organismInfoDTO = new OrganismInfoDTO();
                        organismInfoDTO.setOrganismDTO(monsterDTO);
                        organismInfoDTO.setSkillBarDTO(SkillCache.getSkillBarItem(BelongToEnum.Monster.getBelongTo(), monster.getId()));
                        organismInfoDTO.setInactiveSkills(SkillCache.getInactiveSkill(BelongToEnum.Monster.getBelongTo(), monster.getId())
                                .stream().map(SkillEnum::getById).collect(Collectors.toList()));
                        return organismInfoDTO;
                    },
                    (m1, m2) -> m2)));
            WORLD_BOSS_MAP.put(LocalDate.now(), vo);
        }
    }

    @Data
    public static class BossPropDTO {
        /**
         * 开始时间
         */
        private LocalDateTime startTime;
        /**
         * 结束时间
         */
        private LocalDateTime endTime;
        /**
         * BOSS信息
         */
        private Map<Long, OrganismInfoDTO> monsterMap;
    }

    /**
     * 获取攻击次数
     *
     * @param playerId 玩家id
     * @return 攻击次数
     */
    public static int getAtkTimes(Long playerId) {
        if (!atkTimesInitFlag) {
            initAtkTimes();
        }
        return ATK_TIMES_MAP.getOrDefault(LocalDate.now(), new HashMap<>()).getOrDefault(playerId, 0);
    }

    public static synchronized void increaseAtkTimes(List<Long> playerIdList) {
        playerIdList.forEach(playerId -> {
            if (!WORLD_BOSS_MAP.containsKey(LocalDate.now())) {
                throw WorldBossExceptionEnum.NOT_EXIST.getException();
            }
            int atkTimes = getAtkTimes(playerId);
            if (atkTimes >= Constants.WORLD_BOSS_MAX_ATK_TIMES) {
                throw WorldBossExceptionEnum.ATK_TIMES_OVER_LIMIT.getException();
            }
            preAtkTimesMap = new HashMap<>(ATK_TIMES_MAP.getOrDefault(LocalDate.now(), new HashMap<>()));
            preAtkTimesMap.put(playerId, preAtkTimesMap.getOrDefault(playerId, 0) + 1);
        });
    }

    public static synchronized void commitAtkTimes() {
        ATK_TIMES_MAP.put(LocalDate.now(), preAtkTimesMap);
    }

    private static synchronized void initAtkTimes() {
        if (!atkTimesInitFlag) {
            // 只初始化当天的记录即可
            BossPropDTO bossPropDTO = WORLD_BOSS_MAP.get(LocalDate.now());
            if (Objects.isNull(bossPropDTO)) {
                // 当天没有世界boss
                atkTimesInitFlag = true;
                return;
            }

            List<WorldBossRecord> recordList = DatabaseProvider.getInstance().executeReturn(sqlSession -> {
                QueryWrapper<WorldBossRecord> wrapper = new QueryWrapper<>();
                wrapper.ge(WorldBossRecord.CREATE_TIME, LocalDate.now().atStartOfDay());
                wrapper.lt(WorldBossRecord.CREATE_TIME, LocalDate.now().plusDays(1).atStartOfDay());
                return sqlSession.getMapper(WorldBossRecordMapper.class).selectList(wrapper);
            });

            ATK_TIMES_MAP.putAll(recordList.stream().collect(Collectors.toMap(
                    record -> record.getCreateTime().toLocalDate(),
                    record -> Arrays.stream(record.getPlayerIdList().split("\\|"))
                            .collect(Collectors.toMap(Long::parseLong, id -> 1)),
                    (m1, m2) -> {
                        m1.keySet().forEach(key -> {
                            if (m2.containsKey(key)) {
                                m1.put(key, m1.get(key) + m2.get(key));
                            }
                        });
                        return m1;
                    }
            )));
        }
    }
}
