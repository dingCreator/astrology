package com.dingCreator.immortal.dto;

import com.dingCreator.immortal.entity.Equipment;
import com.dingCreator.immortal.entity.SkillBarItem;
import com.dingCreator.immortal.entity.base.Organism;
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
