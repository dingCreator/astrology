package com.dingCreator.astrology.util.ability;

import cn.hutool.core.collection.CollectionUtil;
import com.dingCreator.astrology.behavior.PlayerBehavior;
import com.dingCreator.astrology.behavior.TeamBehavior;
import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.cache.TaskCache;
import com.dingCreator.astrology.cache.TeamCache;
import com.dingCreator.astrology.dto.TeamDTO;
import com.dingCreator.astrology.dto.organism.player.PlayerDTO;
import com.dingCreator.astrology.dto.organism.player.PlayerInfoDTO;
import com.dingCreator.astrology.dto.task.*;
import com.dingCreator.astrology.enums.PlayerStatusEnum;
import com.dingCreator.astrology.enums.RankEnum;
import com.dingCreator.astrology.enums.exception.TaskExceptionEnum;
import com.dingCreator.astrology.enums.task.TaskScheduleEnum;
import com.dingCreator.astrology.event.EventHelper;
import com.dingCreator.astrology.event.event.TaskEvent;
import com.dingCreator.astrology.service.PeakTaskTemplateService;
import com.dingCreator.astrology.service.TaskScheduleDetailService;
import com.dingCreator.astrology.vo.TaskResultVO;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ding
 * @date 2024/9/23
 */
public class TaskUtilAbility {

    /**
     * 初始化玩家信息
     *
     * @param playerId 玩家ID
     */
    public static PlayerInfoDTO initPlayerInfo(long playerId) {
        return PlayerCache.getPlayerById(playerId);
    }

    /**
     * 初始化玩家队伍信息
     *
     * @param playerInfo 玩家信息
     */
    public static List<PlayerInfoDTO> initPlayerTeamInfo(PlayerInfoDTO playerInfo) {
        if (!playerInfo.getTeam()) {
            return Collections.singletonList(playerInfo);
        }
        return TeamCache.getTeamByPlayerId(playerInfo.getPlayerDTO().getId()).getMembers().stream()
                .map(PlayerCache::getPlayerById).collect(Collectors.toList());
    }

    /**
     * 校验玩家信息
     *
     * @param playerInfo 玩家信息
     */
    public static void validatePlayer(PlayerInfoDTO playerInfo, List<PlayerInfoDTO> teamPlayers) {
        teamPlayers.stream().map(PlayerInfoDTO::getPlayerDTO).forEach(teamPlayer -> {
            if (playerInfo.getPlayerDTO().getStatus().equals(PlayerStatusEnum.MOVING.getCode())) {
                throw TaskExceptionEnum.PLAYER_IS_MOVING.getException();
            }
        });
        PlayerDTO playerDTO = playerInfo.getPlayerDTO();
        if (playerInfo.getTeam()) {
            TeamBehavior.getInstance().captainOnlyValidate(playerDTO.getId());
        }
    }

    /**
     * 初始化任务模板
     *
     * @param titleId 任务标题ID
     * @return 巅峰任务
     */
    public static TaskTemplateTitleDTO initTaskTplDTO(Long titleId) {
        TaskTemplateTitleDTO task = TaskCache.getTaskTplById(titleId);
        if (Objects.isNull(task)) {
            throw TaskExceptionEnum.TASK_TPL_NOT_EXIST.getException();
        }
        return task;
    }

    /**
     * 初始化巅峰任务
     *
     * @param playerInfo 玩家信息
     * @return 巅峰任务
     */
    public static PeakTaskDTO initPeakTaskTplDTO(PlayerInfoDTO playerInfo) {
        PlayerDTO playerDTO = playerInfo.getPlayerDTO();
        PeakTaskDTO task = PeakTaskTemplateService.getInstance().getPeakTaskDTO(playerDTO.getJob(), playerDTO.getRank());
        if (Objects.isNull(task)) {
            throw TaskExceptionEnum.PEAK_TASK_NOT_EXIST.getException();
        }
        return task;
    }

    /**
     * 初始化任务进度
     *
     * @param teamPlayers 小队玩家
     * @return 任务进度
     */
    public static List<TaskScheduleTitleDTO> initTaskSchedule(List<PlayerInfoDTO> teamPlayers) {
        return teamPlayers.stream().map(info -> info.getPlayerDTO().getId()).map(TaskCache::listTaskScheduleByPlayerId)
                .flatMap(Collection::stream).collect(Collectors.toList());
    }

