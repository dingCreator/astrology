package com.dingCreator.astrology.service;

import com.dingCreator.astrology.database.DatabaseProvider;
import com.dingCreator.astrology.entity.base.Monster;
import com.dingCreator.astrology.mapper.MonsterMapper;

import java.util.List;

/**
 * @author ding
 * @date 2024/3/29
 */
public class MonsterService {

    /**
     * 通过怪物ID获取怪物信息
     *
     * @param id ID
     * @return 怪物信息
     */
    public static Monster getMonsterById(Long id) {
        return (Monster) DatabaseProvider.getInstance().doExecute(sqlSession -> sqlSession
                .getMapper(MonsterMapper.class).getMonsterById(id));
    }

    /**
     * 通过怪物ID获取怪物信息
     *
     * @param ids IDs
     * @return 怪物信息
     */
    public static List<Monster> getMonsterByIds(List<Long> ids) {
        return (List<Monster>) DatabaseProvider.getInstance().doExecute(sqlSession -> sqlSession
                .getMapper(MonsterMapper.class).getMonsterByIds(ids));
    }
}
