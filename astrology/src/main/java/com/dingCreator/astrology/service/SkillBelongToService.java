package com.dingCreator.astrology.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dingCreator.astrology.database.DatabaseProvider;
import com.dingCreator.astrology.entity.SkillBelongTo;
import com.dingCreator.astrology.enums.BelongToEnum;
import com.dingCreator.astrology.enums.exception.SkillExceptionEnum;
import com.dingCreator.astrology.enums.skill.SkillEnum;
import com.dingCreator.astrology.mapper.SkillBelongToMapper;
import com.dingCreator.astrology.response.PageResponse;

import java.util.List;
import java.util.stream.Collectors;

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
        DatabaseProvider.getInstance().batchTransactionExecute(sqlSession -> {
            SkillBelongToMapper mapper = sqlSession.getMapper(SkillBelongToMapper.class);
            // 已有技能不再重复插入
            int cnt = mapper.selectCount(new QueryWrapper<SkillBelongTo>()
                    .eq(SkillBelongTo.BELONG_TO, belongTo)
                    .eq(SkillBelongTo.BELONG_TO_ID, belongToId)
                    .eq(SkillBelongTo.SKILL_ID, skillId)
            );
            if (cnt == 0) {
                mapper.createSkillBelongTo(skillBelongTo);
            } else {
                throw SkillExceptionEnum.SKILL_ALREADY_EXIST.getException();
            }
        });
    }

    /**
     * 获取拥有的技能
     *
     * @param belongTo   归属
     * @param belongToId 归属ID
     * @return 拥有的技能
     */
    public PageResponse<SkillBelongTo> querySkillBelongToPage(String belongTo, Long belongToId,
                                                              int pageIndex, int pageSize) {
        return DatabaseProvider.getInstance().executeReturn(sqlSession -> {
                    QueryWrapper<SkillBelongTo> wrapper = new QueryWrapper<SkillBelongTo>()
                            .eq(SkillBelongTo.BELONG_TO, belongTo)
                            .eq(SkillBelongTo.BELONG_TO_ID, belongToId);
                    Page<SkillBelongTo> page = new Page<>(pageIndex, pageSize);
                    sqlSession.getMapper(SkillBelongToMapper.class).selectPage(page, wrapper);
                    PageResponse<SkillBelongTo> response = new PageResponse<>();
                    response.setData(page.getRecords());
                    response.setPageIndex((int) page.getCurrent());
                    response.setPageSize((int) page.getSize());
                    response.setTotal((int) page.getTotal());
                    return response;
                }
        );
    }

    /**
     * 获取拥有的技能
     *
     * @param belongTo   归属
     * @param belongToId 归属ID
     * @return 拥有的技能
     */
    public List<SkillBelongTo> querySkillBelongToList(String belongTo, Long belongToId) {
        return DatabaseProvider.getInstance().executeReturn(sqlSession -> {
                    QueryWrapper<SkillBelongTo> wrapper = new QueryWrapper<SkillBelongTo>()
                            .eq(SkillBelongTo.BELONG_TO, belongTo)
                            .eq(SkillBelongTo.BELONG_TO_ID, belongToId);
                    return sqlSession.getMapper(SkillBelongToMapper.class).selectList(wrapper);
                }
        );
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

    public void deleteInactiveSkills(BelongToEnum belongToEnum, Long belongToId) {
        DatabaseProvider.getInstance().execute(sqlSession -> {
            SkillBelongToMapper mapper = sqlSession.getMapper(SkillBelongToMapper.class);
            List<SkillBelongTo> skills = mapper.selectList(new QueryWrapper<SkillBelongTo>()
                    .eq(SkillBelongTo.BELONG_TO, belongToEnum.getBelongTo())
                    .eq(SkillBelongTo.BELONG_TO_ID, belongToId));
            List<Long> inactiveSkillIds = skills.stream()
                    .map(SkillBelongTo::getSkillId)
                    .filter(skillId -> !SkillEnum.getById(skillId).getActive())
                    .collect(Collectors.toList());
            if (inactiveSkillIds.isEmpty()) {
                return;
            }
            mapper.delete(new QueryWrapper<SkillBelongTo>()
                    .in(SkillBelongTo.SKILL_ID, inactiveSkillIds)
                    .eq(SkillBelongTo.BELONG_TO, belongToEnum.getBelongTo())
                    .eq(SkillBelongTo.BELONG_TO_ID, belongToId)
            );
        });
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
