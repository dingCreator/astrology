package com.dingCreator.astrology.enums.job;

import com.dingCreator.astrology.behavior.SkillBehavior;
import com.dingCreator.astrology.enums.skill.SkillEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.function.Function;

/**
 * @author ding
 * @date 2025/4/2
 */
@Getter
@AllArgsConstructor
public enum JobLevelAwardEnum {

    MAGICIAN_LV_15(JobEnum.MAGICIAN, 15, playerId -> {
        SkillBehavior.getInstance().createSkillBelongTo(playerId, SkillEnum.SKILL_7.getId());
        return "习得技能【" + SkillEnum.SKILL_7.getName() + "】";
    }),
    MAGICIAN_LV_25(JobEnum.MAGICIAN, 25, playerId -> {
        SkillBehavior.getInstance().createSkillBelongTo(playerId, SkillEnum.SKILL_20.getId());
        return "习得技能【" + SkillEnum.SKILL_20.getName() + "】";
    }),
    MAGICIAN_LV_35(JobEnum.MAGICIAN, 35, playerId -> {
        SkillBehavior.getInstance().createSkillBelongTo(playerId, SkillEnum.SKILL_37.getId());
        return "习得技能【" + SkillEnum.SKILL_37.getName() + "】";
    }),
    MAGICIAN_LV_50(JobEnum.MAGICIAN, 50, playerId -> {
        SkillBehavior.getInstance().createSkillBelongTo(playerId, SkillEnum.SKILL_38.getId());
        return "习得技能【" + SkillEnum.SKILL_38.getName() + "】";
    }),
    MAGICIAN_LV_61(JobEnum.MAGICIAN, 61, playerId -> {
        SkillBehavior.getInstance().createSkillBelongTo(playerId, SkillEnum.SKILL_39.getId());
        return "习得技能【" + SkillEnum.SKILL_39.getName() + "】";
    }),

    XIU_ZHEN_LV_15(JobEnum.XIU_ZHEN, 15, playerId -> {
        SkillBehavior.getInstance().createSkillBelongTo(playerId, SkillEnum.SKILL_8.getId());
        return "习得技能【" + SkillEnum.SKILL_8.getName() + "】";
    }),
    XIU_ZHEN_LV_25(JobEnum.XIU_ZHEN, 25, playerId -> {
        SkillBehavior.getInstance().createSkillBelongTo(playerId, SkillEnum.SKILL_25.getId());
        return "习得技能【" + SkillEnum.SKILL_25.getName() + "】";
    }),
    XIU_ZHEN_LV_35(JobEnum.XIU_ZHEN, 35, playerId -> {
        SkillBehavior.getInstance().createSkillBelongTo(playerId, SkillEnum.SKILL_27.getId());
        return "习得技能【" + SkillEnum.SKILL_27.getName() + "】";
    }),
    XIU_ZHEN_LV_50(JobEnum.XIU_ZHEN, 50, playerId -> {
        SkillBehavior.getInstance().createSkillBelongTo(playerId, SkillEnum.SKILL_28.getId());
        return "习得技能【" + SkillEnum.SKILL_28.getName() + "】";
    }),
    XIU_ZHEN_LV_61(JobEnum.XIU_ZHEN, 61, playerId -> {
        SkillBehavior.getInstance().createSkillBelongTo(playerId, SkillEnum.SKILL_29.getId());
        return "习得技能【" + SkillEnum.SKILL_29.getName() + "】";
    }),

    SI_DI_WU_SHI_LV_15(JobEnum.SI_DI_WU_SHI, 15, playerId -> {
        SkillBehavior.getInstance().createSkillBelongTo(playerId, SkillEnum.SKILL_30.getId());
        return "习得技能【" + SkillEnum.SKILL_30.getName() + "】";
    }),
    SI_DI_WU_SHI_LV_25(JobEnum.SI_DI_WU_SHI, 25, playerId -> {
        SkillBehavior.getInstance().createSkillBelongTo(playerId, SkillEnum.SKILL_31.getId());
        return "习得技能【" + SkillEnum.SKILL_31.getName() + "】";
    }),
    SI_DI_WU_SHI_LV_35(JobEnum.SI_DI_WU_SHI, 35, playerId -> {
        SkillBehavior.getInstance().createSkillBelongTo(playerId, SkillEnum.SKILL_32.getId());
        return "习得技能【" + SkillEnum.SKILL_32.getName() + "】";
    }),
    SI_DI_WU_SHI_LV_50(JobEnum.SI_DI_WU_SHI, 50, playerId -> {
        SkillBehavior.getInstance().createSkillBelongTo(playerId, SkillEnum.SKILL_33.getId());
        return "习得技能【" + SkillEnum.SKILL_33.getName() + "】";
    }),
    SI_DI_WU_SHI_LV_61(JobEnum.SI_DI_WU_SHI, 61, playerId -> {
        SkillBehavior.getInstance().createSkillBelongTo(playerId, SkillEnum.SKILL_26.getId());
        return "习得技能【" + SkillEnum.SKILL_26.getName() + "】";
    }),

