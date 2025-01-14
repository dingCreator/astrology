package com.dingCreator.astrology.behavior;

import cn.hutool.core.collection.CollectionUtil;
import com.dingCreator.astrology.cache.SkillCache;
import com.dingCreator.astrology.cache.WorldBossCache;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.dto.skill.SkillBarDTO;
import com.dingCreator.astrology.entity.SkillBarItem;
import com.dingCreator.astrology.entity.base.Monster;
import com.dingCreator.astrology.enums.BelongToEnum;
import com.dingCreator.astrology.enums.OrganismPropertiesEnum;
import com.dingCreator.astrology.enums.exception.MonsterManageExceptionEnum;
import com.dingCreator.astrology.enums.job.JobEnum;
import com.dingCreator.astrology.enums.skill.SkillEnum;
import com.dingCreator.astrology.exception.BusinessException;
import com.dingCreator.astrology.response.PageResponse;
import com.dingCreator.astrology.service.MonsterService;
import com.dingCreator.astrology.service.RankUpBossService;
import com.dingCreator.astrology.service.SkillBarItemService;
import com.dingCreator.astrology.service.WorldBossService;
import com.dingCreator.astrology.util.BattleUtil;
import com.dingCreator.astrology.util.DateUtil;
import com.dingCreator.astrology.util.PageUtil;
import com.dingCreator.astrology.vo.BattleResultVO;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ding
 * @date 2024/4/15
 */
public class MonsterManageBehavior {

    private final MonsterService monsterService = MonsterService.getInstance();

    private final WorldBossService worldBossService = WorldBossService.getInstance();

    private final RankUpBossService rankUpBossService = RankUpBossService.getInstance();

    private final SkillBarItemService skillBarItemService = SkillBarItemService.getInstance();

    /**
     * 查询怪物信息
     *
     * @param pageIndex 页码
     * @return 怪物列表
     */
    public PageResponse<Monster> pageMonster(int pageIndex, int pageSize) {
        if (pageIndex <= 0) {
            throw MonsterManageExceptionEnum.LIST_MONSTER_PAGE_ERR.getException();
        }
        return PageUtil.addPageDesc(monsterService.listMonster(pageIndex, pageSize), pageIndex, pageSize, countAllBoss());
    }

    /**
     * 获取boss数量
     *
     * @return 数量
     */
    public int countAllBoss() {
        return monsterService.count();
    }

    /**
     * 查询怪物详细信息
     *
     * @param id 怪物id
     * @return 怪物详细信息
     */
    public Monster getMonsterById(Long id) {
        return monsterService.getMonsterById(id);
    }

    /**
     * 查询怪物详细信息
     *
     * @param name 怪物名
     * @return 怪物详细信息
     */
    public List<Monster> getMonsterByName(String name) {
        return monsterService.listMonsterByName(name);
    }

    /**
     * 新增怪物
     *
     * @param name 名称
     */
    public long createMonster(String name) {
        Monster monster = Monster.buildMonsterWithTemplate(name);
        setMonsterProp(new ArrayList<>(), monster);
        return monsterService.createMonster(monster);
    }

    /**
     * 新增怪物
     *
     * @param name 名称
     */
    public long createMonster(String name, List<String> propertyTypeValList) {
        Monster monster = Monster.buildMonsterWithTemplate(name);
        setMonsterProp(propertyTypeValList, monster);
        return monsterService.createMonster(monster);
    }

    /**
     * 修改怪物属性
     *
     * @param monsterId           怪物id
     * @param propertyTypeValList 属性类型和值
     * @return 错误信息
     */
    public List<String> updateMonster(Long monsterId, List<String> propertyTypeValList) {
        if (CollectionUtil.isEmpty(propertyTypeValList)) {
            return new ArrayList<>();
        }
        if (Objects.isNull(getMonsterById(monsterId))) {
            throw MonsterManageExceptionEnum.MONSTER_NOT_FOUND.getException();
        }
        Monster monster = new Monster();
        monster.setId(monsterId);

        List<String> exceptionList = setMonsterProp(propertyTypeValList, monster);
        if (exceptionList.size() < propertyTypeValList.size()) {
            monsterService.updateMonster(monster);
        }
        return exceptionList;
    }

