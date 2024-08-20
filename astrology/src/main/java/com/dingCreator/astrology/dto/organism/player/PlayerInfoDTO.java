package com.dingCreator.astrology.dto.organism.player;

import com.dingCreator.astrology.dto.equipment.EquipmentBarDTO;
import com.dingCreator.astrology.dto.skill.SkillBarDTO;
import com.dingCreator.astrology.enums.TitleEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author ding
 * @date 2023/4/18
 */
@Data
public class PlayerInfoDTO implements Serializable {
    /**
     * 玩家基础属性
     */
    private PlayerDTO playerDTO;
    /**
     * 是否在小队中
     */
    private Boolean team;
    /**
     * 技能栏
     */
    private SkillBarDTO skillBarDTO;
    /**
     * 装备栏
     */
    private EquipmentBarDTO equipmentBarDTO;
    /**
     * 称号
     */
    private List<TitleEnum> titleList;
}
