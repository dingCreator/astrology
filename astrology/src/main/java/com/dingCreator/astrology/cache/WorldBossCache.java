package com.dingCreator.astrology.cache;

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

    private static final MonsterService monsterService = MonsterService.getInstance();

    private static final WorldBossService worldBossService = WorldBossService.getInstance();

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
     * 世界boss是否已初始化
     */
    private static volatile boolean worldBossInitFlag = false;

    /**
     * 攻击次数缓存是否已初始化
     */
    private static volatile boolean atkTimesInitFlag = false;
    /**
     * 初始化boss的时间
     */
    private static LocalDate lastInitDate;

    /**
     * 获取boss属性
     *
     * @return BOSS属性
     */
    public static BossPropDTO getBossProp() {
        initBoss();
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
        // 日期更新后需要重新初始化boss
        if (!worldBossInitFlag || !LocalDate.now().isEqual(lastInitDate)) {
            WorldBoss todayBoss = worldBossService.getTodayBoss();
            if (Objects.isNull(todayBoss)) {
                return;
            }

            List<Long> bossIds = Arrays.stream(todayBoss.getMonsterId().split(Constants.COMMA)).map(Long::parseLong)
                    .collect(Collectors.toList());
            List<Monster> monsterList = monsterService.getMonsterByIds(bossIds, false);
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
                        organismInfoDTO.setSkillBarDTO(SkillCache.getSkillBarItem(BelongToEnum.MONSTER.getBelongTo(), monster.getId()));
                        organismInfoDTO.setInactiveSkills(SkillCache.getInactiveSkill(BelongToEnum.MONSTER.getBelongTo(), monster.getId())
                                .stream().map(SkillEnum::getById).collect(Collectors.toList()));
                        return organismInfoDTO;
                    },
                    (m1, m2) -> m2)));
            WORLD_BOSS_MAP.put(LocalDate.now(), vo);
            // 更新初始化标记
            worldBossInitFlag = true;
            lastInitDate = LocalDate.now();
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
        initAtkTimes();
        return ATK_TIMES_MAP.getOrDefault(LocalDate.now(), new HashMap<>()).getOrDefault(playerId, 0);
    }

    /**
     * 增加攻击次数
     *
     * @param playerIdList 玩家id
     */
    public static synchronized void increaseAtkTimes(List<Long> playerIdList) {
        initBoss();
        if (Objects.isNull(WORLD_BOSS_MAP.get(LocalDate.now()))) {
            throw WorldBossExceptionEnum.NOT_EXIST.getException();
        }
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

    /**
     * 提交攻击次数增加后的结果
     */
    public static synchronized void commitAtkTimes() {
        ATK_TIMES_MAP.put(LocalDate.now(), preAtkTimesMap);
    }

    /**
     * 初始化攻击次数
     */
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
            atkTimesInitFlag = true;
        }
    }

    /**
     * 清除所有世界boss缓存
     */
    public static void clearCache() {
        WORLD_BOSS_MAP.clear();
        ATK_TIMES_MAP.clear();
        preAtkTimesMap.clear();
        worldBossInitFlag = false;
        atkTimesInitFlag = false;
        lastInitDate = null;
    }
}
