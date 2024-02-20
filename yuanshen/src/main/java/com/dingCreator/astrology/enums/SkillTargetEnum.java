package com.dingCreator.yuanshen.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author ding
 * @date 2024/2/4
 */
@Getter
@AllArgsConstructor
public enum SkillTargetEnum {
    /**
     * 技能目标
     */
    ANY_ENEMY("ANY_ENEMY", "任意敌方，按正常锁定逻辑"),
    ANY_OUR("ANY_OUR", "任意我方，按正常锁定逻辑"),
    ALL_ENEMY("ALL_ENEMY", "全体敌方"),
    ALL_OUR("ALL_ENEMY", "全体我方"),
    ME("ME", "自己"),
    ;
    private final String code;

    private final String desc;
}
