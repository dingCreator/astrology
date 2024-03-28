package com.dingCreator.astrology.enums.job;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author ding
 * @date 2024/2/26
 */
@Getter
@AllArgsConstructor
public enum JobInitPropertiesEnum {
    /**
     * 修真者
     */
    XIU_ZHEN("XIU_ZHEN",
            200L, 100L,
            30L, 8L,
            10L, 5L,
            0F, 0F,
            0.01F, 0F,
            1.5F, 0F,
            5L, 10L, 10L, 0F),
    /**
     * 死地武士
     */
    SI_DI_WU_SHI("SI_DI_WU_SHI",
            400L, 100L,
            15L, 15L,
            15L, 6L,
            0F, 0F,
            0.01F, 0F,
            1.5F, 0F,
            2L, 10L, 10L, 0F),
    /**
     * 星术师
     */
    MAGICIAN("MAGICIAN",
            250L, 200L,
            8L, 6L,
            35L, 12L,
            0F, 0F,
            0.01F, 0F,
            1.5F, 0F,
            2L, 10L, 10L, 0F),
    /**
     * 枪炮师
     */
    GUN("GUN",
            250L, 150L,
            20L, 7L,
            15L, 4L,
            0.02F, 0F,
            0.01F, 0F,
            1.5F, 0F,
            3L, 10L, 10L, 0F),
    /**
     * 邪魔外道
     */
    EVIL("EVIL",
            260L, 180L,
            20L, 10L,
            0L, 5L,
            0F, 0F,
            0.01F, 0F,
            1.5F, 0F,
            3L, 10L, 10L, 0F),
    /**
     * 神棍
     */
    CHEATER("CHEATER",
            300L, 80L,
            20L, 10L,
            0L, 5L,
            0F, 0F,
            0.01F, 0F,
            1.5F, 0F,
            2L, 10L, 20L, 0F),
    ;
    /**
     * 职业编号
     */
    private final String jobCode;
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
     * 初始法穿
     */
    private final Float initMagicPenetrate;
    /**
     * 初始暴击率
     */
    private final Float initCriticalRate;
    /**
     * 抗暴率
     */
    private final Float initCriticalReductionRate;
    /**
     * 爆伤倍率
     */
    private final Float initCriticalDamage;
    /**
     * 爆伤减免
     */
    private final Float initCriticalDamageReduction;
    /**
     * 行动速度
     */
    private final Long initBehaviorSpeed;
    /**
     * 命中值
     */
    private final Long initHit;
    /**
     * 闪避值
     */
    private final Long initDodge;
    /**
     * 吸血
     */
    private final Float initLifeStealing;

    public static JobInitPropertiesEnum getByCode(String name) {
        return Arrays.stream(JobInitPropertiesEnum.values()).filter(job -> job.getJobCode().equals(name))
                .findFirst().orElse(null);
    }
}
