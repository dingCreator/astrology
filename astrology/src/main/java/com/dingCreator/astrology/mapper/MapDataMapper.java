package com.dingCreator.astrology.mapper;

import com.dingCreator.astrology.entity.Map;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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
    @Select("")
    Map getMapById(Long id);
}