    /**
     * 校验任务信息
     *
     * @param playerInfo   玩家信息
     * @param teamPlayers  小队玩家
     * @param title        任务
     * @param scheduleList 任务进度
     */
    public static void validateTask(PlayerInfoDTO playerInfo, List<PlayerInfoDTO> teamPlayers, TaskTemplateTitleDTO title,
                                    List<TaskScheduleTitleDTO> scheduleList) {
        List<Long> teamPlayerIds = teamPlayers.stream().map(info -> info.getPlayerDTO().getId()).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(scheduleList)) {
            List<TaskScheduleEnum> invalidScheduleEnumList;
            if (title.getAllowRepeatableReceive()) {
                invalidScheduleEnumList = Arrays.asList(TaskScheduleEnum.IN_PROGRESS, TaskScheduleEnum.NOT_START);
            } else {
                invalidScheduleEnumList = Arrays.asList(TaskScheduleEnum.values());
            }

            Map<Long, List<TaskScheduleTitleDTO>> scheduleMap = scheduleList.stream()
                    .collect(Collectors.groupingBy(TaskScheduleTitleDTO::getPlayerId));
            teamPlayerIds.stream().filter(scheduleMap::containsKey)
                    .map(scheduleMap::get)
                    .forEach(sl -> sl.stream().filter(schedule -> schedule.getTaskTemplateTitleId().equals(title.getId()))
                            .filter(schedule -> invalidScheduleEnumList.contains(schedule.getTaskScheduleEnum()))
                            .forEach(schedule -> {
                                if (TaskScheduleEnum.IN_PROGRESS.equals(schedule.getTaskScheduleEnum())) {
                                    throw TaskExceptionEnum.ALREADY_IN_PROGRESS.getException();
                                } else if (TaskScheduleEnum.COMPLETE.equals(schedule.getTaskScheduleEnum())) {
                                    throw TaskExceptionEnum.ALREADY_COMPLETE.getException();
                                } else if (TaskScheduleEnum.FAILED.equals(schedule.getTaskScheduleEnum())) {
                                    throw TaskExceptionEnum.ALREADY_FAILED.getException();
                                }
                            })
                    );
        }
        if (playerInfo.getTeam() && !title.getAllowTeam()) {
            throw TaskExceptionEnum.NOT_ALLOW_TEAM.getException();
        }
        if (title.getMutualExclusion()) {
            if (CollectionUtil.isNotEmpty(scheduleList)) {
                throw TaskExceptionEnum.NOT_ALLOW_MUTUAL.getException();
            }
        }
        if (CollectionUtil.isNotEmpty(title.getLimitMapId())
                && !title.getLimitMapId().contains(playerInfo.getPlayerDTO().getMapId())) {
            throw TaskExceptionEnum.NOT_ALLOW_MAP.getException();
        }
    }

    /**
     * 校验巅峰任务
     *
     * @param playerInfo 玩家信息
     */
    public static void validatePeakTask(PlayerInfoDTO playerInfo) {
        PlayerDTO player = playerInfo.getPlayerDTO();
        RankEnum rankEnum = RankEnum.getEnum(player.getJob(), player.getRank());
        if (player.getLevel() < rankEnum.getMaxLevel()) {
            throw TaskExceptionEnum.PEAK_LOW_LEVEL.getException();
        }
    }

    /**
     * 创建任务进度
     *
     * @param playerId 玩家ID
     * @param title    任务模板标题
     */
    public static void createSchedule(Long playerId, TaskTemplateTitleDTO title) {
        TeamDTO teamDTO = TeamCache.getTeamByPlayerId(playerId);
        List<Long> teamPlayerIds;
        if (Objects.nonNull(teamDTO)) {
            teamPlayerIds = teamDTO.getMembers();
        } else {
            teamPlayerIds = Collections.singletonList(playerId);
        }
        TaskCache.createTaskSchedule(teamPlayerIds, title);
    }

    /**
     * 刷新任务状态
     *
     * @param scheduleTitle 总进度
     */
    public static void flushScheduleEnum(TaskTemplateTitleDTO tplTitle, TaskScheduleTitleDTO scheduleTitle,
                                         TaskScheduleDetailDTO scheduleDetail) {
        if (Objects.nonNull(scheduleTitle)) {
            scheduleTitle.getScheduleList().forEach(schedule -> {
                List<TaskScheduleEnum> detailScheduleEnumList = schedule.getDetailList().stream()
                        .map(TaskScheduleDetailDTO::getTaskScheduleType).collect(Collectors.toList());
                TaskScheduleEnum oldScheduleEnum = schedule.getTaskScheduleEnum();
                TaskScheduleEnum newScheduleEnum = TaskScheduleEnum.getWholeSchedule(detailScheduleEnumList);
                schedule.setTaskScheduleEnum(newScheduleEnum);
                scheduleEnumChange(tplTitle, scheduleDetail,TaskEvent.TaskScheduleLevel.SCHEDULE, oldScheduleEnum, newScheduleEnum);
            });
            List<TaskScheduleEnum> scheduleEnumList = scheduleTitle.getScheduleList().stream()
                    .map(TaskScheduleDTO::getTaskScheduleEnum).collect(Collectors.toList());
            TaskScheduleEnum oldTitleScheduleEnum = scheduleTitle.getTaskScheduleEnum();
            TaskScheduleEnum newTitleScheduleEnum = TaskScheduleEnum.getWholeSchedule(scheduleEnumList);
            scheduleTitle.setTaskScheduleEnum(newTitleScheduleEnum);
            scheduleEnumChange(tplTitle, scheduleDetail, TaskEvent.TaskScheduleLevel.TITLE, oldTitleScheduleEnum, newTitleScheduleEnum);
        }
    }

    /**
     * 任务进度发生变动
     *
     * @param tplTitle        模板
     * @param scheduleDetail  进度
     * @param oldScheduleEnum 旧进度
     * @param newScheduleEnum 新进度
     */
    private static void scheduleEnumChange(TaskTemplateTitleDTO tplTitle, TaskScheduleDetailDTO scheduleDetail, TaskEvent.TaskScheduleLevel level,
                                           TaskScheduleEnum oldScheduleEnum, TaskScheduleEnum newScheduleEnum) {
        if (!oldScheduleEnum.equals(newScheduleEnum)) {
            EventHelper.asyncSendEvent(TaskEvent.builder()
                    .taskTplTitle(tplTitle)
                    .taskScheduleDetail(scheduleDetail)
                    .taskScheduleLevel(level)
                    .oldSchedule(oldScheduleEnum)
                    .newSchedule(newScheduleEnum)
                    .build());
        }
    }

    /**
     * 初始化任务进度详情
     *
     * @param playerId         玩家ID
     * @param scheduleDetailId 进度详情ID
     * @return 进度详情
     */
    public static TaskScheduleDetailDTO initScheduleDetailById(long playerId, long scheduleDetailId) {
        return TaskCache.getTaskScheduleDetailDTOById(playerId, scheduleDetailId);
    }

    /**
     * 初始化任务总进度
     *
     * @param playerId         玩家ID
     * @param scheduleDetailId 进度详情ID
     * @return 任务总进度
     */
    public static TaskScheduleTitleDTO initScheduleTitleByDetailId(long playerId, long scheduleDetailId) {
        return TaskCache.getTaskScheduleTitleDTOById(playerId, scheduleDetailId);
    }

    /**
     * 完成任务校验
     *
     * @param scheduleDetail 进度详情
     * @param scheduleTitle  总进度
     * @param tplTitle       模板标题
     */
    public static void validateCompleteTask(TaskScheduleDetailDTO scheduleDetail, TaskScheduleTitleDTO scheduleTitle,
                                            TaskTemplateTitleDTO tplTitle) {
        if (Objects.isNull(scheduleDetail)) {
            throw TaskExceptionEnum.NO_NEED_COMPLETE_TASK_SCHEDULE.getException();
        }
        if (!TaskScheduleEnum.canComplete(scheduleDetail.getTaskScheduleType())) {
            throw TaskExceptionEnum.NO_NEED_COMPLETE_TASK_SCHEDULE.getException();
        }
        // 判断是否需要按顺序完成
        if (tplTitle.getChildrenSort()) {
            List<Long> sortedTaskTplIdList = tplTitle.getTemplateList().stream()
                    .sorted(Comparator.comparing(TaskTemplateDTO::getPriority))
                    .map(TaskTemplateDTO::getId).collect(Collectors.toList());
            sortedTaskTplIdList.forEach(tplId -> {
                boolean completed = scheduleTitle.getScheduleList().stream()
                        .filter(schedule -> schedule.getTaskTemplateId().equals(tplId))
                        .map(TaskScheduleDTO::getTaskScheduleEnum)
                        .map(TaskScheduleEnum.COMPLETE::equals)
                        .findFirst().orElse(false);
                boolean completingThisTask = scheduleDetail.getTaskTemplateId().equals(tplId);
                if (!completed && !completingThisTask) {
                    throw TaskExceptionEnum.MUST_COMPLETE_TASK_AS_SORT.getException();
                }
            });
        }
    }

    /**
     * 初始化任务详情
     *
     * @param tplTitle       任务标题
     * @param scheduleDetail 进度详情
     * @return 任务详情
     */
    public static TaskTemplateDetailDTO initTplDetail(TaskTemplateTitleDTO tplTitle, TaskScheduleDetailDTO scheduleDetail) {
        TaskTemplateDTO tplDTO = tplTitle.getTemplateList().stream()
                .filter(tpl -> tpl.getId().equals(scheduleDetail.getTaskTemplateId())).findFirst()
                .orElseThrow(TaskExceptionEnum.TASK_TPL_NOT_EXIST::getException);
        return tplDTO.getDetails().stream()
                .filter(detail -> detail.getId().equals(scheduleDetail.getTaskTemplateDetailId())).findFirst()
                .orElseThrow(TaskExceptionEnum.TASK_TPL_NOT_EXIST::getException);
    }

    /**
     * 更新进度
     *
     * @param scheduleDetail 新的进度详情
     */
    public static void updateTaskSchedule(TaskScheduleDetailDTO scheduleDetail) {
        TaskScheduleDetailService.getInstance().updateSchedule(scheduleDetail);
    }

    /**
     * 完成任务
     *
     * @param tplTitle       任务模板标题
     * @param scheduleDetail 进度详情
     * @param tplDetail      模板详情
     * @param playerId       玩家ID
     */
    public static TaskResultVO completeTask(TaskTemplateTitleDTO tplTitle, TaskScheduleDetailDTO scheduleDetail,
                                            TaskTemplateDetailDTO tplDetail, long playerId) {
        TaskScheduleEnum oldSchedule = scheduleDetail.getTaskScheduleType();
        int completeTimes = scheduleDetail.getCompleteCnt();
        while (completeTimes < scheduleDetail.getTargetCnt()) {
            // 判断是否成功
            boolean success = scheduleDetail.getTarget().getBiFunction().apply(playerId, scheduleDetail.getTargetId());
            if (success) {
                completeTimes++;
                scheduleDetail.setCompleteCnt(completeTimes);
                continue;
            }
            // 如果失败，完成次数不变，不需要更新完成次数
            if (!tplDetail.getAllowFailed()) {
                // 若任务不允许失败，则失败后，直接将任务状态设置为失败
                scheduleDetail.setTaskScheduleType(TaskScheduleEnum.FAILED);
            }
            scheduleEnumChange(tplTitle, scheduleDetail, TaskEvent.TaskScheduleLevel.DETAIL, oldSchedule, scheduleDetail.getTaskScheduleType());
            return TaskResultVO.builder().success(false).msg(tplDetail.getFailMsg()).build();
        }
        scheduleDetail.setTaskScheduleType(TaskScheduleEnum.COMPLETE);
        scheduleEnumChange(tplTitle, scheduleDetail, TaskEvent.TaskScheduleLevel.DETAIL, oldSchedule, scheduleDetail.getTaskScheduleType());
        return TaskResultVO.builder().success(true).msg(tplDetail.getSuccessMsg()).build();
    }
}
