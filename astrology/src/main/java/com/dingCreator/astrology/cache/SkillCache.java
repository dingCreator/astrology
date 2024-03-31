package com.dingCreator.astrology.cache;

import cn.hutool.core.collection.CollectionUtil;
import com.dingCreator.astrology.dto.skill.SkillBarDTO;
import com.dingCreator.astrology.entity.SkillBarItem;
import com.dingCreator.astrology.enums.OrganismEnum;
import com.dingCreator.astrology.service.SkillBarItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author ding
 * @date 2024/2/4
 */
public class SkillCache {

    static Logger logger = LoggerFactory.getLogger(SkillCache.class);

    /**
     * 技能栏缓存
     * 格式
     * {
     *      "player": {
     *          1: SkillBarDTO
     *      }
     * }
     */
    private static final Map<String, Map<Long, SkillBarDTO>> SKILL_CACHE = new HashMap<>(64);

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
        SkillBarItem skillBarItem = SkillBarItemService.getSkillBarItemByBelongToId(belongTo, belongToId);
        if (Objects.isNull(skillBarItem)) {
            logger.error("invalid belong to [{}] and belong to id [{}]", belongTo, belongToId);
            throw new IllegalArgumentException("invalid belong to and belong to id");
        }
        // 技能栏信息
        String skillIdStr = skillBarItem.getSkillId();
        List<Long> skillIdList = Arrays.stream(skillIdStr.split(",")).map(Long::parseLong).collect(Collectors.toList());
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

    public static void deleteSkillBar(Long playerId) {
        Map<Long, SkillBarDTO> skillBarMap = SKILL_CACHE.get(OrganismEnum.PLAYER.getType());
        if (Objects.nonNull(skillBarMap)) {
            skillBarMap.remove(playerId);
        }
    }
}
