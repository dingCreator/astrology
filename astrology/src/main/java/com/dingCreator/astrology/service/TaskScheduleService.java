package com.dingCreator.astrology.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dingCreator.astrology.database.DatabaseProvider;
import com.dingCreator.astrology.entity.TaskSchedule;
import com.dingCreator.astrology.enums.task.TaskScheduleEnum;
import com.dingCreator.astrology.mapper.TaskScheduleMapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author ding
 * @date 2024/8/27
 */
public class TaskScheduleService {

    public static List<TaskSchedule> getInProgressTask(List<Long> playerIdList) {
        return DatabaseProvider.getInstance().executeReturn(sqlSession -> {
            QueryWrapper<TaskSchedule> wrapper = new QueryWrapper<TaskSchedule>()
                    .in(TaskSchedule.PLAYER_ID, playerIdList)
                    .eq(TaskSchedule.TASK_SCHEDULE, TaskScheduleEnum.IN_PROGRESS.getType());
            return sqlSession.getMapper(TaskScheduleMapper.class).selectList(wrapper);
        });
    }

    public static void createTaskSchedule(Long taskTemplateId, List<Long> playerIds) {
        DatabaseProvider.getInstance().execute(sqlSession ->
                playerIds.forEach(playerId -> {
                    TaskSchedule schedule = TaskSchedule.builder().playerId(playerId).taskTemplateId(taskTemplateId)
                            .taskSchedule(TaskScheduleEnum.IN_PROGRESS.getType()).updateTime(LocalDateTime.now()).build();
                    sqlSession.getMapper(TaskScheduleMapper.class).insert(schedule);
                })
        );
    }
}
