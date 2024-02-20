package com.dingCreator.astrology.dto;

import com.dingCreator.astrology.entity.Equipment;
import com.dingCreator.astrology.entity.base.Organism;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 生物
 *
 * @author ding
 * @date 2024/2/1
 */
@Data
public class OrganismDTO implements Serializable {
    /**
     * 生物属性
     */
    private Organism organism;
    /**
     * 技能组
     */
    private SkillBarDTO skillBarDTO;
    /**
     * 装备
     */
    private List<Equipment> equipmentList;
}
