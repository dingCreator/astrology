package com.dingCreator.astrology.behavior;

import com.dingCreator.astrology.cache.SkillCache;
import com.dingCreator.astrology.dto.skill.SkillBarDTO;
import com.dingCreator.astrology.service.SkillBelongToService;

/**
 * @author ding
 * @date 2024/2/21
 */
public class SkillBehavior {

    /**
     * 获取技能循环链
     *
     * @param belongTo   技能归属
     * @param belongToId 技能归属ID
     * @return 技能循环链表头
     */
    public SkillBarDTO getSkillBarDTO(String belongTo, Long belongToId) {
        return SkillCache.getSkillBarItem(belongTo, belongToId);
    }

    /**
     * 新增技能归属记录
     *
     * @param belongTo   技能归属
     * @param belongToId 技能归属ID
     * @param skillId    技能ID
     */
    public void createSkillBelongTo(String belongTo, Long belongToId, Long skillId) {
        SkillBelongToService.createSkillBelongTo(belongTo, belongToId, skillId);
    }


    private static class Holder {
        private static final SkillBehavior BEHAVIOR = new SkillBehavior();
    }

    private SkillBehavior() {

    }

    public static SkillBehavior getInstance() {
        return SkillBehavior.Holder.BEHAVIOR;
    }
}
