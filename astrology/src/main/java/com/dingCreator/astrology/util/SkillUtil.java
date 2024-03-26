package com.dingCreator.astrology.util;

import com.dingCreator.astrology.entity.SkillBarItem;
import com.dingCreator.astrology.enums.BelongToEnum;
import com.dingCreator.astrology.enums.skill.SkillEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author ding
 * @date 2024/3/26
 */
public class SkillUtil {

    /**
     * 构建技能循环链
     *
     * @param skillIdList 技能id列表
     * @return 技能循环链
     */
    public static List<SkillBarItem> buildSkillBarItemChain(List<Long> skillIdList, BelongToEnum belongToEnum,
                                                            Long belongToId) {
        String headId = UUID.randomUUID().toString();
        SkillBarItem head = new SkillBarItem(headId, belongToEnum.getBelongTo(), skillIdList.remove(0),
                belongToId, headId, null);
        List<SkillBarItem> skillBarItemList = new ArrayList<>(skillIdList.size());
        skillBarItemList.add(head);
        SkillBarItem index = head;
        if (skillIdList.size() > 0) {
            for (Long skillId : skillIdList) {
                String uuid = UUID.randomUUID().toString();
                index.setNextId(uuid);
                SkillBarItem item = new SkillBarItem(headId, belongToEnum.getBelongTo(), skillId, belongToId,
                        headId, null);
                skillBarItemList.add(item);
                index = item;
            }
        }
        return skillBarItemList;
    }
}
