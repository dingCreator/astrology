package com.dingCreator.astrology.enums.equipment;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author ding
 * @date 2024/4/7
 */
@Getter
@AllArgsConstructor
public enum EquipmentRankEnum {

    /**
     * 装备级别
     */
    SP("SP", "论外级", 0, 0, 0),
    ORDINARY("ORDINARY", "凡级", 0, 0, 100),
    NORMAL("NORMAL", "普通级", 0, 15, 200),
    DYNAMIC("DYNAMIC", "灵威级", 0, 30, 300),
    MYSTERY("MYSTERY", "通玄级", 0, 50, 400),
    WONDER("WONDER", "神迹级", 0, 75, 500),
    RULE("RULE", "法则级", 0, 81, 600),
    WORLD("WORLD", "世界级", 0, 500, 9999),
    ;

    private final String rank;

    private final String rankChnDesc;

    private final Integer maxEquipmentLevel;

    private final Integer defaultPlayerLimitLevel;

    private final Integer rare;
}
