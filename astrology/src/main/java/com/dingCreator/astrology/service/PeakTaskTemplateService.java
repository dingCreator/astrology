package com.dingCreator.astrology.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dingCreator.astrology.database.DatabaseProvider;
import com.dingCreator.astrology.dto.LootDTO;
import com.dingCreator.astrology.dto.task.PeakTaskDTO;
import com.dingCreator.astrology.entity.Loot;
import com.dingCreator.astrology.entity.PeakTaskTemplate;
import com.dingCreator.astrology.entity.TaskTemplate;
import com.dingCreator.astrology.enums.LootBelongToEnum;
import com.dingCreator.astrology.mapper.PeakTaskTemplateMapper;
import com.dingCreator.astrology.mapper.TaskTemplateMapper;
import com.dingCreator.astrology.util.LootUtil;
import com.dingCreator.astrology.util.TaskUtil;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author ding
 * @date 2024/8/27
 */
public class PeakTaskTemplateService {

    public static PeakTaskDTO getPeakTaskDTO(String job, int rank) {
        // 获取巅峰任务配置
        PeakTaskTemplate peakTaskTemplate = getPeakTaskTemplate(job, rank);
        if (Objects.isNull(peakTaskTemplate)) {
            return null;
        }
        // 获取任务模板
        Long templateId = peakTaskTemplate.getTaskTemplateId();
        List<TaskTemplate> templateList = DatabaseProvider.getInstance().executeReturn(sqlSession ->
                sqlSession.getMapper(TaskTemplateMapper.class).listTaskTemplateById(templateId));
        TaskTemplate parent = templateList.stream().filter(tpl -> templateId.equals(tpl.getId())).findFirst().orElse(null);
        if (Objects.isNull(parent)) {
            return null;
        }
        List<Long> templateIdList = templateList.stream().map(TaskTemplate::getId).collect(Collectors.toList());
        // 获取掉落物
        List<Loot> lootList = LootService.getByBelongToId(LootBelongToEnum.TASK.getBelongTo(), templateIdList);
        Map<Long, LootDTO> lootMap = lootList.stream()
                .collect(Collectors.toMap(Loot::getBelongToId, LootUtil::convertLoot, (l1, l2) -> l2));
        templateList.remove(parent);
        return TaskUtil.convertPeakTaskTpl2DTO(peakTaskTemplate, parent, lootMap, templateList);
    }

    /**
     * 获取巅峰任务模板
     *
     * @param job  职业
     * @param rank 阶级
     * @return 模板
     */
    public static PeakTaskTemplate getPeakTaskTemplate(String job, int rank) {
        return DatabaseProvider.getInstance().executeReturn(sqlSession -> {
            QueryWrapper<PeakTaskTemplate> wrapper = new QueryWrapper<>();
            wrapper.eq(PeakTaskTemplate.JOB, job).eq(PeakTaskTemplate.RANK, rank);
            return sqlSession.getMapper(PeakTaskTemplateMapper.class).selectOne(wrapper);
        });
    }
}
