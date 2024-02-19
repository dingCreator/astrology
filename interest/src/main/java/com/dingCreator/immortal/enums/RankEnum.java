package com.dingCreator.immortal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ding
 * @date 2024/2/3
 */
@Getter
@AllArgsConstructor
public enum RankEnum {
    /**
     * Rank
     */
    SI_DI_WU_SHI_RANK_1(JobEnum.SI_DI_WU_SHI.getJobCode(), 1, "迷雾武徒", 1, 20),
    SI_DI_WU_SHI_RANK_2(JobEnum.SI_DI_WU_SHI.getJobCode(), 2, "死地武者", 21, 40),
    SI_DI_WU_SHI_RANK_3(JobEnum.SI_DI_WU_SHI.getJobCode(), 3, "雾隐狂煞", 41, 60),
    SI_DI_WU_SHI_RANK_4(JobEnum.SI_DI_WU_SHI.getJobCode(), 4, "诡雾狂刀", 61, 80),
    SI_DI_WU_SHI_RANK_5(JobEnum.SI_DI_WU_SHI.getJobCode(), 5, "血沐修罗", 81, 100),

    MAGICIAN_RANK_1(JobEnum.MAGICIAN.getJobCode(), 1, "星术学徒", 1, 20),
    MAGICIAN_RANK_2(JobEnum.MAGICIAN.getJobCode(), 2, "星术师", 21, 40),
    MAGICIAN_RANK_3(JobEnum.MAGICIAN.getJobCode(), 3, "御星领主", 41, 60),
    MAGICIAN_RANK_4(JobEnum.MAGICIAN.getJobCode(), 4, "璨星神君", 61, 80),
    MAGICIAN_RANK_5(JobEnum.MAGICIAN.getJobCode(), 5, "星灵圣王", 81, 100),

    XIU_ZHEN_RANK_1(JobEnum.XIU_ZHEN.getJobCode(), 1, "炼精化气", 1, 20),
    XIU_ZHEN_RANK_2(JobEnum.XIU_ZHEN.getJobCode(), 2, "炼气化神", 21, 40),
    XIU_ZHEN_RANK_3(JobEnum.XIU_ZHEN.getJobCode(), 3, "炼神还虚", 41, 60),
    XIU_ZHEN_RANK_4(JobEnum.XIU_ZHEN.getJobCode(), 4, "炼虚合道", 61, 80),
    XIU_ZHEN_RANK_5(JobEnum.XIU_ZHEN.getJobCode(), 5, "超凡入圣", 81, 100),

    GUN_RANK_1(JobEnum.GUN.getJobCode(), 1, "NoName", 1, 20),
    GUN_RANK_2(JobEnum.GUN.getJobCode(), 2, "NoName", 21, 40),
    GUN_RANK_3(JobEnum.GUN.getJobCode(), 3, "NoName", 41, 60),
    GUN_RANK_4(JobEnum.GUN.getJobCode(), 4, "NoName", 61, 80),
    GUN_RANK_5(JobEnum.GUN.getJobCode(), 5, "NoName", 81, 100),

    ;
    /**
     * 所属职业
     */
    private final String jobCode;
    /**
     * 阶级
     */
    private final Integer rank;
    /**
     * 阶级名称
     */
    private final String rankName;
    /**
     * 最低等级
     */
    private final Integer minLevel;
    /**
     * 最高等级
     */
    private final Integer maxLevel;

    /**
     * Rank
     */
    private static final Map<String, Map<Integer, RankEnum>> RANK_MAP = new HashMap<>(128);

    static {
        for (RankEnum r : RankEnum.values()) {
            Map<Integer, RankEnum> tmp = RANK_MAP.getOrDefault(r.getJobCode(), new HashMap<>(16));
            tmp.put(r.getRank(), r);
            RANK_MAP.put(r.getJobCode(), tmp);
        }
    }

    /**
     * 根据职业和阶级获取对应阶级
     *
     * @param jobCode 职业编码
     * @param rank    阶级
     * @return 阶级枚举
     */
    public static RankEnum getEnum(String jobCode, int rank) {
        return RANK_MAP.get(jobCode).get(rank);
    }
}
