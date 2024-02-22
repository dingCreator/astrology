package com.dingCreator.astrology.behavior;

import com.dingCreator.astrology.cache.SkillCache;
import com.dingCreator.astrology.dto.SkillBarDTO;

/**
 * @author ding
 * @date 2024/2/21
 */
public class SkillBehavior {

    /**
     * 获取技能循环链
     *
     * @param belongTo 技能归属
     * @param belongToId 技能归属ID
     * @return 技能循环链表头
     */
    public SkillBarDTO getSkillBarDTO(String belongTo, Long belongToId) {
        return SkillCache.getSkillBarItem(belongTo, belongToId);
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