    GUN_LV_15(JobEnum.GUN, 15, playerId -> {
        SkillBehavior.getInstance().createSkillBelongTo(playerId, SkillEnum.SKILL_40.getId());
        return "习得技能【" + SkillEnum.SKILL_40.getName() + "】";
    }),
    GUN_LV_25(JobEnum.GUN, 25, playerId -> {
        SkillBehavior.getInstance().createSkillBelongTo(playerId, SkillEnum.SKILL_41.getId());
        return "习得技能【" + SkillEnum.SKILL_41.getName() + "】";
    }),
    GUN_LV_35(JobEnum.GUN, 35, playerId -> {
        SkillBehavior.getInstance().createSkillBelongTo(playerId, SkillEnum.SKILL_47.getId());
        return "习得技能【" + SkillEnum.SKILL_47.getName() + "】";
    }),
    GUN_LV_50(JobEnum.GUN, 50, playerId -> {
        SkillBehavior.getInstance().createSkillBelongTo(playerId, SkillEnum.SKILL_48.getId());
        return "习得技能【" + SkillEnum.SKILL_48.getName() + "】";
    }),
    GUN_LV_61(JobEnum.GUN, 61, playerId -> {
        SkillBehavior.getInstance().createSkillBelongTo(playerId, SkillEnum.SKILL_49.getId());
        return "习得技能【" + SkillEnum.SKILL_49.getName() + "】";
    }),

    EVIL_LV_15(JobEnum.EVIL, 15, playerId -> {
        SkillBehavior.getInstance().createSkillBelongTo(playerId, SkillEnum.SKILL_42.getId());
        return "习得技能【" + SkillEnum.SKILL_42.getName() + "】";
    }),
    EVIL_LV_25(JobEnum.EVIL, 25, playerId -> {
        SkillBehavior.getInstance().createSkillBelongTo(playerId, SkillEnum.SKILL_43.getId());
        return "习得技能【" + SkillEnum.SKILL_43.getName() + "】";
    }),
    EVIL_LV_35(JobEnum.EVIL, 35, playerId -> {
        SkillBehavior.getInstance().createSkillBelongTo(playerId, SkillEnum.SKILL_44.getId());
        return "习得技能【" + SkillEnum.SKILL_44.getName() + "】";
    }),
    EVIL_LV_50(JobEnum.EVIL, 50, playerId -> {
        SkillBehavior.getInstance().createSkillBelongTo(playerId, SkillEnum.SKILL_45.getId());
        return "习得技能【" + SkillEnum.SKILL_45.getName() + "】";
    }),
    EVIL_LV_61(JobEnum.EVIL, 61, playerId -> {
        SkillBehavior.getInstance().createSkillBelongTo(playerId, SkillEnum.SKILL_46.getId());
        return "习得技能【" + SkillEnum.SKILL_46.getName() + "】";
    }),

    CHEATER_LV_15(JobEnum.CHEATER, 15, playerId -> {return "";}),
    CHEATER_LV_25(JobEnum.CHEATER, 25, playerId -> {return "";}),
    CHEATER_LV_35(JobEnum.CHEATER, 35, playerId -> {return "";}),
    CHEATER_LV_50(JobEnum.CHEATER, 50, playerId -> {return "";}),
    CHEATER_LV_61(JobEnum.CHEATER, 61, playerId -> {return "";}),
    ;
    /**
     * 职业
     */
    private final JobEnum jobEnum;
    /**
     * 等级
     */
    private final Integer level;
    /**
     * 奖品
     */
    private final Function<Long, String> sendAward;

    public static JobLevelAwardEnum getByJobAndLv(JobEnum jobEnum, int level) {
        return Arrays.stream(values()).filter(e -> e.getJobEnum().equals(jobEnum) && e.getLevel().equals(level))
                .findFirst().orElse(null);
    }
}
