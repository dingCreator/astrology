package com.dingCreator.astrology.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dingCreator.astrology.entity.Dungeon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author ding
 * @date 2024/4/4
 */
@Mapper
public interface DungeonMapper extends BaseMapper<Dungeon> {

    /**
     * 根据ID获取副本
     *
     * @param id 副本ID
     * @return 副本
     */
    @Select("select * from astrology_dungeon where id=#{id}")
    Dungeon getById(@Param("id") Long id);

    /**
     * 根据副本名称和地图ID获取副本
     *
     * @param name  副本名称
     * @return 副本
     */
    @Select("select * from astrology_dungeon where `name`=#{name}")
    Dungeon getByName(@Param("name") String name);

    /**
     * 获取副本
     *
     * @return 副本列表
     */
    @Select("select * from astrology_dungeon")
    List<Dungeon> list();
}
