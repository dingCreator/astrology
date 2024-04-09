package com.dingCreator.astrology.mapper;

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
public interface DungeonMapper {

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
     * @param mapId 地图ID
     * @param name  副本名称
     * @return 副本
     */
    @Select("select * from astrology_dungeon where mapId=#{mapId} and name=#{name}")
    Dungeon getByName(@Param("mapId") Long mapId, @Param("name") String name);

    /**
     * 根据地图ID获取副本
     *
     * @param mapId 地图ID
     * @return 副本列表
     */
    @Select("select * from astrology_dungeon where mapId=#{mapId}")
    List<Dungeon> list(@Param("mapId") Long mapId);
}
