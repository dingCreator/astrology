package com.dingCreator.astrology.util;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.dto.LootDTO;
import com.dingCreator.astrology.dto.task.*;
import com.dingCreator.astrology.entity.*;
import com.dingCreator.astrology.enums.RankEnum;
import com.dingCreator.astrology.enums.job.JobEnum;
import com.dingCreator.astrology.enums.task.TaskScheduleEnum;
import com.dingCreator.astrology.enums.task.TaskTargetTypeEnum;
import com.dingCreator.astrology.enums.task.TaskTypeEnum;
import com.dingCreator.astrology.event.EventHelper;
import com.dingCreator.astrology.event.listener.TaskLootListener;
import com.dingCreator.astrology.util.ability.TaskUtilAbility;
import com.dingCreator.astrology.util.context.CompleteTaskContext;
import com.dingCreator.astrology.util.context.ReceiveTaskContext;
import com.dingCreator.astrology.vo.TaskResultVO;

import java.util.*;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author ding
 * @date 2024/8/29
 */
public class TaskUtil {

    static {
        EventHelper.addListener(new TaskLootListener());
    }

    /**
     * 组装巅峰任务对象
     */
    public static PeakTaskDTO constructPeakTaskDTO(PeakTaskTemplate peakTaskTpl, TaskTemplateTitleDTO title) {
        return PeakTaskDTO.builder()
                .jobEnum(JobEnum.getByCode(peakTaskTpl.getJob()))
                .rankEnum(RankEnum.getEnum(peakTaskTpl.getJob(), peakTaskTpl.getRank()))
                .taskTitle(title)
                .build();
    }

    /**
     * 组装任务对象
     *
     * @param titleList    任务标题
     * @param titleLootMap 全任务完成后掉落物
     * @param lootMap      任务掉落物
     * @return 组装好的任务
     */
    public static List<TaskTemplateTitleDTO> constructTaskTemplateTitleDTOList(
            List<TaskTemplateTitle> titleList, Map<Long, LootDTO> titleLootMap, Map<Long, LootDTO> lootMap) {
        return titleList.stream().map(title -> constructTaskTemplateTitleDTO(title, titleLootMap.get(title.getId()), lootMap))
                .collect(Collectors.toList());
    }

    /**
     * 组装任务对象
     *
     * @param title     任务标题
     * @param titleLoot 全任务完成后掉落物
     * @param lootMap   任务掉落物
     * @return 组装好的任务
     */
    public static TaskTemplateTitleDTO constructTaskTemplateTitleDTO(TaskTemplateTitle title, LootDTO titleLoot,
                                                                     Map<Long, LootDTO> lootMap) {
        List<TaskTemplateDTO> tplList = title.getTemplateList().stream()
                .sorted(Comparator.comparing(TaskTemplate::getPriority))
                .map(tpl -> constructTaskTemplateDTO(tpl, lootMap.get(tpl.getId())))
                .collect(Collectors.toList());

        return TaskTemplateTitleDTO.builder()
                .id(title.getId())
                .name(title.getName())
                .description(title.getDescription())
                .taskTypeEnum(TaskTypeEnum.getByCode(title.getTaskType()))
                .limitMapId(StringUtils.isBlank(title.getLimitMapId()) ? null : Arrays.stream(title.getLimitMapId()
                        .split(Constants.COMMA)).map(Long::parseLong).collect(Collectors.toList()))
                .childrenSort(title.getChildrenSort())
                .mutualExclusion(title.getMutualExclusion())
                .allowTeam(title.getAllowTeam())
                .allowRepeatableReceive(title.getAllowRepeatableReceive())
                .lootDTO(titleLoot)
                .templateList(tplList)
                .build();
    }

    public static TaskTemplateDTO constructTaskTemplateDTO(TaskTemplate tpl, LootDTO loot) {
        return TaskTemplateDTO.builder()
                .id(tpl.getId())
                .description(tpl.getDescription())
                .titleId(tpl.getTitleId())
                .lootDTO(loot)
                .priority(tpl.getPriority())
                .details(tpl.getDetailList().stream().map(TaskUtil::constructTaskTemplateDetailDTO).collect(Collectors.toList()))
                .build();
    }

