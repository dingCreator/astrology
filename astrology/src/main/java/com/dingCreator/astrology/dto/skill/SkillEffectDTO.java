package com.dingCreator.astrology.dto.skill;

import com.dingCreator.astrology.enums.skill.DamageTypeEnum;
import com.dingCreator.astrology.enums.skill.TargetEnum;
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
    private TargetEnum targetEnum;
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

    public SkillEffectDTO(TargetEnum targetEnum){
        this(targetEnum, DamageTypeEnum.ATK, 0F);
    }

    public SkillEffectDTO(TargetEnum targetEnum, ThisEffectExtraBattleProcessTemplate template){
        this(targetEnum, DamageTypeEnum.ATK, 0F, template);
    }

    public SkillEffectDTO(TargetEnum targetEnum, DamageTypeEnum damageTypeEnum, Float damageRate) {
        this(targetEnum, damageTypeEnum, damageRate, new ThisEffectExtraBattleProcessTemplate() {

        });
    }
}
