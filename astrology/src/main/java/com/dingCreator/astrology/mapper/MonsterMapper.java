package com.dingCreator.astrology.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dingCreator.astrology.entity.base.Monster;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author ding
 * @date 2024/3/29
 */
@Mapper
public interface MonsterMapper extends BaseMapper<Monster> {

    /**
     * 获取怪物信息
     *
     * @param id ID
     * @return 怪物信息
     */
    @Select("select * from astrology_monster where id=#{id}")
    Monster getMonsterById(@Param("id") Long id);

    /**
     * 获取怪物列表
     *
     * @param index index
     * @param size  size
     * @return 怪物信息
     */
    @Select("select * from astrology_monster limit #{index},#{size}")
    List<Monster> listMonster(@Param("index") int index, @Param("size") int size);

    /**
     * 获取怪物信息
     *
     * @param list IDs
     * @return 怪物信息
     */
    @Select({"<script>",
            "select * from astrology_monster where id IN(",
            "<foreach collection='list' item='item' index='index' separator=','>",
            "#{item}",
            "</foreach>)",
            "</script>"})
    List<Monster> getMonsterByIds(@Param("list") List<Long> list);

    /**
     * 新建怪物
     *
     * @param monster 怪物信息
     */
    void createMonster(@Param("monster") Monster monster);
}
