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
    @Select("select * from astrology_skill_bar_item where belongTo=#{belongTo} and belongToId=#{belongToId}")
    List<SkillBarItem> querySkillBarItemByBelongToId(@Param("belongTo") String belongTo, @Param("belongToId") Long belongToId);

    /**
     * 插入新技能栏信息
     *
     * @param skillBarItemList 技能栏信息
     */
    @Insert({
            "<script>",
            "INSERT INTO astrology_skill_bar_item (id, belong_to, belong_to_id, skill_id, head_id, next_id) VALUES",
            "<foreach collection='skillBarItemList' item='item' index='index' separator=','>",
            "(#{item.id}, #{item.belongTo}, #{item.belongToId}, #{item.skillId}, #{item.headId}, #{item.nextId})",
            "</foreach>",
            "</script>"
    })
    Integer insertSkillBarItem(List<SkillBarItem> skillBarItemList);

    /**
     * 删除技能栏信息
     *
     * @param belongTo   归属
     * @param belongToId 归属ID
     */
    @Delete("delete from astrology_skill_bar_item where belongTo=#{belongTo} and belongToId=#{belongToId}")
    void deleteSkillBarItem(@Param("belongTo") String belongTo, @Param("belongToId") Long belongToId);
}
