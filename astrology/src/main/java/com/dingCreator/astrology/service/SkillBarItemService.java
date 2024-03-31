package com.dingCreator.astrology.service;

import com.dingCreator.astrology.database.DatabaseProvider;
import com.dingCreator.astrology.dto.skill.SkillBarDTO;
import com.dingCreator.astrology.entity.SkillBarItem;
import com.dingCreator.astrology.mapper.SkillBarItemMapper;

import java.util.List;
import java.util.Objects;

/**
 * @author ding
 * @date 2024/2/21
 */
public class SkillBarItemService {

    @SuppressWarnings({"unchecked"})
    public static SkillBarItem getSkillBarItemByBelongToId(String belongTo, Long belongToId) {
        return (SkillBarItem) DatabaseProvider.getInstance().doExecute(sqlSession ->
                sqlSession.getMapper(SkillBarItemMapper.class).querySkillBarItemByBelongToId(belongTo, belongToId));
    }

    public static void addSkillBarItem(SkillBarItem skillBarItem) {
        DatabaseProvider.getInstance().doExecute(sqlSession ->
                sqlSession.getMapper(SkillBarItemMapper.class).insertSkillBarItem(skillBarItem));
    }

    public static void deleteSkillBarItem(String belongTo, Long belongToId) {
        DatabaseProvider.getInstance().doExecute(sqlSession ->
                sqlSession.getMapper(SkillBarItemMapper.class).deleteSkillBarItem(belongTo, belongToId));
    }
}
