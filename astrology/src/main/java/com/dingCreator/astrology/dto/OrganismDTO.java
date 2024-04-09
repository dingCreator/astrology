package com.dingCreator.astrology.dto;

import com.dingCreator.astrology.dto.skill.SkillBarDTO;
import com.dingCreator.astrology.entity.base.Organism;
import com.dingCreator.astrology.enums.skill.SkillEnum;
import com.dingCreator.astrology.template.ExtraBattleProcessTemplate;
import lombok.Data;

import java.io.Serializable;

/**
 * 生物
 *
 * @author ding
 * @date 2024/2/1
 */
@Data
public class OrganismDTO implements Serializable, ExtraBattleProcessTemplate {
    /**
     * 生物属性
     */
    private Organism organism;
    /**
     * 技能组
     */
    private SkillBarDTO skillBarDTO;
    /**
     * 默认技能
     */
    private SkillEnum defaultSkill;
    /**
     * 装备
     */
    private EquipmentBarDTO equipmentBarDTO;
}
