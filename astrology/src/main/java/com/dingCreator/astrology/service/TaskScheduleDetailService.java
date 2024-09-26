package com.dingCreator.astrology.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dingCreator.astrology.database.DatabaseProvider;
import com.dingCreator.astrology.entity.TaskScheduleDetail;
import com.dingCreator.astrology.mapper.TaskScheduleDetailMapper;

import java.sql.Wrapper;
import java.util.List;

/**
 * @author ding
 * @date 2024/8/30
 */
public class TaskScheduleDetailService {

    public static void createTaskScheduleDetail(List<TaskScheduleDetail> detailList) {
        DatabaseProvider.getInstance().execute(sqlSession -> detailList.forEach(detail ->
                        sqlSession.getMapper(TaskScheduleDetailMapper.class).insert(detail)
                )
        );
    }

    public static List<TaskScheduleDetail> listTaskScheduleDetail(Long playerId) {
        return DatabaseProvider.getInstance().executeReturn(sqlSession -> {
            QueryWrapper<TaskScheduleDetail> wrapper = new QueryWrapper<TaskScheduleDetail>()
                    .eq(TaskScheduleDetail.PLAYER_ID, playerId);
            return sqlSession.getMapper(TaskScheduleDetailMapper.class).selectList(wrapper);
        });
    }
}
