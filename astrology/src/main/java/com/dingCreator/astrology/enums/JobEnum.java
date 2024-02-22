package com.dingCreator.astrology.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author ding
 * @date 2024/1/28
 */
@Getter
@AllArgsConstructor
public enum JobEnum {
    /**
     * 职业
     */
    XIU_ZHEN("XIU_ZHEN",
            "修真者",
            "",
            200L,
            100L,
            30L,
            8L,
            10L,
            5L,
            0F,
            0L,
            5L,
            10L,
            10L),

    SI_DI_WU_SHI("SI_DI_WU_SHI",
            "死地武士",
            "",
            400L,
            100L,
            15L,
            15L,
            15L,
            6L,
            0F,
            0L,
            2L,
            10L,
            10L),

    MAGICIAN("MAGICIAN",
            "星术师",
            "",
            250L,
            200L,
            8L,
            6L,
            35L,
            12L,
            0F,
            0L,
            2L,
            10L,
            10L),

    GUN("GUN",
            "枪炮师",
            "",
            250L,
            150L,
            20L,
            7L,
            15L,
            4L,
            0.02F,
            0L,
            3L,
            10L,
            10L),
    ;

    /**
     * 职业编号
     */
    private final String jobCode;
    /**
     * 职业名称
     */
    private final String jobName;
    /**
     * 职业描述
     */
    private final String description;
    /**
     * 初始血量
     */
    private final Long initHp;
    /**
     * 初始蓝量
     */
    private final Long initMp;
    /**
     * 初始攻击力
     */
    private final Long initAtk;
    /**
     * 初始防御力
     */
    private final Long initDef;
    /**
     * 初始魔攻
     */
    private final Long initMagicAtk;
    /**
     * 初始魔抗
     */
    private final Long initMagicDef;
    /**
     * 初始穿透
     */
    private final Float initPenetrate;
    /**
     * 暴击
     */
    private final Long initCritical;
    /**
     * 行动速度
     */
    private final Long initBehaviorSpeed;
    /**
     * 命中率
     */
    private final Long initHit;
    /**
     * 闪避率
     */
    private final Long initDodge;

    public static JobEnum getByName(String name) {
        return Arrays.stream(JobEnum.values()).filter(job -> job.getJobName().equals(name))
                .findFirst().orElse(null);
    }

    public static JobEnum getByCode(String name) {
        return Arrays.stream(JobEnum.values()).filter(job -> job.getJobCode().equals(name))
                .findFirst().orElse(null);
    }
}
