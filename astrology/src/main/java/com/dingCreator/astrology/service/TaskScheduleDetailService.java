package com.dingCreator.astrology.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dingCreator.astrology.database.DatabaseProvider;
import com.dingCreator.astrology.dto.task.TaskScheduleDetailDTO;
import com.dingCreator.astrology.entity.TaskScheduleDetail;
import com.dingCreator.astrology.mapper.TaskScheduleDetailMapper;

import java.util.List;

/**
 * @author ding
 * @date 2024/8/30
 */
public class TaskScheduleDetailService {

    /**
     * 创建任务进度详情记录
     *
     * @param detailList 任务进度详情
     */
    public static void createTaskScheduleDetail(List<TaskScheduleDetail> detailList) {
        DatabaseProvider.getInstance().execute(sqlSession -> detailList.forEach(detail ->
                        sqlSession.getMapper(TaskScheduleDetailMapper.class).insert(detail)
                )
        );
    }

    /**
     * 根据玩家ID获取任务进度详情
     *
     * @param playerId 玩家ID
     * @return 任务进度详情
     */
    public static List<TaskScheduleDetail> listTaskScheduleDetail(Long playerId) {
        return DatabaseProvider.getInstance().executeReturn(sqlSession -> {
            QueryWrapper<TaskScheduleDetail> wrapper = new QueryWrapper<TaskScheduleDetail>()
                    .eq(TaskScheduleDetail.PLAYER_ID, playerId);
            return sqlSession.getMapper(TaskScheduleDetailMapper.class).selectList(wrapper);
        });
    }

    /**
     * 只更新任务进度和完成数量
     *
     * @param detailDTO 进度详情
     */
    public static void updateSchedule(TaskScheduleDetailDTO detailDTO) {
        TaskScheduleDetail detail = TaskScheduleDetail.builder()
                .id(detailDTO.getId())
                .completeCnt(detailDTO.getCompleteCnt())
                .taskScheduleType(detailDTO.getTaskScheduleType().getType())
                .build();
        DatabaseProvider.getInstance().execute(sqlSession -> sqlSession.getMapper(TaskScheduleDetailMapper.class).updateById(detail));
    }
}
