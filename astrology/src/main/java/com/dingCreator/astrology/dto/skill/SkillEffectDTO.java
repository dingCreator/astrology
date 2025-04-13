package com.dingCreator.astrology.dto.skill;

import com.dingCreator.astrology.enums.skill.DamageTypeEnum;
import com.dingCreator.astrology.enums.skill.SkillTargetEnum;
import com.dingCreator.astrology.util.template.ThisEffectExtraBattleProcessTemplate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

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
    private ThisEffectExtraBattleProcessTemplate template;

    public SkillEffectDTO(SkillTargetEnum skillTargetEnum, DamageTypeEnum damageTypeEnum, Float damageRate) {
        this(skillTargetEnum, damageTypeEnum, damageRate, new ThisEffectExtraBattleProcessTemplate() {

        });
    }
}
