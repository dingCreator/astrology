package com.dingCreator.immortal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author ding
 * @date 2024/2/4
 */
@Getter
@AllArgsConstructor
public enum SkillEffectEnum {
    /**
     * 提供BUFF
     */
    BUFF("BUFF"),
    ;

    private final String effectType;
}
