package com.dingCreator.immortal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

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
    ;
    private final String code;
    private final String name;
}
