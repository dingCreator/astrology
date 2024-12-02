package com.dingCreator.astrology.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dingCreator.astrology.cache.TaskCache;
import com.dingCreator.astrology.database.DatabaseProvider;
import com.dingCreator.astrology.dto.task.PeakTaskDTO;
import com.dingCreator.astrology.dto.task.TaskTemplateTitleDTO;
import com.dingCreator.astrology.entity.PeakTaskTemplate;
import com.dingCreator.astrology.mapper.PeakTaskTemplateMapper;
import com.dingCreator.astrology.util.TaskUtil;

import java.util.Objects;

/**
 * @author ding
 * @date 2024/8/27
 */
public class PeakTaskTemplateService {

    public PeakTaskDTO getPeakTaskDTO(String job, int rank) {
        // 获取巅峰任务配置
        PeakTaskTemplate peakTaskTemplate = getPeakTaskTemplate(job, rank);
        if (Objects.isNull(peakTaskTemplate)) {
            return null;
        }
        // 获取任务模板
        TaskTemplateTitleDTO title = TaskCache.getTaskTplById(peakTaskTemplate.getTaskTemplateTitleId());
        return TaskUtil.constructPeakTaskDTO(peakTaskTemplate, title);
    }

    /**
     * 获取巅峰任务模板
     *
     * @param job  职业
     * @param rank 阶级
     * @return 模板
     */
    public PeakTaskTemplate getPeakTaskTemplate(String job, int rank) {
        return DatabaseProvider.getInstance().executeReturn(sqlSession -> {
            QueryWrapper<PeakTaskTemplate> wrapper = new QueryWrapper<>();
            wrapper.eq(PeakTaskTemplate.JOB, job).eq(PeakTaskTemplate.RANK, rank);
            return sqlSession.getMapper(PeakTaskTemplateMapper.class).selectOne(wrapper);
        });
    }

    public void createPeakTaskTemplate(PeakTaskTemplate peakTaskTemplate) {
        DatabaseProvider.getInstance().execute(sqlSession ->
                sqlSession.getMapper(PeakTaskTemplateMapper.class).insert(peakTaskTemplate));
    }


    private static class Holder {
        private static final PeakTaskTemplateService SERVICE = new PeakTaskTemplateService();
    }

    private PeakTaskTemplateService() {

    }

    public static PeakTaskTemplateService getInstance() {
        return PeakTaskTemplateService.Holder.SERVICE;
    }
}
