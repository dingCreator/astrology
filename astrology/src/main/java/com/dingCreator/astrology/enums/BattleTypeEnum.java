package com.dingCreator.astrology.enums;

import com.dingCreator.astrology.vo.BattleResultVO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.BiFunction;

/**
 * @author ding
 * @date 2025/8/30
 */
@Getter
@AllArgsConstructor
public enum BattleTypeEnum {

    PVP((initiatorAliveNum, recipientAliveNum) -> {
        if (initiatorAliveNum.equals(recipientAliveNum)) {
            return BattleResultVO.BattleResult.DRAW;
        } else if (initiatorAliveNum > recipientAliveNum) {
            return BattleResultVO.BattleResult.WIN;
        } else {
            return BattleResultVO.BattleResult.LOSE;
        }
    }),

    PVE((initiatorAliveNum, recipientAliveNum) -> {
        if (recipientAliveNum > 0) {
            return BattleResultVO.BattleResult.LOSE;
        } else if (initiatorAliveNum > 0) {
            return BattleResultVO.BattleResult.WIN;
        } else {
            return BattleResultVO.BattleResult.DRAW;
        }
    }),

    EVE((initiatorAliveNum, recipientAliveNum) -> {
        if (initiatorAliveNum.equals(recipientAliveNum)) {
            return BattleResultVO.BattleResult.DRAW;
        } else if (initiatorAliveNum > recipientAliveNum) {
            return BattleResultVO.BattleResult.WIN;
        } else {
            return BattleResultVO.BattleResult.LOSE;
        }
    }),
    ;

    private final BiFunction<Long, Long, BattleResultVO.BattleResult> resultJudgeFunc;
}
