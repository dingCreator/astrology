package com.dingCreator.astrology.enums.alchemy;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author ding
 * @date 2025/3/13
 */
@Getter
@AllArgsConstructor
public enum PillTypeEnum {

    PROPERTY("property", "属性丹"),
    BUFF("buff", "buff丹"),
    STATUS("status", "状态丹")
    ;

    private final String engDesc;

    private final String chnDesc;
}
