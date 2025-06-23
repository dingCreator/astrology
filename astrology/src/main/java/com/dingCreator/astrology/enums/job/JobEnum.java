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

    MAGICIAN("MAGICIAN", "星术师", 1L,
            "星术师是星术界天才中的天才，能够动用星星的力量秒杀敌人",
            9L),

    XIU_ZHEN("XIU_ZHEN", "修真者", 2L,
            "修真者来自古老而神秘的东方国度能够御空飞行，具有极高的速度，能够通过熟练的战斗技巧突破敌人的防御",
            24L),

    SI_DI_WU_SHI("SI_DI_WU_SHI", "死地武士", 3L,
            "从诡境雾霭走出来的不死的武士，能使用雾霭的力量侵蚀敌人，并且具有极高的防御能力",
            21L),

    GUN("GUN", "枪炮师", 4L,
            "枪炮师是未元之都已经失败的神临计划的产物。无人知晓，枪炮师是这项计划中唯一的成功品，可以动用伪神的力量精准穿透敌人的防御",
            22L),

    EVIL("EVIL", "邪魔外道", 1L,
            "天弃之人，难以提升。对同等级和低等级来说都是灾难程度的对手，但是遇到高级别敌人就是自身的灾难",
            23L),

    CHEATER("CHEATER", "神棍", 6L,
            "江湖上招摇撞骗的神棍，但似乎也有着神秘的过往。一张神嘴能够洞穿敌人的心灵，扰乱敌方的神志。善于闪避敌人的攻击后发起反击",
            9L)
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
    /**
     * 默认被动技能
     */
    private final Long defaultInactiveSkillId;

    public static JobEnum getByName(String name) {
        return Arrays.stream(JobEnum.values()).filter(job -> job.getJobName().equals(name))
                .findFirst().orElse(null);
    }

    public static JobEnum getByCode(String code) {
        return Arrays.stream(JobEnum.values()).filter(job -> job.getJobCode().equals(code))
                .findFirst().orElse(null);
    }
}
