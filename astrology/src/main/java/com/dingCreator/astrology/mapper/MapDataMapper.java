package com.dingCreator.astrology.mapper;

import com.dingCreator.astrology.entity.Map;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author ding
 * @date 2024/2/20
 */
@Mapper
public interface MapDataMapper {

    /**
     * 根据地图ID获取地图信息
     *
     * @param id 地图ID
     * @return 地图信息
     */
    @Select("select * from astrology_map where id=#{id}")
    Map getMapById(@Param("id") Long id);

    /**
     * 根据地图名称获取地图信息
     *
     * @param name 地图名称
     * @return 地图信息
     */
    @Select("select * from astrology_map where `name`=#{name}")
    Map getMapByName(@Param("name") String name);

    /**
     * 查询地图列表
     *
     * @return 地图列表
     */
    @Select("select * from astrology_map")
    List<Map> listMap();
}
