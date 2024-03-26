package com.dingCreator.astrology.dto;

import com.dingCreator.astrology.enums.skill.SkillEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 战斗包装类，用于存储一些中间过程
 * 血量/蓝量这一类要持久化的则直接修改生物属性内的值
 *
 * @author ding
 * @date 2024/2/2
 */
@Data
public class BattleDTO implements Serializable {
    /**
     * 生物属性
     */
    private OrganismDTO organismDTO;
    /**
     * 行动值
     */
    private Long behavior;
    /**
     * mp不足时的默认技能
     */
    private Long defaultSkillId;
    /**
     * 获得的buff
     */
    private Map<String, List<BattleBuffDTO>> buffMap;
    /**
     * 装备
     */
    private EquipmentBarDTO equipmentBarDTO;
    /**
     * 轮次
     */
    private Long round;

    /**
     * 设置默认技能
     * @param id 技能id
     */
    public void setDefaultSkillId(Long id) {
        SkillEnum skillEnum;
        if (Objects.isNull(skillEnum = SkillEnum.getById(id))) {
            throw new IllegalArgumentException("技能id对应的技能不存在");
        }
        if (!skillEnum.getActive()) {
            throw new IllegalArgumentException("不能设置被动技能为默认技能");
        }
        if (skillEnum.getMp() > 0 || skillEnum.getMpRate() > 0F) {
            throw new IllegalArgumentException("不能设置耗蓝技能为默认技能");
        }
        this.defaultSkillId = id;
    }
}
