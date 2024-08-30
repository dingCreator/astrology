package com.dingCreator.astrology.util;

import cn.hutool.core.collection.CollectionUtil;
import com.dingCreator.astrology.behavior.PlayerBehavior;
import com.dingCreator.astrology.behavior.TeamBehavior;
import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.cache.TeamCache;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.dto.LootDTO;
import com.dingCreator.astrology.dto.organism.player.PlayerDTO;
import com.dingCreator.astrology.dto.organism.player.PlayerInfoDTO;
import com.dingCreator.astrology.dto.task.PeakTaskDTO;
import com.dingCreator.astrology.dto.task.TaskBaseDTO;
import com.dingCreator.astrology.dto.task.TaskDetailDTO;
import com.dingCreator.astrology.entity.PeakTaskTemplate;
import com.dingCreator.astrology.entity.TaskSchedule;
import com.dingCreator.astrology.entity.TaskScheduleDetail;
import com.dingCreator.astrology.entity.TaskTemplate;
import com.dingCreator.astrology.enums.PlayerStatusEnum;
import com.dingCreator.astrology.enums.RankEnum;
import com.dingCreator.astrology.enums.exception.TaskExceptionEnum;
import com.dingCreator.astrology.enums.job.JobEnum;
import com.dingCreator.astrology.enums.task.TaskScheduleEnum;
import com.dingCreator.astrology.enums.task.TaskTypeEnum;
import com.dingCreator.astrology.service.PeakTaskTemplateService;
import com.dingCreator.astrology.service.TaskScheduleDetailService;
import com.dingCreator.astrology.service.TaskScheduleService;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ding
 * @date 2024/8/29
 */
public class TaskUtil {

    /**
     * 组装巅峰任务对象
     */
    public static PeakTaskDTO convertPeakTaskTpl2DTO(PeakTaskTemplate peakTaskTpl, TaskTemplate taskTpl,
                                                     Map<Long, LootDTO> lootMap, List<TaskTemplate> children) {
        TaskBaseDTO base = convertTaskTpl2DTO(taskTpl, lootMap, children);
        return PeakTaskDTO.builder()
                .jobEnum(JobEnum.getByCode(peakTaskTpl.getJob()))
                .rankEnum(RankEnum.getEnum(peakTaskTpl.getJob(), peakTaskTpl.getRank()))
                .base(base)
                .build();
    }

    /**
     * 组装任务对象
     *
     * @param taskTpl  任务模板
     * @param lootMap  掉落物
     * @param children 子任务
     * @return 组装好的任务
     */
    public static TaskBaseDTO convertTaskTpl2DTO(TaskTemplate taskTpl, Map<Long, LootDTO> lootMap, List<TaskTemplate> children) {
        TaskBaseDTO.TaskBaseDTOBuilder builder = TaskBaseDTO.builder()
                .id(taskTpl.getId())
                .taskTypeEnum(TaskTypeEnum.getByCode(taskTpl.getTaskType()))
                .taskName(taskTpl.getTaskName())
                .description(taskTpl.getDescription())
                .limitMapId(Arrays.stream(taskTpl.getLimitMapId().split(Constants.COMMA)).map(Long::parseLong).collect(Collectors.toList()))
                .childrenSort(taskTpl.getChildrenSort())
                .lootDTO(lootMap.get(taskTpl.getId()))
                .mutualExclusion(taskTpl.getMutualExclusion())
                .allowTeam(taskTpl.getAllowTeam());
        if (CollectionUtil.isNotEmpty(taskTpl.getDetails())) {
            builder.details(taskTpl.getDetails().stream().map(TaskDetailDTO::convert).collect(Collectors.toList()));
        }
        if (CollectionUtil.isNotEmpty(children)) {
            builder.children(children.stream().map(childTpl ->
                    convertTaskTpl2DTO(childTpl, lootMap, null))
                    .collect(Collectors.toList()));
        }
        return builder.build();
    }

    /**
     * 接取巅峰任务
     *
     * @param playerId 玩家ID
     */
    public static void receivePeakTask(long playerId) {
        ReceiveValidateContext context = new ReceiveValidateContext();
        initPlayerInfo(playerId, context);
        validatePlayer(context);
        initTaskDTO(context);
        initTaskSchedule(context);
        validateTask(context);
        validatePeakTask(context);
        createSchedule(context);
    }

    private static void initPlayerInfo(long playerId, ReceiveValidateContext context) {
        PlayerInfoDTO info = PlayerCache.getPlayerById(playerId);
        context.setPlayerInfoDTO(info);
        context.setPlayerIdList(info.getTeam() ? Collections.singletonList(info.getPlayerDTO().getId())
                : TeamCache.getTeamByPlayerId(info.getPlayerDTO().getId()).getMembers());
    }

