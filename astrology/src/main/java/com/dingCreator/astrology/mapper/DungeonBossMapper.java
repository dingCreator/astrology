package com.dingCreator.astrology.mapper;

import com.dingCreator.astrology.entity.DungeonBoss;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author ding
 * @date 2024/4/7
 */
@Mapper
public interface DungeonBossMapper {

    /**
     * 根据ID获取boss
     *
     * @param id ID
     * @return boss
     */
    @Select("select * from astrology_dungeon_boss where id=#{id}")
    DungeonBoss getById(@Param("id") Long id);

    /**
     * 根据副本ID获取boss
     *
     * @param dungeonId 副本ID
     * @return boss信息
     */
    @Select("select * from astrology_dungeon_boss where dungeonId=#{dungeonId}")
    List<DungeonBoss> getByDungeonId(@Param("dungeonId") Long dungeonId);
}