    private List<String> setMonsterProp(List<String> propertyTypeValList, Monster monster) {
        List<BusinessException> exceptionList = propertyTypeValList.stream()
                .map(propertyTypeVal -> propertyTypeVal = propertyTypeVal.replace("：", Constants.COLON))
                .map(propertyTypeVal -> {
                    if (!propertyTypeVal.contains(Constants.COLON)) {
                        return MonsterManageExceptionEnum.MONSTER_PROP_FORMAT_ERR.getException();
                    }

                    String[] property = propertyTypeVal.split(Constants.COLON);
                    String propertyType = property[0];
                    String propertyVal = property[1];

                    OrganismPropertiesEnum prop = OrganismPropertiesEnum.getByChnDesc(propertyType);
                    if (Objects.isNull(prop)) {
                        switch (propertyType) {
                            case "名称":
                                monster.setName(propertyVal);
                                break;
                            case "描述":
                                monster.setDescription(propertyVal);
                                break;
                            default:
                                return MonsterManageExceptionEnum.MONSTER_PROP_NOT_FOUND.getException();
                        }
                    } else {
                        try {
                            prop.getDoSetMethod().accept(monster, propertyVal);
                        } catch (NumberFormatException formatException) {
                            return MonsterManageExceptionEnum.MONSTER_PROP_VAL_ERR.getException();
                        }
                    }
                    return null;
                }).collect(Collectors.toList());
        List<String> errorMsgList = new ArrayList<>();
        for (int i = 0; i < exceptionList.size(); i++) {
            if (Objects.nonNull(exceptionList.get(i))) {
                errorMsgList.add((i + 1) + Constants.DOT + exceptionList.get(i).getDesc());
            }
        }
        return errorMsgList;
    }

    /**
     * 查询boss属性中文名
     *
     * @return 属性中文名
     */
    public List<String> queryMonsterPropChnDesc() {
        List<String> prop = Arrays.stream(OrganismPropertiesEnum.values()).map(OrganismPropertiesEnum::getChnDesc)
                .collect(Collectors.toList());
        prop.add("名称");
        prop.add("描述");
        return prop;
    }

    /**
     * 获取怪物主动技能
     *
     * @param monsterId 怪物ID
     * @return 怪物主动技能
     */
    public SkillBarDTO getMonsterActiveSkill(Long monsterId) {
        if (Objects.isNull(getMonsterById(monsterId))) {
            throw MonsterManageExceptionEnum.MONSTER_NOT_FOUND.getException();
        }
        return SkillCache.getSkillBarItem(BelongToEnum.MONSTER.getBelongTo(), monsterId);
    }

    /**
     * 设置怪物主动技能
     *
     * @param monsterId 怪物ID
     * @param skillIds  技能ID
     */
    public void setMonsterActiveSkill(Long monsterId, List<Long> skillIds) {
        if (Objects.isNull(getMonsterById(monsterId))) {
            throw MonsterManageExceptionEnum.MONSTER_NOT_FOUND.getException();
        }
        String validSkillIdStr = skillIds.stream().filter(id -> Objects.nonNull(SkillEnum.getById(id)))
                .map(Object::toString)
                .reduce((s1, s2) -> s1 + Constants.COMMA + s2)
                .orElseThrow(MonsterManageExceptionEnum.MONSTER_SKILL_ID_NOT_FOUND::getException);

        skillBarItemService.deleteSkillBarItem(BelongToEnum.MONSTER.getBelongTo(), monsterId);
        SkillBarItem skillBarItem = new SkillBarItem();
        skillBarItem.setBelongTo(BelongToEnum.MONSTER.getBelongTo());
        skillBarItem.setBelongToId(monsterId);
        skillBarItem.setSkillId(validSkillIdStr);
        skillBarItemService.deleteSkillBarItem(BelongToEnum.MONSTER.getBelongTo(), monsterId);
        skillBarItemService.addSkillBarItem(skillBarItem);
        SkillCache.deleteSkillBar(BelongToEnum.MONSTER.getBelongTo(), monsterId);
    }

