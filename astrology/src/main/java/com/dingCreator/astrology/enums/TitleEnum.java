package com.dingCreator.astrology.enums;

import com.dingCreator.astrology.dto.battle.BuffDTO;
import com.dingCreator.astrology.dto.battle.RuleDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author ding
 * @date 2024/7/31
 */
@Getter
@AllArgsConstructor
public enum TitleEnum {
    /**
     * 获取方式：星术师rank1巅峰任务
     */
    MAGICIAN_PEAK_1(1L, "MAGICIAN_PEAK_1", "巅峰.圣星之子", new RuleDTO()),
    /**
     * 获取方式：转职
     */
    REINCARNATION(2L, "reincarnation", "轮回之人", new RuleDTO()),
    ;

    /**
     * id
     */
    private final Long id;
    /**
     * 编码
     */
    private final String titleCode;
    /**
     * 称号名
     */
    private final String titleName;
    /**
     * 称号加成
     */
    private final RuleDTO rule;
}
