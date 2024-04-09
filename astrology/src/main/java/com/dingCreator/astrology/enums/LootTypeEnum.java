package com.dingCreator.astrology.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Consumer;

/**
 * @author ding
 * @date 2024/4/8
 */
@Getter
@AllArgsConstructor
public enum LootTypeEnum {
    /**
     * 货币
     */
    MONEY("MONEY"),
    /**
     * 经验值
     */
    EXP("EXP"),
    /**
     * 装备
     */
    EQUIPMENT("EQUIPMENT"),
    /**
     * 技能书
     */
    SKILL("SKILL"),
    ;
    private final String lootType;
}
