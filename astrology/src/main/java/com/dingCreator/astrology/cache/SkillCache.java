package com.dingCreator.astrology.cache;

import com.dingCreator.astrology.dto.SkillBarDTO;
import com.dingCreator.astrology.entity.SkillBarItem;
import com.dingCreator.astrology.enums.OrganismEnum;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ding
 * @date 2024/2/4
 */
public class SkillCache {

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
     * 初始化技能栏
     *
     * @param ids  对应的Id
     * @param type player/boss
     */
    public void initSkillBar(List<Long> ids, String type) {
        List<SkillBarItem> skillBarItem;
    }

    public void deleteSkillBar(Long id) {
        SKILL_CACHE.get(OrganismEnum.PLAYER.getType()).remove(id);
    }
}
