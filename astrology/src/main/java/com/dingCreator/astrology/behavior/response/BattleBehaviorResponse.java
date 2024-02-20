package com.dingCreator.astrology.behavior.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

/**
 * @author ding
 * @date 2023/4/18
 */
@Data
public class BattleBehaviorResponse implements Serializable {
    /**
     * 是否可以进行此次战斗
     */
    private Boolean canBattle;
    /**
     * 战斗信息
     */
    private List<String> info;
    /**
     * 战况（对于发起方而言）
     */
    private BattleResult battleResult;

    /**
     * 战斗结果
     */
    @AllArgsConstructor
    @Getter
    public static enum BattleResult {
        /**
         * 胜平负
         */
        WIN("WIN","胜"),
        DRAW("DRAW", "平"),
        LOSE("LOSE", "负"),
        TIME_OUT("TIME_OUT", "超时"),
        ;

        private final String engDesc;
        private final String chnDesc;
    }
}
