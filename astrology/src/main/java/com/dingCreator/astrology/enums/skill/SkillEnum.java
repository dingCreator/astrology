package com.dingCreator.astrology.enums.skill;

import com.dingCreator.astrology.dto.GiveBuffDTO;
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
            JobEnum.XIU_ZHEN.getJobCode(),
            new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 0.9F)),

    SKILL_2(2L, "顺劈",
            "死地武士挥动武士刀进行劈砍，对敌方造成120%物理伤害",
            JobEnum.SI_DI_WU_SHI.getJobCode(),
            new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 1.2F)),

    SKILL_3(3L, "唤星术",
            "星星具有蛊惑人的力量，星术师发动唤星术造成100%法术伤害的同时，有10%概率降低敌方50%命中值，持续一回合",
            JobEnum.MAGICIAN.getJobCode(),
            new SkillEffectDTO(
                    SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.MAGIC, 1F,
                    new GiveBuffDTO(BuffTypeEnum.HIT, 0L, -0.5F, 1, 0.1F)
            )
    ),

    SKILL_4(4L, "速射",
            "枪炮师引动枪弹进行五次射击，每次射击造成50%物理伤害，同时该技能每次伤害命中均能触发枪炮师伪神之眼效果。",
            JobEnum.GUN.getJobCode(),
            Collections.nCopies(5, new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 0.5F))
    ),

    SKILL_5(5L, "汲魂之隙",
            "造成100%物理伤害同时从中汲取生命力，使此攻击附带12%物理吸血，每次攻击造成伤害时使敌方防御降低1%持续一回合，" +
                    "每使用一次该防御降低效果增加1%，最多不超过50%。",
            JobEnum.EVIL.getJobCode(), new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 1F)
    ),

    SKILL_6(6L, "芬芳",
            "小嘴抹蜜口吐芬芳，对敌方全体造成80%物理伤害并且有10%概率嘲讽对方1回合",
            JobEnum.CHEATER.getJobCode(),
            Arrays.asList(
                    new SkillEffectDTO(SkillTargetEnum.ALL_ENEMY, DamageTypeEnum.ATK, 0.8F),
                    new SkillEffectDTO(SkillTargetEnum.ME, DamageTypeEnum.ATK, 0F,
                            new GiveBuffDTO(BuffTypeEnum.TAUNT, 0L, 0F, 1, 0.1F)
                    )
            )
    ),

    SKILL_7(7L, "星术师技能1",
            "",
            JobEnum.MAGICIAN.getJobCode(), 1000L,
            new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.MAGIC, 2F)
    ),

    SKILL_1000(1000L, "雷击",
            "天劫的基础手段，造成100%物理伤害",
            "None", new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 1F)
    ),

    SKILL_1001(1001L, "天雷化身-剑",
            "天劫化身为剑，造成50%物理伤害，并提升5%物攻，持续5回合",
            "None", 100L,
            Arrays.asList(
                    new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 0.5F),
                    new SkillEffectDTO(SkillTargetEnum.ME, DamageTypeEnum.ATK, 0F,
                            new GiveBuffDTO(BuffTypeEnum.ATK, 0L, 0.05F, 5, 1F)
                    )
            )
    ),

    SKILL_1002(1002L, "天威",
            "天劫释放天威，降低对方10%物攻和10%法攻，持续5回合",
            "None", 500L,
            new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 0F,
                    Arrays.asList(
                            new GiveBuffDTO(BuffTypeEnum.ATK, 0L, -0.1F, 5, 1F),
                            new GiveBuffDTO(BuffTypeEnum.MAGIC_ATK, 0L, -0.1F, 5, 1F)
                    )
            )
    ),

    SKILL_1003(1003L, "五雷轰顶",
            "天劫释放五道天雷，分别造成100%，150%，200%，300%，600%物理伤害",
            "None", 10000L,
            Arrays.asList(
                new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 1F),
                new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 1.5F),
                new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 2F),
                new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 3F),
                new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 6F)
            )
    ),

    SKILL_10000(10000L, "作弊",
            "开发者的作弊技能，造成1000%的物理伤害，并提升100000物理攻击力10回合",
            "All", 0L,
            Arrays.asList(
                    new SkillEffectDTO(SkillTargetEnum.ALL_ENEMY, DamageTypeEnum.ATK, 10F),
                    new SkillEffectDTO(SkillTargetEnum.ME, DamageTypeEnum.ATK, 0F,
                            new GiveBuffDTO(BuffTypeEnum.ATK, 100000L, 0F, 10, 1F)
                    )
            )
    ),

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
     * All代表所有职业均可使用
     * None代表所有职业均不可使用
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
     * 技能
     */
    private static final Map<Long, SkillEnum> SKILL_ID_MAP;
    /**
     * 技能
     */
    private static final Map<String, SkillEnum> SKILL_NAME_MAP;

    private static final Map<String, Long> DEFAULT_SKILL_MAP = new HashMap<>();

    static {
        SKILL_ID_MAP = Arrays.stream(SkillEnum.values()).collect(Collectors.toMap(SkillEnum::getId, Function.identity()));
        SKILL_NAME_MAP = Arrays.stream(SkillEnum.values()).collect(Collectors.toMap(SkillEnum::getName, Function.identity()));

        DEFAULT_SKILL_MAP.put(JobEnum.XIU_ZHEN.getJobCode(), 1L);
        DEFAULT_SKILL_MAP.put(JobEnum.SI_DI_WU_SHI.getJobCode(), 2L);
        DEFAULT_SKILL_MAP.put(JobEnum.MAGICIAN.getJobCode(), 3L);
        DEFAULT_SKILL_MAP.put(JobEnum.GUN.getJobCode(), 4L);
        DEFAULT_SKILL_MAP.put(JobEnum.EVIL.getJobCode(), 5L);
        DEFAULT_SKILL_MAP.put(JobEnum.CHEATER.getJobCode(), 6L);
    }

    public static SkillEnum getById(Long id) {
        return SKILL_ID_MAP.get(id);
    }

    public static SkillEnum getByName(String name) {
        return SKILL_NAME_MAP.get(name);
    }

    public static SkillEnum getDefaultSkillByJob(String jobCode) {
        return getById(DEFAULT_SKILL_MAP.get(jobCode));
    }

    SkillEnum(Long id, String name, String desc, String jobCode, SkillEffectDTO skillEffect) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.jobCode = Collections.singletonList(jobCode);
        this.active = true;
        this.mp = 0L;
        this.mpRate = 0F;
        this.cd = 0;
        this.skillEffects = Collections.singletonList(skillEffect);
    }

    SkillEnum(Long id, String name, String desc, String jobCode, List<SkillEffectDTO> skillEffects) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.jobCode = Collections.singletonList(jobCode);
        this.active = true;
        this.mp = 0L;
        this.mpRate = 0F;
        this.cd = 0;
        this.skillEffects = skillEffects;
    }

    SkillEnum(Long id, String name, String desc, String jobCode, Long mp, SkillEffectDTO skillEffect) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.jobCode = Collections.singletonList(jobCode);
        this.active = true;
        this.mp = mp;
        this.mpRate = 0F;
        this.cd = 0;
        this.skillEffects = Collections.singletonList(skillEffect);
    }

    SkillEnum(Long id, String name, String desc, String jobCode, Long mp, List<SkillEffectDTO> skillEffects) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.jobCode = Collections.singletonList(jobCode);
        this.active = true;
        this.mp = mp;
        this.mpRate = 0F;
        this.cd = 0;
        this.skillEffects = skillEffects;
    }
}
