package com.dingCreator.astrology.service;

import com.dingCreator.astrology.database.DatabaseProvider;
import com.dingCreator.astrology.entity.SkillBelongTo;
import com.dingCreator.astrology.mapper.SkillBelongToMapper;

import java.util.List;

/**
 * @author ding
 * @date 2024/2/22
 */
public class SkillBelongToService {

    /**
     * 新增技能归属记录
     *
     * @param belongTo   技能归属
     * @param belongToId 技能归属ID
     * @param skillId    技能ID
     */
    public void createSkillBelongTo(String belongTo, Long belongToId, Long skillId) {
        SkillBelongTo skillBelongTo = new SkillBelongTo();
        skillBelongTo.setBelongTo(belongTo);
        skillBelongTo.setBelongToId(belongToId);
        skillBelongTo.setSkillId(skillId);
        DatabaseProvider.getInstance().execute(sqlSession ->
                sqlSession.getMapper(SkillBelongToMapper.class).createSkillBelongTo(skillBelongTo));
    }

    /**
     * 获取拥有的技能
     *
     * @param belongTo   归属
     * @param belongToId 归属ID
     * @return 拥有的技能
     */
    public List<SkillBelongTo> querySkillBelongToList(String belongTo, Long belongToId) {
        return DatabaseProvider.getInstance().executeReturn(sqlSession ->
                sqlSession.getMapper(SkillBelongToMapper.class).querySkillBelongToList(belongTo, belongToId));
    }

    /**
     * 获取拥有的技能
     *
     * @param belongTo   归属
     * @param belongToId 归属ID
     * @return 拥有的技能
     */
    public List<SkillBelongTo> querySkillBelongToBySkillId(String belongTo, Long belongToId, List<Long> skillIds) {
        return DatabaseProvider.getInstance().executeReturn(sqlSession ->
                sqlSession.getMapper(SkillBelongToMapper.class).querySkillBelongToBySkillId(belongTo, belongToId, skillIds));
    }


    private static class Holder {
        private static final SkillBelongToService SERVICE = new SkillBelongToService();
    }

    private SkillBelongToService() {

    }

    public static SkillBelongToService getInstance() {
        return SkillBelongToService.Holder.SERVICE;
    }
}
