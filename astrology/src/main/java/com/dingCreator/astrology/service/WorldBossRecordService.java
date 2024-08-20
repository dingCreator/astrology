package com.dingCreator.astrology.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dingCreator.astrology.database.DatabaseProvider;
import com.dingCreator.astrology.entity.WorldBossRecord;
import com.dingCreator.astrology.mapper.WorldBossRecordMapper;

import java.time.LocalDate;
import java.util.List;

/**
 * @author ding
 * @date 2024/8/7
 */
public class WorldBossRecordService {

    public static void insert(WorldBossRecord worldBossRecord) {
        DatabaseProvider.getInstance().execute(sqlSession ->
                sqlSession.getMapper(WorldBossRecordMapper.class).insert(worldBossRecord));
    }

    public static List<WorldBossRecord> queryTodayWorldBossRecordList() {
        QueryWrapper<WorldBossRecord> wrapper = new QueryWrapper<WorldBossRecord>()
                .ge(WorldBossRecord.CREATE_TIME, LocalDate.now().atStartOfDay())
                .lt(WorldBossRecord.CREATE_TIME, LocalDate.now().plusDays(1).atStartOfDay())
                .orderByDesc(WorldBossRecord.DAMAGE);
        return DatabaseProvider.getInstance().executeReturn(sqlSession ->
                sqlSession.getMapper(WorldBossRecordMapper.class).selectList(wrapper));
    }

    public static Page<WorldBossRecord> queryTodayWorldBossRecordPage(int pageIndex, int pageSize) {
        Page<WorldBossRecord> page = new Page<>(pageIndex, pageSize);
        QueryWrapper<WorldBossRecord> wrapper = new QueryWrapper<WorldBossRecord>()
                .ge(WorldBossRecord.CREATE_TIME, LocalDate.now().atStartOfDay())
                .lt(WorldBossRecord.CREATE_TIME, LocalDate.now().plusDays(1).atStartOfDay())
                .orderByDesc(WorldBossRecord.DAMAGE);
        DatabaseProvider.getInstance().execute(sqlSession ->
                sqlSession.getMapper(WorldBossRecordMapper.class).selectPage(page, wrapper));
        return page;
    }
}