    public static TaskTemplateDetailDTO constructTaskTemplateDetailDTO(TaskTemplateDetail detail) {
        return TaskTemplateDetailDTO.builder()
                .id(detail.getId())
                .templateId(detail.getTaskTemplateId())
                .target(TaskTargetTypeEnum.getByCode(detail.getTargetType()))
                .allowFailed(detail.getAllowFailed())
                .targetId(detail.getTargetId())
                .targetCnt(detail.getTargetCnt())
                .build();
    }

    /**
     * 构建任务进度
     *
     * @param titleList  模板标题
     * @param detailList 进度详情
     * @param playerId   玩家ID
     * @return 任务进度
     */
    public static List<TaskScheduleTitleDTO> constructTaskScheduleTitleDTOList(List<TaskTemplateTitleDTO> titleList,
                                                                               List<TaskScheduleDetail> detailList, Long playerId) {
        Map<Long, List<TaskScheduleDetail>> titleIdScheduleMap = detailList.stream()
                .filter(detail -> detail.getPlayerId().equals(playerId))
                .collect(Collectors.groupingBy(TaskScheduleDetail::getTaskTitleId));
        return titleList.stream().map(title -> constructTaskScheduleTitleDTO(title, titleIdScheduleMap
                .getOrDefault(title.getId(), new ArrayList<>()), playerId)).collect(Collectors.toList());
    }

    /**
     * 构建任务进度
     *
     * @param title      模板标题
     * @param detailList 进度详情
     * @return 任务进度
     */
    public static TaskScheduleTitleDTO constructTaskScheduleTitleDTO(TaskTemplateTitleDTO title,
                                                                     List<TaskScheduleDetail> detailList, Long playerId) {
        List<TaskScheduleDetailDTO> detailDTOList = constructTaskScheduleDetailDTO(detailList, playerId);
        List<TaskScheduleDTO> scheduleDTOList = constructTaskScheduleDTO(title, detailDTOList, playerId);
        return TaskScheduleTitleDTO.builder()
                .playerId(playerId)
                .taskTemplateTitleId(title.getId())
                .title(title.getName())
                .description(title.getDescription())
                .scheduleList(scheduleDTOList)
                .taskScheduleEnum(TaskScheduleEnum.getWholeSchedule(
                        scheduleDTOList.stream().map(TaskScheduleDTO::getTaskScheduleEnum).collect(Collectors.toList())))
                .build();
    }

    public static List<TaskScheduleDetailDTO> constructTaskScheduleDetailDTO(List<TaskScheduleDetail> detailList, Long playerId) {
        return detailList.stream()
                .filter(detail -> detail.getPlayerId().equals(playerId))
                .map(detail -> TaskScheduleDetailDTO.builder()
                        .id(detail.getId())
                        .playerId(detail.getPlayerId())
                        .taskTitleId(detail.getTaskTitleId())
                        .taskTemplateId(detail.getTaskTemplateId())
                        .taskTemplateDetailId(detail.getTaskTemplateDetailId())
                        .targetId(detail.getTargetId())
                        .completeCnt(detail.getCompleteCnt())
                        .targetCnt(detail.getTargetCnt())
                        .taskScheduleType(TaskScheduleEnum.getByType(detail.getTaskScheduleType()))
                        .build()
                ).collect(Collectors.toList());
    }


    public static List<TaskScheduleDTO> constructTaskScheduleDTO(TaskTemplateTitleDTO title,
                                                                 List<TaskScheduleDetailDTO> detailList, Long playerId) {
        Map<Long, TaskTemplateDTO> tplMap = title.getTemplateList().stream()
                .collect(Collectors.toMap(TaskTemplateDTO::getId, Function.identity()));

        List<TaskScheduleDTO> scheduleDTOList = new ArrayList<>(detailList.size());
        detailList.stream().collect(Collectors.groupingBy(TaskScheduleDetailDTO::getTaskTemplateId))
                .forEach((tplId, scheduleList) -> TaskScheduleDTO.builder().playerId(playerId)
                        .taskTemplateId(tplId)
                        .description(tplMap.getOrDefault(tplId, new TaskTemplateDTO()).getDescription())
                        .detailList(scheduleList)
                        .taskScheduleEnum(TaskScheduleEnum.getWholeSchedule(
                                scheduleList.stream().map(TaskScheduleDetailDTO::getTaskScheduleType).collect(Collectors.toList())))
                        .build()
                );
        return scheduleDTOList;
    }

