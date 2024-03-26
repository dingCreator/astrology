package com.dingCreator.astrology.service;

import com.dingCreator.astrology.database.DatabaseProvider;
import com.dingCreator.astrology.entity.SkillBelongTo;
import com.dingCreator.astrology.mapper.SkillBelongToMapper;

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
    public static void createSkillBelongTo(String belongTo, Long belongToId, Long skillId) {
        SkillBelongTo skillBelongTo = new SkillBelongTo();
        skillBelongTo.setBelongTo(belongTo);
        skillBelongTo.setBelongToId(belongToId);
        skillBelongTo.setSkillId(skillId);
        DatabaseProvider.getInstance().doExecute(sqlSession ->
                sqlSession.getMapper(SkillBelongToMapper.class).createSkillBelongTo(skillBelongTo));
    }
}