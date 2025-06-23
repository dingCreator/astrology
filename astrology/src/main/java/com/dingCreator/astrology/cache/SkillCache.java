package com.dingCreator.astrology.cache;

import cn.hutool.core.collection.CollectionUtil;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.dto.skill.SkillBarDTO;
import com.dingCreator.astrology.entity.SkillBarItem;
import com.dingCreator.astrology.entity.SkillBelongTo;
import com.dingCreator.astrology.enums.BelongToEnum;
import com.dingCreator.astrology.enums.skill.SkillEnum;
import com.dingCreator.astrology.service.SkillBarItemService;
import com.dingCreator.astrology.service.SkillBelongToService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ding
 * @date 2024/2/4
 */
public class SkillCache {

    private static final SkillBarItemService skillBarItemService = SkillBarItemService.getInstance();

    private static final SkillBelongToService skillBelongToService = SkillBelongToService.getInstance();

    static Logger logger = LoggerFactory.getLogger(SkillCache.class);

    /**
     * 技能栏缓存
     * 格式
     * {
     * "player": {
     * 1: SkillBarDTO
     * }
     * }
     */
    private static final Map<String, Map<Long, SkillBarDTO>> SKILL_CACHE = new HashMap<>(64);

    /**
     * 被动技能缓存
     * 格式
     * {
     * "player": {
     * 1: [1,2,3]
     * }
     * }
     */
    private static final Map<String, Map<Long, List<Long>>> INACTIVE_SKILL_CACHE = new HashMap<>(64);

    /**
     * 获取技能栏
     *
     * @param belongTo   技能归属
     * @param belongToId 技能归属ID
     * @return 技能栏
     */
    public static SkillBarDTO getSkillBarItem(String belongTo, Long belongToId) {
        SkillBarDTO skillBarDTO;
        Map<Long, SkillBarDTO> belongToMap = SKILL_CACHE.getOrDefault(belongTo, new HashMap<>());
        if (CollectionUtil.isEmpty(belongToMap) || !belongToMap.containsKey(belongToId)) {
            skillBarDTO = initSkillBar(belongTo, belongToId);
            belongToMap.put(belongToId, skillBarDTO);
            SKILL_CACHE.put(belongTo, belongToMap);
        } else {
            skillBarDTO = SKILL_CACHE.get(belongTo).get(belongToId);
        }
        return skillBarDTO;
    }

    /**
     * 初始化技能栏
     *
     * @param belongTo   player/boss
     * @param belongToId 对应的Id
     */
    public static SkillBarDTO initSkillBar(String belongTo, Long belongToId) {
        SkillBarItem skillBarItem = skillBarItemService.getSkillBarItemByBelongToId(belongTo, belongToId);
        if (Objects.isNull(skillBarItem)) {
            logger.error("invalid belong to [{}] and belong to id [{}]", belongTo, belongToId);
            throw new IllegalArgumentException("invalid belong to and belong to id");
        }
        // 技能栏信息
        String skillIdStr = skillBarItem.getSkillId();
        List<Long> skillIdList = Arrays.stream(skillIdStr.split(Constants.COMMA)).map(Long::parseLong).collect(Collectors.toList());
        // 构建头
        SkillBarDTO head = new SkillBarDTO();
        head.setHead(head);
        head.setSkillId(skillIdList.remove(0));
        // 构建技能栏循环链
        SkillBarDTO index = head;
        for (Long skillId : skillIdList) {
            SkillBarDTO skillBarDTO = new SkillBarDTO();
            skillBarDTO.setHead(head);
            skillBarDTO.setSkillId(skillId);
            index.setNext(skillBarDTO);
            index = skillBarDTO;
        }
        return head;
    }

    /**
     * 删除技能栏缓存
     *
     * @param playerId 玩家ID
     */
    public static void deleteSkillBar(Long playerId) {
        deleteSkillBar(BelongToEnum.PLAYER.getBelongTo(), playerId);
    }

    /**
     * 删除技能栏缓存
     *
     * @param belongTo   归属
     * @param belongToId 归属ID
     */
    public static void deleteSkillBar(String belongTo, Long belongToId) {
        Map<Long, SkillBarDTO> skillBarMap = SKILL_CACHE.get(belongTo);
        if (Objects.nonNull(skillBarMap)) {
            skillBarMap.remove(belongToId);
        }
    }

    /**
     * 获取被动技能
     *
     * @param belongTo   技能归属
     * @param belongToId 技能归属ID
     * @return 被动技能
     */
    public static List<Long> getInactiveSkill(String belongTo, Long belongToId) {
        List<Long> inactiveSkillIds;
        Map<Long, List<Long>> belongToMap = INACTIVE_SKILL_CACHE.getOrDefault(belongTo, new HashMap<>());
        if (CollectionUtil.isEmpty(belongToMap) || !belongToMap.containsKey(belongToId)) {
            inactiveSkillIds = initInactiveSkill(belongTo, belongToId);
            belongToMap.put(belongToId, inactiveSkillIds);
            INACTIVE_SKILL_CACHE.put(belongTo, belongToMap);
        } else {
            inactiveSkillIds = INACTIVE_SKILL_CACHE.get(belongTo).get(belongToId);
        }
        return inactiveSkillIds;
    }

    /**
     * 初始化技能栏
     *
     * @param belongTo   player/boss
     * @param belongToId 对应的Id
     */
    public static List<Long> initInactiveSkill(String belongTo, Long belongToId) {
        List<SkillBelongTo> belongToList = skillBelongToService.querySkillBelongToList(belongTo, belongToId);
        if (Objects.isNull(belongToList) || belongToList.isEmpty()) {
            return new ArrayList<>();
        }
        return belongToList.stream().map(SkillBelongTo::getSkillId).map(SkillEnum::getById)
                .filter(skillEnum -> !skillEnum.getActive()).map(SkillEnum::getId).collect(Collectors.toList());
    }

    /**
     * 删除技能栏缓存
     *
     * @param playerId 玩家ID
     */
    public static void deleteInactiveSkillCache(Long playerId) {
        deleteInactiveSkillCache(BelongToEnum.PLAYER.getBelongTo(), playerId);
    }

    /**
     * 删除技能栏缓存
     *
     * @param belongTo   技能归属
     * @param belongToId 技能归属ID
     */
    public static void deleteInactiveSkillCache(String belongTo, Long belongToId) {
        Map<Long, List<Long>> skillBarMap = INACTIVE_SKILL_CACHE.get(belongTo);
        if (Objects.nonNull(skillBarMap)) {
            skillBarMap.remove(belongToId);
        }
    }

}
