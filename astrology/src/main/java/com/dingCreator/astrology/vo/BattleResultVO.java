package com.dingCreator.astrology.vo;

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
public class BattleResultVO implements Serializable {
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
     * 发起方ID
     */
    private Long initiatorId;
    /**
     * 接收方ID
     */
    private Long recipientId;

    /**
     * 战斗结果
     */
    @AllArgsConstructor
    @Getter
    public enum BattleResult {
        /**
         * 胜平负
         */
        WIN("WIN", "胜"),
        DRAW("DRAW", "平"),
        LOSE("LOSE", "负"),
        TIME_OUT("TIME_OUT", "超时"),
        ;

        private final String engDesc;
        private final String chnDesc;
    }
}
