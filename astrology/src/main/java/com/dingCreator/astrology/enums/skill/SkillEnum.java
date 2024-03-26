package com.dingCreator.astrology.enums.skill;

import com.dingCreator.astrology.dto.BuffDTO;
import com.dingCreator.astrology.dto.skill.SkillBuffDTO;
import com.dingCreator.astrology.dto.skill.SkillEffectDTO;
import com.dingCreator.astrology.enums.BuffTypeEnum;
import com.dingCreator.astrology.enums.job.JobEnum;
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
            "修真者使用仙剑进行攻击，对敌方造成90%物理伤害",
            Collections.singletonList(JobEnum.XIU_ZHEN.getJobCode()),
            true, 0L, 0F, 0,
            Collections.singletonList(new SkillEffectDTO(
                    SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 0.9F
            ))),

    SKILL_2(2L, "顺劈",
            "死地武士挥动武士刀进行劈砍，对敌方造成120%物理伤害",
            Collections.singletonList(JobEnum.SI_DI_WU_SHI.getJobCode()),
            true, 0L, 0F, 0,
            Collections.singletonList(new SkillEffectDTO(
                    SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 1.2F
            ))),

    SKILL_3(3L, "唤星术",
            "星星具有蛊惑人的力量，星术师发动唤星术造成100%法术伤害的同时，有10%概率使敌方失神。失神（降低敌方50%命中率持续一回合） ",
            Collections.singletonList(JobEnum.MAGICIAN.getJobCode()),
            true, 0L, 0F, 0,
            Collections.singletonList(
                    new SkillEffectDTO(
                            SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.MAGIC, 1F,
                            Collections.singletonList(
                                    new SkillBuffDTO(
                                            new BuffDTO(BuffTypeEnum.HIT_RATE.getName(), 0L, -0.5F
                                            ), 0.1F, 1)
                            )
                    )
            )
    ),

    SKILL_4(4L, "速射",
            "枪炮师引动枪弹进行五次射击，每次射击造成50%物理伤害，同时该技能每次伤害命中均能触发枪炮师伪神之眼效果。",
            Collections.singletonList(JobEnum.GUN.getJobCode()),
            true, 0L, 0F, 0,
            Arrays.asList(
                    new SkillEffectDTO(
                            SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 0.5F
                    ),
                    new SkillEffectDTO(
                            SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 0.5F
                    ),
                    new SkillEffectDTO(
                            SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 0.5F
                    ),
                    new SkillEffectDTO(
                            SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 0.5F
                    ),
                    new SkillEffectDTO(
                            SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 0.5F
                    )
            )
    ),

    SKILL_5(5L, "汲魂之隙",
            "造成100%物理伤害同时从中汲取生命力，使此攻击附带12%物理吸血，每次攻击造成伤害时使敌方防御降低1%持续一回合，每使用一次该防御降低效果增加1%，最多不超过50%。",
            Collections.singletonList(JobEnum.EVIL.getJobCode()),
            true, 0L, 0F, 0,
            Collections.singletonList(
                    new SkillEffectDTO(
                            SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 1F
                    )
            )
    ),

    SKILL_6(6L, "芬芳",
            "小嘴抹蜜口吐芬芳，对敌方全体造成80%魔法伤害并且有10%概率嘲讽对方1回合",
            Collections.singletonList(JobEnum.CHEATER.getJobCode()),
            true, 0L, 0F, 0,
            Arrays.asList(
                    new SkillEffectDTO(SkillTargetEnum.ALL_ENEMY, DamageTypeEnum.MAGIC, 0.8F),
                    new SkillEffectDTO(SkillTargetEnum.ME, DamageTypeEnum.MAGIC, 0F,
                            Collections.singletonList(
                                    new SkillBuffDTO(
                                            new BuffDTO(BuffTypeEnum.TAUNT.getName(), 0L, 0F),
                                            0.1F, 1)))
            )
    )
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
     * 限定职业编码
     * 特殊：ALL代表所有职业均可使用
     */
    private final List<String> jobCode;
    /**
     * 是否主动技能
     */
    private final Boolean active;
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
     * 空集合
     */
    private static final List<?> EMPTY = Collections.EMPTY_LIST;
    /**
     * 技能
     */
    private static final Map<Long, SkillEnum> SKILL_ENUM_MAP;

    private static final Map<String, Long> DEFAULT_SKILL_MAP = new HashMap<>();

    static {
        SKILL_ENUM_MAP = Arrays.stream(SkillEnum.values()).collect(Collectors.toMap(SkillEnum::getId, Function.identity()));
        DEFAULT_SKILL_MAP.put(JobEnum.XIU_ZHEN.getJobCode(), 1L);
        DEFAULT_SKILL_MAP.put(JobEnum.SI_DI_WU_SHI.getJobCode(), 2L);
        DEFAULT_SKILL_MAP.put(JobEnum.MAGICIAN.getJobCode(), 3L);
        DEFAULT_SKILL_MAP.put(JobEnum.GUN.getJobCode(), 4L);
        DEFAULT_SKILL_MAP.put(JobEnum.EVIL.getJobCode(), 5L);
        DEFAULT_SKILL_MAP.put(JobEnum.CHEATER.getJobCode(), 6L);
    }

    public static SkillEnum getById(Long id) {
        return SKILL_ENUM_MAP.get(id);
    }

    public static SkillEnum getDefaultSkillByJob(String jobCode) {
        return getById(DEFAULT_SKILL_MAP.get(jobCode));
    }
}