    private static void validatePlayer(ReceiveValidateContext context) {
        PlayerInfoDTO infoDTO = context.getPlayerInfoDTO();
        PlayerDTO playerDTO = infoDTO.getPlayerDTO();
        if (playerDTO.getLevel() < RankEnum.getEnum(playerDTO.getJob(), playerDTO.getRank()).getMaxLevel()) {
            throw TaskExceptionEnum.PEAK_LOW_LEVEL.getException();
        }
        if (PlayerBehavior.getInstance().getStatus(playerDTO).equals(PlayerStatusEnum.MOVING.getCode())) {
            throw TaskExceptionEnum.PEAK_PLAYER_IS_MOVING.getException();
        }
        if (infoDTO.getTeam()) {
            TeamBehavior.getInstance().captainOnlyValidate(playerDTO.getId());
        }
    }

    private static void initTaskDTO(ReceiveValidateContext context) {
        PlayerDTO playerDTO = context.getPlayerInfoDTO().getPlayerDTO();
        PeakTaskDTO task = PeakTaskTemplateService.getPeakTaskDTO(playerDTO.getJob(), playerDTO.getRank());
        if (Objects.isNull(task)) {
            throw TaskExceptionEnum.PEAK_TASK_NOT_EXIST.getException();
        }
        context.setTaskBaseDTO(task.getBase());
    }

    private static void initTaskSchedule(ReceiveValidateContext context) {
        List<TaskSchedule> scheduleList = TaskScheduleService.getInProgressTask(context.getPlayerIdList());
        context.setScheduleList(scheduleList);
    }

    private static void validateTask(ReceiveValidateContext context) {
        PlayerInfoDTO info = context.getPlayerInfoDTO();
        TaskBaseDTO taskBaseDTO = context.getTaskBaseDTO();

        if (CollectionUtil.isNotEmpty(context.getScheduleList())) {
            boolean isInProgress = context.getScheduleList().stream().anyMatch(schedule ->
                    context.getPlayerIdList().stream().anyMatch(playerId ->
                            schedule.getPlayerId().equals(playerId) && schedule.getTaskTemplateId().equals(taskBaseDTO.getId())));
            if (isInProgress) {
                throw TaskExceptionEnum.ALREADY_IN_PROGRESS.getException();
            }
        }
        if (info.getTeam() && !taskBaseDTO.getAllowTeam()) {
            throw TaskExceptionEnum.NOT_ALLOW_TEAM.getException();
        }
        if (taskBaseDTO.getMutualExclusion()) {
            if (CollectionUtil.isNotEmpty(context.getScheduleList())) {
                throw TaskExceptionEnum.NOT_ALLOW_MUTUAL.getException();
            }
        }
        if (CollectionUtil.isNotEmpty(taskBaseDTO.getLimitMapId())
                && !taskBaseDTO.getLimitMapId().contains(info.getPlayerDTO().getMapId())) {
            throw TaskExceptionEnum.NOT_ALLOW_MAP.getException();
        }
    }

    private static void validatePeakTask(ReceiveValidateContext context) {
        if (CollectionUtil.isNotEmpty(context.getScheduleList())) {
            Long playerId = context.getPlayerInfoDTO().getPlayerDTO().getId();
            TaskSchedule schedule = context.getScheduleList().stream().filter(s -> s.getPlayerId().equals(playerId))
                    .filter(s -> s.getTaskTemplateId().equals(context.getTaskBaseDTO().getId())).findFirst().orElse(null);
            if (Objects.nonNull(schedule)) {
                if (TaskScheduleEnum.IN_PROGRESS.getType().equals(schedule.getTaskSchedule())) {
                    throw TaskExceptionEnum.ALREADY_IN_PROGRESS.getException();
                } else if (TaskScheduleEnum.COMPLETE.getType().equals(schedule.getTaskSchedule())) {
                    throw TaskExceptionEnum.ALREADY_COMPLETE.getException();
                } else if (TaskScheduleEnum.FAILED.getType().equals(schedule.getTaskSchedule())) {
                    throw TaskExceptionEnum.ALREADY_FAILED.getException();
                }
            }
        }
    }

    private static void createSchedule(ReceiveValidateContext context) {
        TaskScheduleService.createTaskSchedule(context.getTaskBaseDTO().getId(), context.getPlayerIdList());
        List<TaskScheduleDetail> details = context.getTaskBaseDTO().getDetails().stream()
                .map(dto -> new TaskScheduleDetail(context.getTaskBaseDTO().getId(), dto.getTargetId(), dto.getTargetCnt()))
                .collect(Collectors.toList());
        TaskScheduleDetailService.createTaskScheduleDetail(details);
        if (CollectionUtil.isNotEmpty(context.getTaskBaseDTO().getChildren())) {
            context.getTaskBaseDTO().getChildren()
                    .forEach(t -> TaskScheduleService.createTaskSchedule(t.getId(), context.getPlayerIdList()));
            List<TaskDetailDTO> detailDTOList = context.getTaskBaseDTO().getChildren().stream().map(TaskBaseDTO::getDetails)
                    .reduce((d1, d2) -> {
                        d2.addAll(d1);
                        return d2;
                    }).orElse(new ArrayList<>());


        }
    }

    @Data
    private static class ReceiveValidateContext {
        private PlayerInfoDTO playerInfoDTO;
        private TaskBaseDTO taskBaseDTO;
        private List<TaskSchedule> scheduleList;
        private List<Long> playerIdList;
    }
}
