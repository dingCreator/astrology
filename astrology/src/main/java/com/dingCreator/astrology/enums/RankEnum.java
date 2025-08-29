package com.dingCreator.astrology.enums;

import com.dingCreator.astrology.behavior.EquipmentBehavior;
import com.dingCreator.astrology.behavior.SkillBehavior;
import com.dingCreator.astrology.enums.equipment.EquipmentEnum;
import com.dingCreator.astrology.enums.job.JobEnum;
import com.dingCreator.astrology.enums.skill.SkillEnum;
import com.dingCreator.astrology.service.EquipmentBelongToService;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

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
    SI_DI_WU_SHI_RANK_1(JobEnum.SI_DI_WU_SHI.getJobCode(), 1, "迷雾武徒", 1, 20, 0.5F,
            (playerId, builder) -> {
            }),
    SI_DI_WU_SHI_RANK_2(JobEnum.SI_DI_WU_SHI.getJobCode(), 2, "死地武者", 21, 40, 0.4F,
            (playerId, builder) -> {
            }),
    SI_DI_WU_SHI_RANK_3(JobEnum.SI_DI_WU_SHI.getJobCode(), 3, "雾隐狂煞", 41, 60, 0.3F,
            (playerId, builder) -> {
            }),
    SI_DI_WU_SHI_RANK_4(JobEnum.SI_DI_WU_SHI.getJobCode(), 4, "诡雾狂刀", 61, 80, 0.2F,
            (playerId, builder) -> {
            }),
    SI_DI_WU_SHI_RANK_5(JobEnum.SI_DI_WU_SHI.getJobCode(), 5, "血沐修罗", 81, 100, 0.1F,
            (playerId, builder) -> {
            }),

    MAGICIAN_RANK_1(JobEnum.MAGICIAN.getJobCode(), 1, "星术学徒", 1, 20, 0.5F,
            (playerId, builder) -> {
            }),
    MAGICIAN_RANK_2(JobEnum.MAGICIAN.getJobCode(), 2, "星术师", 21, 40, 0.4F,
            (playerId, builder) -> {
            }),
    MAGICIAN_RANK_3(JobEnum.MAGICIAN.getJobCode(), 3, "御星领主", 41, 60, 0.3F,
            (playerId, builder) -> {
            }),
    MAGICIAN_RANK_4(JobEnum.MAGICIAN.getJobCode(), 4, "璨星神君", 61, 80, 0.2F,
            (playerId, builder) -> {
            }),
    MAGICIAN_RANK_5(JobEnum.MAGICIAN.getJobCode(), 5, "星灵圣王", 81, 100, 0.1F,
            (playerId, builder) -> {
            }),

    XIU_ZHEN_RANK_1(JobEnum.XIU_ZHEN.getJobCode(), 1, "炼精化气", 1, 20, 0.5F,
            (playerId, builder) -> {
            }),
    XIU_ZHEN_RANK_2(JobEnum.XIU_ZHEN.getJobCode(), 2, "炼气化神", 21, 40, 0.4F,
            (playerId, builder) -> {
            }),
    XIU_ZHEN_RANK_3(JobEnum.XIU_ZHEN.getJobCode(), 3, "炼神还虚", 41, 60, 0.3F,
            (playerId, builder) -> {
            }),
    XIU_ZHEN_RANK_4(JobEnum.XIU_ZHEN.getJobCode(), 4, "炼虚合道", 61, 80, 0.2F,
            (playerId, builder) -> {
            }),
    XIU_ZHEN_RANK_5(JobEnum.XIU_ZHEN.getJobCode(), 5, "超凡入圣", 81, 100, 0.1F,
            (playerId, builder) -> {
            }),

    GUN_RANK_1(JobEnum.GUN.getJobCode(), 1, "上等兵", 1, 20, 0.5F,
            (playerId, builder) -> {
            }),
    GUN_RANK_2(JobEnum.GUN.getJobCode(), 2, "上士", 21, 40, 0.4F,
            (playerId, builder) -> {
            }),
    GUN_RANK_3(JobEnum.GUN.getJobCode(), 3, "上尉", 41, 60, 0.3F,
            (playerId, builder) -> {
            }),
    GUN_RANK_4(JobEnum.GUN.getJobCode(), 4, "上校", 61, 80, 0.2F,
            (playerId, builder) -> {
            }),
    GUN_RANK_5(JobEnum.GUN.getJobCode(), 5, "上将", 81, 100, 0.1F,
            (playerId, builder) -> {
            }),

    EVIL_RANK_1(JobEnum.EVIL.getJobCode(), 1, "天谴", 1, 20, 0.5F,
            (playerId, builder) -> {
                builder.append("获得装备【").append(EquipmentEnum.EQUIPMENT_331.getName()).append("】\n");
                EquipmentBehavior.getInstance().sendEquipment(EquipmentEnum.EQUIPMENT_331.getId(), playerId);
            }),
    EVIL_RANK_2(JobEnum.EVIL.getJobCode(), 2, "孽镜", 21, 40, 0.4F,
            (playerId, builder) -> {
                builder.append("获得装备【").append(EquipmentEnum.EQUIPMENT_409.getName()).append("】\n");
                EquipmentBehavior.getInstance().sendEquipment(EquipmentEnum.EQUIPMENT_409.getId(), playerId);
            }),
    EVIL_RANK_3(JobEnum.EVIL.getJobCode(), 3, "舂臼", 41, 60, 0.3F,
            (playerId, builder) -> {
                builder.append("获得装备【").append(EquipmentEnum.EQUIPMENT_500.getName()).append("】\n");
                EquipmentBehavior.getInstance().sendEquipment(EquipmentEnum.EQUIPMENT_500.getId(), playerId);
            }),
    EVIL_RANK_4(JobEnum.EVIL.getJobCode(), 4, "破劫", 61, 80, 0.2F,
            (playerId, builder) -> {
            }),
    EVIL_RANK_5(JobEnum.EVIL.getJobCode(), 5, "彼岸", 81, 100, 0.1F,
            (playerId, builder) -> {
            }),

//    CHEATER_RANK_1(JobEnum.CHEATER.getJobCode(), 1, "谵妄", 1, 20, 0.5F,
//            (playerId, builder) -> {
//            }),
//    CHEATER_RANK_2(JobEnum.CHEATER.getJobCode(), 2, "诳语", 21, 40, 0.4F,
//            (playerId, builder) -> {
//            }),
//    CHEATER_RANK_3(JobEnum.CHEATER.getJobCode(), 3, "盗名", 41, 60, 0.3F,
//            (playerId, builder) -> {
//            }),
//    CHEATER_RANK_4(JobEnum.CHEATER.getJobCode(), 4, "诡道", 61, 80, 0.2F,
//            (playerId, builder) -> {
//            }),
//    CHEATER_RANK_5(JobEnum.CHEATER.getJobCode(), 5, "欺天", 81, 100, 0.1F,
//            (playerId, builder) -> {
//            }),
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
     * 每分钟回复状态比例
     */
    private final Float rechargeRatePerMin;
    /**
     * 突破成功奖励
     */
    private final BiConsumer<Long, StringBuilder> rankUpAward;

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
