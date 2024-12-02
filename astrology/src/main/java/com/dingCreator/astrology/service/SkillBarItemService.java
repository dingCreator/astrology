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

    public SkillBarItem getSkillBarItemByBelongToId(String belongTo, Long belongToId) {
        return DatabaseProvider.getInstance().executeReturn(sqlSession ->
                sqlSession.getMapper(SkillBarItemMapper.class).querySkillBarItemByBelongToId(belongTo, belongToId));
    }

    public void addSkillBarItem(SkillBarItem skillBarItem) {
        DatabaseProvider.getInstance().execute(sqlSession ->
                sqlSession.getMapper(SkillBarItemMapper.class).insertSkillBarItem(skillBarItem));
    }

    public void deleteSkillBarItem(String belongTo, Long belongToId) {
        DatabaseProvider.getInstance().execute(sqlSession ->
                sqlSession.getMapper(SkillBarItemMapper.class).deleteSkillBarItem(belongTo, belongToId));
    }


    private static class Holder {
        private static final SkillBarItemService SERVICE = new SkillBarItemService();
    }

    private SkillBarItemService() {

    }

    public static SkillBarItemService getInstance() {
        return SkillBarItemService.Holder.SERVICE;
    }
}
