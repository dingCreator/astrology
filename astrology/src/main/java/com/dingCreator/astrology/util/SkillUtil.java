package com.dingCreator.astrology.util;

import com.dingCreator.astrology.constants.Constants;
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
    public static SkillBarItem buildSkillBarItemChain(List<Long> skillIdList, BelongToEnum belongToEnum, Long belongToId) {
        SkillBarItem skillBarItem = new SkillBarItem();
        skillBarItem.setBelongTo(belongToEnum.getBelongTo());
        skillBarItem.setBelongToId(belongToId);

        StringBuilder builder = new StringBuilder(skillIdList.remove(0).toString());
        if (skillIdList.size() > 0) {
            for (Long id : skillIdList) {
                builder.append(Constants.COMMA).append(id);
            }
        }

        skillBarItem.setSkillId(builder.toString());
        return skillBarItem;
    }
}
