package com.dingCreator.astrology.cache;

import cn.hutool.core.collection.CollectionUtil;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.dto.task.TaskScheduleTitleDTO;
import com.dingCreator.astrology.dto.task.TaskTemplateTitleDTO;
import com.dingCreator.astrology.entity.TaskScheduleDetail;
import com.dingCreator.astrology.service.TaskScheduleDetailService;
import com.dingCreator.astrology.service.TaskTitleService;
import com.dingCreator.astrology.util.LockUtil;
import com.dingCreator.astrology.util.TaskUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ding
 * @date 2024/9/23
 */
public class TaskCache {

    /**
     * 任务模板缓存
     */
    private static final Map<Long, TaskTemplateTitleDTO> TASK_TEMPLATE_TITLE_MAP = new HashMap<>(32);

    /**
     * 任务进度缓存
     */
    private static final Map<Long, List<TaskScheduleTitleDTO>> TASK_SCHEDULE_MAP = new HashMap<>(512);

    private static volatile boolean tplInit = false;

    private static synchronized void initTpl() {
        if (!tplInit) {
            tplInit = true;
            List<TaskTemplateTitleDTO> titleList = TaskTitleService.constructTaskTitleDTOList(TaskTitleService.listTask());
            titleList.forEach(title -> TASK_TEMPLATE_TITLE_MAP.put(title.getId(), title));
        }
    }

    /**
     * 获取个人任务
     *
     * @param playerId 玩家ID
     * @return 玩家任务
     */
    public static List<TaskScheduleTitleDTO> listTaskScheduleByPlayerId(Long playerId) {
        if (!TASK_SCHEDULE_MAP.containsKey(playerId)) {
            LockUtil.execute(Constants.TASK_SCHEDULE_LOCK_PREFIX + playerId, () -> {
                List<TaskScheduleDetail> detailList = TaskScheduleDetailService.listTaskScheduleDetail(playerId);
                if (CollectionUtil.isEmpty(detailList)) {
                    TASK_SCHEDULE_MAP.put(playerId, new ArrayList<>());
                } else {
                    List<Long> detailIds = detailList.stream().map(TaskScheduleDetail::getTaskTemplateDetailId).collect(Collectors.toList());
                    List<TaskTemplateTitleDTO> titleList = TaskTitleService.listTaskTitleDTO(detailIds);
                    List<TaskScheduleTitleDTO> scheduleList = TaskUtil.constructTaskScheduleTitleDTOList(titleList,
                            detailList, playerId);
                    TASK_SCHEDULE_MAP.put(playerId, scheduleList);
                }
            });
        }
        return TASK_SCHEDULE_MAP.get(playerId);
    }

    /**
     * 获取所有任务模板标题
     *
     * @return 任务模板标题
     */
    public static List<TaskTemplateTitleDTO> listTaskTplTitle() {
        if (!tplInit) {
            initTpl();
        }
        return new ArrayList<>(TASK_TEMPLATE_TITLE_MAP.values());
    }

    /**
     * 根据ID获取任务模板标题
     *
     * @param titleId 标题ID
     * @return 任务模板标题
     */
    public static TaskTemplateTitleDTO getTaskTplById(Long titleId) {
        if (!tplInit) {
            initTpl();
        }
        return TASK_TEMPLATE_TITLE_MAP.get(titleId);
    }
}
