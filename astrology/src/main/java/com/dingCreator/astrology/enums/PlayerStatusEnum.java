package com.dingCreator.astrology.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author ding
 * @date 2024/1/30
 */
@Getter
@AllArgsConstructor
public enum PlayerStatusEnum {

    /**
     * 玩家状态
     */
    FREE("FREE", "空闲"),
    HANG_UP("HANG_UP", "挂机"),
    BATTLE("BATTLE", "战斗中"),
    EXPLORE("EXPLORE", "探索中"),
    MOVING("MOVING", "移动中")
    ;
    private final String code;
    private final String name;

    public static PlayerStatusEnum getByCode(String code) {
        return Arrays.stream(PlayerStatusEnum.values()).filter(e -> e.getCode().equals(code)).findFirst().orElse(null);
    }
}
