package com.dingCreator.astrology.cache;

import cn.hutool.core.collection.CollectionUtil;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.dto.task.TaskScheduleDetailDTO;
import com.dingCreator.astrology.dto.task.TaskScheduleTitleDTO;
import com.dingCreator.astrology.dto.task.TaskTemplateTitleDTO;
import com.dingCreator.astrology.entity.TaskScheduleDetail;
import com.dingCreator.astrology.enums.task.TaskScheduleEnum;
import com.dingCreator.astrology.service.TaskScheduleDetailService;
import com.dingCreator.astrology.service.TaskTitleService;
import com.dingCreator.astrology.util.LockUtil;
import com.dingCreator.astrology.util.TaskUtil;

import java.util.*;
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

    /**
     * 创建任务进度
     *
     * @param playerIds 玩家id列表
     * @param title     任务模板标题
     */
    public static void createTaskSchedule(List<Long> playerIds, TaskTemplateTitleDTO title) {
        playerIds.forEach(playerId -> {
            List<TaskScheduleDetail> detailList = title.getTemplateList().stream()
                    .flatMap(tpl -> tpl.getDetails().stream())
                    .map(tplDetail -> TaskScheduleDetail.builder()
                            .playerId(playerId)
                            .taskTitleId(tplDetail.getTitleId())
                            .taskTemplateId(tplDetail.getTemplateId())
                            .taskTemplateDetailId(tplDetail.getId())
                            .targetId(tplDetail.getTargetId())
                            .targetCnt(tplDetail.getTargetCnt())
                            .completeCnt(0)
                            .taskScheduleType(TaskScheduleEnum.IN_PROGRESS.getType())
                            .build()
                    ).collect(Collectors.toList());
            TaskScheduleDetailService.createTaskScheduleDetail(detailList);

            List<TaskScheduleTitleDTO> titleList = TASK_SCHEDULE_MAP.getOrDefault(playerId, new ArrayList<>());
            titleList.add(TaskUtil.constructTaskScheduleTitleDTO(title, detailList, playerId));
            TASK_SCHEDULE_MAP.put(playerId, titleList);
        });
    }

    /**
     * 根据进度详情ID获取进度详情
     *
     * @param playerId         玩家ID
     * @param scheduleDetailId 进度详情ID
     * @return 进度详情
     */
    public static TaskScheduleDetailDTO getTaskScheduleDetailDTOById(Long playerId, Long scheduleDetailId) {
        return listTaskScheduleByPlayerId(playerId).stream().map(title ->
                title.getScheduleList().stream().map(schedule ->
                        schedule.getDetailList().stream()
                                .filter(detail -> detail.getId().equals(scheduleDetailId)).findFirst().orElse(null)
                ).filter(Objects::nonNull).findFirst().orElse(null)
        ).filter(Objects::nonNull).findFirst().orElse(null);
    }

    /**
     * 根据进度详情ID获取总进度
     *
     * @param playerId         玩家ID
     * @param scheduleDetailId 进度详情ID
     * @return 总进度
     */
    public static TaskScheduleTitleDTO getTaskScheduleTitleDTOById(Long playerId, Long scheduleDetailId) {
        return listTaskScheduleByPlayerId(playerId)
                .stream().filter(title -> title.getScheduleList()
                        .stream().anyMatch(schedule -> schedule.getDetailList()
                                .stream().anyMatch(detail -> detail.getId().equals(scheduleDetailId)))
                ).findFirst().orElse(null);
    }

    public static void clearTplCache() {
        TASK_TEMPLATE_TITLE_MAP.clear();
        tplInit = false;
    }
}
