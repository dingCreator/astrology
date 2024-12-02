package com.dingCreator.astrology.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
    public Monster getMonsterById(Long id) {
        return DatabaseProvider.getInstance().executeReturn(sqlSession -> sqlSession
                .getMapper(MonsterMapper.class).getMonsterById(id));
    }

    /**
     * 通过怪物名称获取怪物信息
     *
     * @param name 怪物名
     * @return 怪物信息
     */
    public List<Monster> listMonsterByName(String name) {
        return DatabaseProvider.getInstance().executeReturn(sqlSession -> {
            QueryWrapper<Monster> wrapper = new QueryWrapper<Monster>().eq(Monster.NAME, name);
            return sqlSession.getMapper(MonsterMapper.class).selectList(wrapper);
        });
    }

    /**
     * 查询怪物
     *
     * @param index 下标
     * @param size  尺寸
     * @return 怪物列表
     */
    public List<Monster> listMonster(int index, int size) {
        return DatabaseProvider.getInstance().executeReturn(sqlSession -> sqlSession.getMapper(MonsterMapper.class)
                .listMonster(index - 1, size));
    }

    /**
     * 新建怪物信息
     *
     * @param monster 怪物信息
     */
    public long createMonster(Monster monster) {
        DatabaseProvider.getInstance().execute(sqlSession -> sqlSession.getMapper(MonsterMapper.class).insert(monster));
        return monster.getId();
    }

    /**
     * 更新怪物血量
     *
     * @param id 怪物id
     * @param hp 怪物血量
     */
    public void updateHpById(long id, long hp) {
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
    public void updateMonster(Monster monster) {
        DatabaseProvider.getInstance().execute(sqlSession -> sqlSession.getMapper(MonsterMapper.class).updateById(monster));
    }

    /**
     * 通过怪物ID获取怪物信息
     *
     * @param ids IDs
     * @return 怪物信息
     */
    public List<Monster> getMonsterByIds(List<Long> ids) {
        return getMonsterByIds(ids, true);
    }

    /**
     * 通过怪物ID获取怪物信息
     *
     * @param ids        IDs
     * @param fullStatus 是否满状态
     * @return 怪物信息
     */
    public List<Monster> getMonsterByIds(List<Long> ids, boolean fullStatus) {
        List<Monster> monsterList = DatabaseProvider.getInstance().executeReturn(sqlSession -> sqlSession
                .getMapper(MonsterMapper.class).getMonsterByIds(ids));
        if (fullStatus) {
            monsterList.forEach(monster -> {
                monster.setHp(monster.getMaxHp());
                monster.setMp(monster.getMaxMp());
            });
        }
        return monsterList;
    }

    public int count() {
        return DatabaseProvider.getInstance().executeReturn(sqlSession ->
            sqlSession.getMapper(MonsterMapper.class).selectCount(new QueryWrapper<>())
        );
    }


    private static class Holder {
        private static final MonsterService SERVICE = new MonsterService();
    }

    private MonsterService() {

    }

    public static MonsterService getInstance() {
        return MonsterService.Holder.SERVICE;
    }
}
