package com.dingCreator.astrology.mapper;

import com.dingCreator.astrology.entity.SkillBelongTo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author ding
 * @date 2024/2/21
 */
@Mapper
public interface SkillBelongToMapper {

    /**
     * 创建技能归属
     *
     * @param skillBelongTo 实体类
     * @return 是否成功
     */
    @Insert("INSERT INTO astrology_skill_belong_to (belongTo, belongToId, skillId) VALUES (#{belongTo}, #{belongToId}, #{skillId})")
    Boolean createSkillBelongTo(SkillBelongTo skillBelongTo);

    /**
     * 获取技能归属
     *
     * @param belongTo   归属
     * @param belongToId 归属ID
     * @return 技能列表
     */
    @Select("select * from astrology_skill_belong_to where belongTo=#{belongTo} and belongToId=#{belongToId}")
    List<SkillBelongTo> querySkillBelongToList(@Param("belongTo") String belongTo, @Param("belongToId") Long belongToId);

    /**
     * 获取技能归属
     *
     * @param belongTo   归属
     * @param belongToId 归属ID
     * @param skillIds   技能ID
     * @return 技能列表
     */
    @Select({
            "<script>",
            "select * from astrology_skill_belong_to where belongTo=#{belongTo} and belongToId=#{belongToId}",
            "and skillId IN(",
            "<foreach collection='skillIds' item='item' index='index' separator=','>",
            "#{item}",
            "</foreach>",
            ")",
            "</script>"
    })
    List<SkillBelongTo> querySkillBelongToBySkillId(@Param("belongTo") String belongTo, @Param("belongToId") Long belongToId,
                                               @Param("skillIds") List<Long> skillIds);
}
