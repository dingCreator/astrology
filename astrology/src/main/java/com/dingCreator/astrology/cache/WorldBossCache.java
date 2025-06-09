package com.dingCreator.astrology.cache;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dingCreator.astrology.behavior.WorldBossBehavior;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.database.DatabaseProvider;
import com.dingCreator.astrology.dto.WorldBossAwardDTO;
import com.dingCreator.astrology.dto.article.ArticleItemDTO;
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
    private static volatile BossPropDTO worldBoss;

    /**
     * 攻击次数缓存
     */
    private static final Map<LocalDate, Map<Long, Integer>> ATK_TIMES_MAP = new ConcurrentHashMap<>();

    /**
     * 世界boss是否已初始化
     */
    private static volatile boolean worldBossInitFlag = false;

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
        initBoss();
        return worldBoss;
    }

    /**
     * 获取BOSS是否已被击败
     *
     * @return 是/否
     */
    public static boolean getDefeated() {
        if (Objects.isNull(worldBoss)) {
            return true;
        }
        return worldBoss.getMonsterMap().values().stream().allMatch(o ->
                o.getOrganismDTO().getHpWithAddition() <= 0);
    }

    /**
     * 初始化世界boss
     */
    private static synchronized void initBoss() {
        // 日期更新后需要重新初始化boss
        if (!worldBossInitFlag || !bossInTime()) {
            WorldBoss nowBoss = worldBossService.getNowBoss();
            if (Objects.isNull(nowBoss)) {
                return;
            }

            List<Long> bossIds = Arrays.stream(nowBoss.getMonsterId().split(Constants.COMMA)).map(Long::parseLong)
                    .collect(Collectors.toList());
            List<Monster> monsterList = monsterService.getMonsterByIds(bossIds, false);
            if (CollectionUtil.isEmpty(monsterList)) {
                return;
            }

            BossPropDTO prop = new BossPropDTO();
            prop.setWorldBossId(nowBoss.getId());
            prop.setStartTime(nowBoss.getStartTime());
            prop.setEndTime(nowBoss.getEndTime());
            prop.setMonsterMap(monsterList.stream().collect(Collectors.toMap(
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
            prop.setAwardList(WorldBossBehavior.getInstance().convertAwardList(nowBoss.getAward()));
            worldBoss = prop;
            // 更新初始化标记
            worldBossInitFlag = true;
        }
    }

    @Data
    public static class BossPropDTO {
        /**
         * 世界boss配置ID
         */
        private Long worldBossId;
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
        /**
         * 奖励
         */
        private List<WorldBossAwardDTO> awardList;
    }

    /**
     * 清除所有世界boss缓存
     */
    public static void clearCache() {
        worldBoss = null;
        ATK_TIMES_MAP.clear();
        worldBossInitFlag = false;
        atkTimesInitFlag = false;
    }

    private static boolean bossInTime() {
        return Objects.nonNull(worldBoss) && (
                LocalDateTime.now().isBefore(worldBoss.getStartTime())
                        || (LocalDateTime.now().isAfter(worldBoss.getEndTime()))
        );
    }
}
