package com.dingCreator.astrology.enums.job;

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
    XIU_ZHEN("XIU_ZHEN", "修真者", 2L,
            "修真者来自古老而神秘的东方国度能够御空飞行，具有极高的速度，能够通过熟练的战斗技巧突破敌人的防御"),

    SI_DI_WU_SHI("SI_DI_WU_SHI", "死地武士", 3L,
            ""),

    MAGICIAN("MAGICIAN", "星术师", 1L,
            ""),

    GUN("GUN", "枪炮师", 4L,
            ""),

    EVIL("EVIL", "邪魔外道", 5L,
            ""),

    CHEATER("CHEATER", "神棍", 6L,
            "")
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
     * 初始地
     */
    private final Long mapId;
    /**
     * 职业描述
     */
    private final String description;

    public static JobEnum getByName(String name) {
        return Arrays.stream(JobEnum.values()).filter(job -> job.getJobName().equals(name))
                .findFirst().orElse(null);
    }

    public static JobEnum getByCode(String code) {
        return Arrays.stream(JobEnum.values()).filter(job -> job.getJobCode().equals(code))
                .findFirst().orElse(null);
    }
}
