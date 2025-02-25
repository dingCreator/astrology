package com.dingCreator.astrology.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dingCreator.astrology.database.DatabaseProvider;
import com.dingCreator.astrology.dto.loot.LootDTO;
import com.dingCreator.astrology.dto.loot.LootQueryDTO;
import com.dingCreator.astrology.dto.task.TaskTemplateTitleDTO;
import com.dingCreator.astrology.entity.Loot;
import com.dingCreator.astrology.entity.TaskTemplate;
import com.dingCreator.astrology.entity.TaskTemplateTitle;
import com.dingCreator.astrology.enums.LootBelongToEnum;
import com.dingCreator.astrology.mapper.TaskTemplateTitleMapper;
import com.dingCreator.astrology.request.TaskTemplateTitleQryReq;
import com.dingCreator.astrology.util.LootUtil;
import com.dingCreator.astrology.util.TaskUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ding
 * @date 2024/9/23
 */
public class TaskTitleService {

    public List<TaskTemplateTitle> listTask() {
        return DatabaseProvider.getInstance().executeReturn(sqlSession ->
                sqlSession.getMapper(TaskTemplateTitleMapper.class).listTaskTemplate(new TaskTemplateTitleQryReq()));
    }

    /**
     * 获取任务
     *
     * @param titleId 标题ID
     * @return 任务
     */
    public TaskTemplateTitleDTO getTaskTitleDTO(Long titleId) {
        TaskTemplateTitle title = getTaskTitle(titleId);
        return constructTaskTitleDTO(title);
    }

    /**
     * 获取任务
     *
     * @param detailIds 详情ID
     * @return 任务列表
     */
    public List<TaskTemplateTitleDTO> listTaskTitleDTO(List<Long> detailIds) {
        List<TaskTemplateTitle> titleList = getTaskTitle(detailIds);
        return constructTaskTitleDTOList(titleList);
    }

    /**
     * 获取单个
     *
     * @param title 任务
     * @return 任务
     */
    public TaskTemplateTitleDTO constructTaskTitleDTO(TaskTemplateTitle title) {
        List<TaskTemplateTitleDTO> list = constructTaskTitleDTOList(Collections.singletonList(title));
        if (CollectionUtil.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    public List<TaskTemplateTitleDTO> constructTaskTitleDTOList(List<TaskTemplateTitle> titleList) {
        if (CollectionUtil.isEmpty(titleList)) {
            return new ArrayList<>();
        }

        List<Long> titleIdList = titleList.stream().map(TaskTemplateTitle::getId).collect(Collectors.toList());
        List<Long> templateIdList = titleList.stream().flatMap(title -> title.getTemplateList().stream())
                .map(TaskTemplate::getId).collect(Collectors.toList());
        // 不完整的任务不转化
        if (CollectionUtils.isEmpty(templateIdList)) {
            return new ArrayList<>();
        }
        if (titleList.stream().anyMatch(title ->
                title.getTemplateList().stream().anyMatch(tpl ->
                        CollectionUtil.isEmpty(tpl.getDetailList())))) {
            return new ArrayList<>();
        }
        // 获取掉落物
        List<LootQueryDTO> titleLootList
                = LootService.getInstance().getByBelongToId(LootBelongToEnum.TASK_TITLE.getBelongTo(), titleIdList);
        List<LootQueryDTO> lootList
                = LootService.getInstance().getByBelongToId(LootBelongToEnum.TASK.getBelongTo(), templateIdList);

        Map<Long, LootDTO> titleLootMap = titleLootList.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(LootQueryDTO::getBelongToId, LootUtil::convertLoot, (l1, l2) -> l2));
        Map<Long, LootDTO> lootMap = lootList.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(LootQueryDTO::getBelongToId, LootUtil::convertLoot, (l1, l2) -> l2));
        return TaskUtil.constructTaskTemplateTitleDTOList(titleList, titleLootMap, lootMap);
    }

    /**
     * 获取任务标题
     *
     * @param titleId 任务标题ID
     * @return 任务标题
     */
    public TaskTemplateTitle getTaskTitle(Long titleId) {
        return DatabaseProvider.getInstance().executeReturn(sqlSession -> {
            TaskTemplateTitleQryReq req = TaskTemplateTitleQryReq.builder().titleId(titleId).build();
            List<TaskTemplateTitle> list = sqlSession.getMapper(TaskTemplateTitleMapper.class).listTaskTemplate(req);
            return CollectionUtil.isEmpty(list) ? null : list.get(0);
        });
    }

    /**
     * 获取任务标题
     *
     * @param detailIds 详情ID
     * @return 任务标题
     */
    public List<TaskTemplateTitle> getTaskTitle(List<Long> detailIds) {
        return DatabaseProvider.getInstance().executeReturn(sqlSession -> {
            TaskTemplateTitleQryReq req = TaskTemplateTitleQryReq.builder().detailIds(detailIds).build();
            return sqlSession.getMapper(TaskTemplateTitleMapper.class).listTaskTemplate(req);
        });
    }


    private static class Holder {
        private static final TaskTitleService SERVICE = new TaskTitleService();
    }

    private TaskTitleService() {

    }

    public static TaskTitleService getInstance() {
        return TaskTitleService.Holder.SERVICE;
    }
}
