package com.dingCreator.astrology.util.ability;

import cn.hutool.core.collection.CollectionUtil;
import com.dingCreator.astrology.behavior.PlayerBehavior;
import com.dingCreator.astrology.behavior.TeamBehavior;
import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.cache.TaskCache;
import com.dingCreator.astrology.cache.TeamCache;
import com.dingCreator.astrology.dto.organism.player.PlayerDTO;
import com.dingCreator.astrology.dto.organism.player.PlayerInfoDTO;
import com.dingCreator.astrology.dto.task.PeakTaskDTO;
import com.dingCreator.astrology.dto.task.TaskScheduleTitleDTO;
import com.dingCreator.astrology.dto.task.TaskTemplateTitleDTO;
import com.dingCreator.astrology.enums.PlayerStatusEnum;
import com.dingCreator.astrology.enums.RankEnum;
import com.dingCreator.astrology.enums.exception.TaskExceptionEnum;
import com.dingCreator.astrology.enums.task.TaskScheduleEnum;
import com.dingCreator.astrology.service.PeakTaskTemplateService;
import com.dingCreator.astrology.service.TaskScheduleDetailService;

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
            if (PlayerBehavior.getInstance().getStatus(teamPlayer).equals(PlayerStatusEnum.MOVING.getCode())) {
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
        PeakTaskDTO task = PeakTaskTemplateService.getPeakTaskDTO(playerDTO.getJob(), playerDTO.getRank());
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

    public static void validatePeakTask(PlayerInfoDTO playerInfo) {
        PlayerDTO player = playerInfo.getPlayerDTO();
        RankEnum rankEnum = RankEnum.getEnum(player.getJob(), player.getRank());
        if (player.getLevel() < rankEnum.getMaxLevel()) {
            throw TaskExceptionEnum.PEAK_LOW_LEVEL.getException();
        }
    }

    /**
     * 创建任务进度
     */
    public static void createSchedule(List<PlayerInfoDTO> teamPlayers, TaskTemplateTitleDTO title) {
        List<Long> teamPlayerIds = teamPlayers.stream().map(info -> info.getPlayerDTO().getId()).collect(Collectors.toList());
    }
}
