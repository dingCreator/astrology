package com.dingCreator.astrology.service;

import com.dingCreator.astrology.database.DatabaseProvider;
import com.dingCreator.astrology.entity.WorldBoss;
import com.dingCreator.astrology.entity.base.Monster;
import com.dingCreator.astrology.mapper.MonsterMapper;
import com.dingCreator.astrology.mapper.WorldBossMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Objects;

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
        return DatabaseProvider.getInstance().executeReturn(sqlSession -> sqlSession
                .getMapper(MonsterMapper.class).getMonsterById(id));
    }

    /**
     * 查询怪物
     *
     * @param index 下标
     * @param size  尺寸
     * @return 怪物列表
     */
    public static List<Monster> listMonster(int index, int size) {
        return DatabaseProvider.getInstance().executeReturn(sqlSession -> sqlSession.getMapper(MonsterMapper.class)
                .listMonster(index + 1, size));
    }

    /**
     * 新建怪物信息
     *
     * @param monster 怪物信息
     */
    public static void createMonster(Monster monster) {
        DatabaseProvider.getInstance().execute(sqlSession -> sqlSession.getMapper(MonsterMapper.class)
                .createMonster(monster));
    }

    /**
     * 更新怪物血量
     *
     * @param id 怪物id
     * @param hp 怪物血量
     */
    public static void updateHpById(long id, long hp) {
        Monster monster = new Monster();
        monster.setId(id);
        monster.setHp(hp);
        DatabaseProvider.getInstance().execute(sqlSession -> sqlSession.getMapper(MonsterMapper.class).updateById(monster));
    }


    /**
     * 编辑怪物信息
     *
     * @param monster 怪物信息
     */
    public static void updateMonster(Monster monster) {
        DatabaseProvider.getInstance().execute(sqlSession -> sqlSession.getMapper(MonsterMapper.class).updateById(monster));
    }

    /**
     * 通过怪物ID获取怪物信息
     *
     * @param ids IDs
     * @return 怪物信息
     */
    public static List<Monster> getMonsterByIds(List<Long> ids) {
        return DatabaseProvider.getInstance().executeReturn(sqlSession -> sqlSession
                .getMapper(MonsterMapper.class).getMonsterByIds(ids));
    }
}
