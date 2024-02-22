package com.dingCreator.astrology.cache;

import cn.hutool.core.collection.CollectionUtil;
import com.dingCreator.astrology.dto.SkillBarDTO;
import com.dingCreator.astrology.entity.SkillBarItem;
import com.dingCreator.astrology.enums.OrganismEnum;
import com.dingCreator.astrology.service.SkillBarItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
     *  "player": {
     *      1: SkillBarDTO
     *  }
     * }
     */
    private static final Map<String, Map<Long, SkillBarDTO>> SKILL_CACHE = new HashMap<>(64);

    /**
     * 获取技能栏
     *
     * @param belongTo 技能归属
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
     * @param belongTo player/boss
     * @param belongToId  对应的Id
     */
    public static SkillBarDTO initSkillBar(String belongTo, Long belongToId) {
        List<SkillBarItem> skillBarItem = SkillBarItemService.getSkillBarItemByBelongToId(belongTo, belongToId);
        if (Objects.isNull(skillBarItem) || skillBarItem.size() == 0) {
            logger.error("invalid belong to [{}] and belong to id [{}]", belongTo, belongToId);
            throw new IllegalArgumentException("invalid belong to and belong to id");
        }

        SkillBarDTO skillBarDTO = new SkillBarDTO();
        skillBarDTO.setId(skillBarDTO.getId());
        skillBarDTO.setBelongTo(belongTo);
        skillBarDTO.setBelongToId(belongToId);

        return skillBarDTO;
    }

    public static void deleteSkillBar(Long id) {
        SKILL_CACHE.get(OrganismEnum.PLAYER.getType()).remove(id);
    }
}
