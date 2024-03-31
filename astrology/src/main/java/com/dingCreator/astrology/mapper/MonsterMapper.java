package com.dingCreator.astrology.mapper;

import com.dingCreator.astrology.entity.base.Monster;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author ding
 * @date 2024/3/29
 */
@Mapper
public interface MonsterMapper {

    /**
     * 获取怪物信息
     *
     * @param id ID
     * @return 怪物信息
     */
    @Select("select * from astrology_monster where id=#{id}")
    Monster getMonsterById(@Param("id") Long id);

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
}
