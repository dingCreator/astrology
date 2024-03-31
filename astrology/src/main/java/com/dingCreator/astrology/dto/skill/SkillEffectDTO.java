package com.dingCreator.astrology.dto.skill;

import com.dingCreator.astrology.enums.skill.DamageTypeEnum;
import com.dingCreator.astrology.enums.skill.SkillConditionEnum;
import com.dingCreator.astrology.enums.skill.SkillTargetEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @author ding
 * @date 2024/2/5
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillEffectDTO implements Serializable {
    /**
     * 目标
     */
    private SkillTargetEnum skillTargetEnum;
    /**
     * 伤害类型
     */
    private DamageTypeEnum damageTypeEnum;
    /**
     * 伤害倍率
     */
    private Float damageRate;
    /**
     * buff
     */
    private List<SkillBuffDTO> skillBuffDTOList;
    /**
     * 触发的被动
     */
    private List<Long> inactiveSkillList;

    public SkillEffectDTO(SkillTargetEnum skillTargetEnum, DamageTypeEnum damageTypeEnum, Float damageRate) {
        this.skillTargetEnum = skillTargetEnum;
        this.damageTypeEnum = damageTypeEnum;
        this.damageRate = damageRate;
    }

    public SkillEffectDTO(SkillTargetEnum skillTargetEnum, DamageTypeEnum damageTypeEnum, Float damageRate,
                          SkillBuffDTO skillBuffDTO) {
        this(skillTargetEnum, damageTypeEnum, damageRate);
        this.skillBuffDTOList = Collections.singletonList(skillBuffDTO);
    }

    public SkillEffectDTO(SkillTargetEnum skillTargetEnum, DamageTypeEnum damageTypeEnum, Float damageRate,
                          List<SkillBuffDTO> skillBuffDTOList) {
        this(skillTargetEnum, damageTypeEnum, damageRate);
        this.skillBuffDTOList = skillBuffDTOList;
    }
}
