package com.dingCreator.immortal.enums;

import com.dingCreator.immortal.dto.BuffDTO;
import com.dingCreator.immortal.dto.SkillEffectDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author ding
 * @date 2024/2/4
 */
@Getter
@AllArgsConstructor
public enum SkillEnum {

    /**
     * 技能列表
     */
    SKILL_1(1L, "御剑术",
            "",
            JobEnum.XIU_ZHEN.getJobCode(),
            SkillTargetEnum.ANY_ENEMY.getCode(),
            DamageTypeEnum.ATK.getTypeName(),
            true, 1.1F, 0L, 0F, 0,
            Collections.EMPTY_LIST),
    SKILL_2(2L, "霸刀斩",
            "",
            JobEnum.SI_DI_WU_SHI.getJobCode(),
            SkillTargetEnum.ANY_ENEMY.getCode(),
            DamageTypeEnum.ATK.getTypeName(),
            true, 1F, 0L, 0F, 0,
            Collections.EMPTY_LIST),
    SKILL_3(3L, "坠星珠",
            "",
            JobEnum.MAGICIAN.getJobCode(),
            SkillTargetEnum.ANY_ENEMY.getCode(),
            DamageTypeEnum.MAGIC.getTypeName(),
            true, 1F, 0L, 0F, 0,
            Collections.singletonList(
                    new SkillEffectDTO(
                            SkillConditionEnum.NONE.getCondition(),
                            SkillEffectEnum.BUFF.getEffectType(),
                            new BuffDTO(
                                    BuffTypeEnum.HIT_RATE.getName(),
                                    0L, -0.5F, 0.1F, 2
                            )
                    )
            )),
    ;
    /**
     * 技能编号
     */
    private final Long id;
    /**
     * 技能名称
     */
    private final String name;
    /**
     * 技能描述
     */
    private final String desc;
    /**
     * 职业编码
     * 特殊：ALL代表所有职业均可使用
     */
    private final String jobCode;
    /**
     * 技能目标
     */
    private final String target;
    /**
     * 伤害类型
     */
    private final String damageType;
    /**
     * 是否主动技能
     */
    private final Boolean active;
    /**
     * 伤害倍率
     */
    private final Float damageRate;
    /**
     * 耗蓝
     */
    private final Long mp;
    /**
     * 百分比耗蓝
     */
    private final Float mpRate;
    /**
     * 冷却回合数
     */
    private final Integer cd;
    /**
     * 技能效果
     */
    private final List<SkillEffectDTO> skillEffects;
    /**
     * 技能
     */
    private static final Map<Long, SkillEnum> SKILL_ENUM_MAP;

    static {
        SKILL_ENUM_MAP = Arrays.stream(SkillEnum.values()).collect(Collectors.toMap(SkillEnum::getId, Function.identity()));
    }

    public static SkillEnum getById(Long id) {
        return SKILL_ENUM_MAP.get(id);
    }
}
