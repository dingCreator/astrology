package com.dingCreator.astrology.service;

import com.dingCreator.astrology.database.DatabaseProvider;
import com.dingCreator.astrology.entity.TaskScheduleDetail;
import com.dingCreator.astrology.mapper.TaskScheduleDetailMapper;

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
}
