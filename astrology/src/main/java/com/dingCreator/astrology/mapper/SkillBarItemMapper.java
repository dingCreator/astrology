package com.dingCreator.astrology.mapper;

import com.dingCreator.astrology.entity.SkillBarItem;
import org.apache.ibatis.annotations.*;

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
     * @param belongTo   归属
     * @param belongToId 归属ID
     * @return 技能栏信息
     */
    @Select("select * from astrology_skill_bar_item where belong_to=#{belongTo} and belong_to_id=#{belongToId}")
    SkillBarItem querySkillBarItemByBelongToId(@Param("belongTo") String belongTo, @Param("belongToId") Long belongToId);

    /**
     * 插入新技能栏信息
     *
     * @param skillBarItem 技能栏信息
     * @return int
     */
    @Insert("INSERT INTO astrology_skill_bar_item (belong_to, belong_to_id, skill_id) VALUES"
            + "(#{belongTo}, #{belongToId}, #{skillId})")
    void insertSkillBarItem(SkillBarItem skillBarItem);

    /**
     * 删除技能栏信息
     *
     * @param belongTo   归属
     * @param belongToId 归属ID
     */
    @Delete("delete from astrology_skill_bar_item where belong_to=#{belongTo} and belong_to_id=#{belongToId}")
    void deleteSkillBarItem(@Param("belongTo") String belongTo, @Param("belongToId") Long belongToId);
}
