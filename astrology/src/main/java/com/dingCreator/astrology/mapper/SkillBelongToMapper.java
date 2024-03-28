package com.dingCreator.astrology.mapper;

import com.dingCreator.astrology.entity.SkillBelongTo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

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
}