    /**
     * 接取巅峰任务
     *
     * @param playerId 玩家ID
     */
    public static void receivePeakTask(long playerId) {
        PeakTaskDTO peakTaskDTO = TaskUtilAbility.initPeakTaskTplDTO(PlayerCache.getPlayerById(playerId));
        // 任务校验
        validateTask(playerId, peakTaskDTO.getTaskTitle().getId());
        // 校验巅峰任务信息
        TaskUtilAbility.validatePeakTask(PlayerCache.getPlayerById(playerId));
        // 创建任务进度
        TaskUtilAbility.createSchedule(playerId, peakTaskDTO.getTaskTitle());
    }

    /**
     * 完成任务
     *
     * @param playerId         玩家id
     * @param scheduleDetailId 进度详情ID
     */
    public static TaskResultVO completeTask(long playerId, long scheduleDetailId) {
        return LockUtil.execute(Constants.TASK_SCHEDULE_LOCK_PREFIX + playerId + Constants.UNDERLINE + scheduleDetailId, () -> {
            // 初始化上下文
            CompleteTaskContext context = new CompleteTaskContext();
            // 初始化进度详情
            context.setScheduleDetail(TaskUtilAbility.initScheduleDetailById(playerId, scheduleDetailId));
            // 初始化总进度
            context.setScheduleTitleDTO(TaskUtilAbility.initScheduleTitleByDetailId(playerId, scheduleDetailId));
            // 初始化模板标题
            context.setTplTitle(TaskUtilAbility.initTaskTplDTO(context.getScheduleDetail().getTaskTitleId()));
            // 校验任务信息
            TaskUtilAbility.validateCompleteTask(context.getScheduleDetail(), context.getScheduleTitleDTO(), context.getTplTitle());
            // 初始化模板详情
            context.setTplDetail(TaskUtilAbility.initTplDetail(context.getTplTitle(), context.getScheduleDetail()));
            // 任务过程
            context.setTaskResultVO(TaskUtilAbility.completeTask(context.getTplTitle(), context.getScheduleDetail(), context.getTplDetail(), playerId));
            // 持久化任务信息
            TaskUtilAbility.updateTaskSchedule(context.getScheduleDetail());
            // 刷新任务进度
            TaskUtilAbility.flushScheduleEnum(context.getTplTitle(), context.getScheduleTitleDTO(), context.getScheduleDetail());
            // 返回结果
            return context.getTaskResultVO();
        });
    }

    /**
     * 校验任务信息
     *
     * @param playerId   玩家id
     * @param tplTitleId 任务模板id
     */
    public static void validateTask(long playerId, long tplTitleId) {
        // 初始化上下文
        ReceiveTaskContext context = new ReceiveTaskContext();
        // 初始化玩家信息
        context.setPlayerInfo(TaskUtilAbility.initPlayerInfo(playerId));
        // 初始化玩家队伍信息
        context.setTeamPlayerInfo(TaskUtilAbility.initPlayerTeamInfo(context.getPlayerInfo()));
        // 校验玩家信息
        TaskUtilAbility.validatePlayer(context.getPlayerInfo(), context.getTeamPlayerInfo());

        // 初始化任务信息
        context.setTitle(TaskUtilAbility.initTaskTplDTO(tplTitleId));
        // 初始化任务进度
        context.setScheduleList(TaskUtilAbility.initTaskSchedule(context.getTeamPlayerInfo()));
        // 校验任务信息
        TaskUtilAbility.validateTask(context.getPlayerInfo(), context.getTeamPlayerInfo(),
                context.getTitle(), context.getScheduleList());
    }
}
