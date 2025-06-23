package com.dingCreator.astrology.enums;

import com.dingCreator.astrology.dto.battle.BuffDTO;
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
    TITLE_1(1L, "巅峰.圣星之子", new BuffDTO()),
    ;
    /**
     * id
     */
    private final Long id;
    /**
     * 称号名
     */
    private final String titleName;
    /**
     * 称号加成
     */
    private final BuffDTO buffDTO;
}