    public void setMonsterInactiveSkill(Long monsterId, List<Long> skillIds) {

    }


    /**
     * 创建或者更新世界boss
     *
     * @param appearDateStr 出现日期
     * @param startHour     开始时间
     * @param endHour       结束时间
     * @param monsterIds    怪物ID
     */
    public void insertOrUpdateWorldBoss(String appearDateStr, int startHour, int endHour, List<Long> monsterIds) {
        boolean invalidStartHour = startHour < Constants.MIN_HOUR || startHour > Constants.MAX_HOUR + 1;
        boolean invalidEndHour = endHour < Constants.MIN_HOUR || startHour > Constants.MAX_HOUR + 1;
        boolean invalidHourRelation = startHour >= endHour;
        if (invalidStartHour || invalidEndHour || invalidHourRelation) {
            throw MonsterManageExceptionEnum.WORLD_BOSS_TIME_INVALID.getException();
        }
        LocalDate appearDate = DateUtil.parseDate(appearDateStr);
        if (Objects.isNull(appearDate)) {
            throw MonsterManageExceptionEnum.WORLD_BOSS_TIME_INVALID.getException();
        }
        // 过滤非法boss ID
        List<Long> validMonsterIds = monsterService.getMonsterByIds(monsterIds).stream().map(Monster::getId)
                .collect(Collectors.toList());
        String monsterId = validMonsterIds.stream().map(Objects::toString).reduce((id1, id2) -> id1 + Constants.COMMA + id2)
                .orElseThrow(MonsterManageExceptionEnum.WORLD_BOSS_ID_INVALID::getException);
        worldBossService.insertOrUpdateWorldBoss(appearDate, startHour, endHour, monsterId);
        WorldBossCache.clearCache();
    }

    /**
     * 创建或者更新突破boss
     *
     * @param job        职业
     * @param rank       阶级
     * @param monsterIds 怪物ID
     */
    public void insertOrUpdateRankUpBoss(String job, int rank, List<Long> monsterIds) {
        JobEnum jobEnum = JobEnum.getByName(job);
        if (Objects.isNull(jobEnum)) {
            throw MonsterManageExceptionEnum.RANK_UP_BOSS_JOB_INVALID.getException();
        }
        if (rank < 0 || rank > Constants.MAX_RANK) {
            throw MonsterManageExceptionEnum.RANK_UP_BOSS_RANK_INVALID.getException();
        }
        // 过滤非法boss ID
        List<Long> validMonsterIds = monsterService.getMonsterByIds(monsterIds).stream().map(Monster::getId)
                .collect(Collectors.toList());
        if (CollectionUtil.isEmpty(validMonsterIds)) {
            throw MonsterManageExceptionEnum.WORLD_BOSS_ID_INVALID.getException();
        }
        rankUpBossService.insertOrUpdateRankUpBoss(jobEnum.getJobCode(), rank, validMonsterIds);
    }

    public BattleResultVO EVE(Long playerId, Long monsterId1, Long monsterId2) {
        return BattleUtil.battleEVE(playerId, Collections.singletonList(monsterId1), Collections.singletonList(monsterId2));
    }

    private static class Holder {
        private static final MonsterManageBehavior BEHAVIOR = new MonsterManageBehavior();
    }

    private MonsterManageBehavior() {

    }

    public static MonsterManageBehavior getInstance() {
        return MonsterManageBehavior.Holder.BEHAVIOR;
    }
}
