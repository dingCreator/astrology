package com.dingCreator.astrology.mapper;

import com.dingCreator.astrology.entity.SkillBarItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author ding
 * @date 2024/2/21
 */
@Mapper
public interface SkillBarItemMapper {

    /**
     * 根据归属和归属ID查询技能栏信息
     *
     * @param belongTo 归属
     * @param belongToId 归属ID
     * @return 技能栏信息
     */
    @Select("select * from astrology where belongTo=#{belongTo} and belongToId=#{belongToId}")
    List<SkillBarItem> querySkillBarItemByBelongToId(@Param("belongTo") String belongTo, @Param("belongToId") Long belongToId);
}
