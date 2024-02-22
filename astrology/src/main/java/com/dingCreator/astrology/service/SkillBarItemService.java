package com.dingCreator.astrology.service;

import com.dingCreator.astrology.database.DatabaseProvider;
import com.dingCreator.astrology.entity.SkillBarItem;
import com.dingCreator.astrology.mapper.SkillBarItemMapper;

import java.util.List;

/**
 * @author ding
 * @date 2024/2/21
 */
public class SkillBarItemService {

    @SuppressWarnings({"unchecked"})
    public static List<SkillBarItem> getSkillBarItemByBelongToId(String belongTo, Long belongToId) {
        return (List<SkillBarItem>) DatabaseProvider.getInstance().doExecute(sqlSession ->
                sqlSession.getMapper(SkillBarItemMapper.class).querySkillBarItemByBelongToId(belongTo, belongToId));
    }
}
