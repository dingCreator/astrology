package com.dingCreator.astrology.enums.task;

import com.dingCreator.astrology.util.BattleUtil;
import com.dingCreator.astrology.vo.BattleResultVO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author ding
 * @date 2024/8/26
 */
@Getter
@AllArgsConstructor
public enum TaskTargetTypeEnum {
    /**
     * 战斗
     */
    BATTLE("BATTLE", "战斗任务", (playerId, monsterId) -> {
        BattleResultVO resultVO = BattleUtil.battlePVE(playerId, monsterId);
        return BattleResultVO.BattleResult.WIN.equals(resultVO.getBattleResult());
    }),
    /**
     * 提交物资
     */
    SUBMIT("SUBMIT", "提交任务", (playerId, equipmentId) -> {
        // todo 提交物资的场景待完善
        return true;
    }),
    ;
    private final String code;
    private final String name;
    private final BiFunction<Long, Long, Boolean> biFunction;

    public static TaskTargetTypeEnum getByCode(String code) {
        return Arrays.stream(TaskTargetTypeEnum.values()).filter(t -> t.getCode().equals(code)).findFirst().orElse(null);
    }
}
