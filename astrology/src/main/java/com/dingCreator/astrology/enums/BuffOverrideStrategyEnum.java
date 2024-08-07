package com.dingCreator.astrology.enums;

import com.dingCreator.astrology.dto.BattleBuffDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * @author ding
 * @date 2024/7/27
 */
@Getter
@AllArgsConstructor
public enum BuffOverrideStrategyEnum {
    /**
     * 取最大回合数
     */
    MAX_ROUND((battleBuffDTO, battleBuffDTOList) -> doOverride(battleBuffDTO, battleBuffDTOList,
            (present, now) -> now.getRound() > present.getRound() ? now.getRound() : present.getRound())),

    /**
     * 取最小回合数
     */
    MIN_ROUND((battleBuffDTO, battleBuffDTOList) -> doOverride(battleBuffDTO, battleBuffDTOList,
            (present, now) -> now.getRound() < present.getRound() ? now.getRound() : present.getRound())),

    /**
     * 回合数相加
     */
    ROUND_PLUS((battleBuffDTO, battleBuffDTOList) -> doOverride(battleBuffDTO, battleBuffDTOList,
            (present, now) -> now.getRound() + present.getRound())),

    /**
     * 以新buff覆盖
     */
    OVERRIDE((battleBuffDTO, battleBuffDTOList) -> doOverride(battleBuffDTO, battleBuffDTOList,
            (present, now) -> now.getRound())),

    /**
     * 忽略新buff
     */
    IGNORE((battleBuffDTO, battleBuffDTOList) -> doOverride(battleBuffDTO, battleBuffDTOList,
            (present, now) -> present.getRound())),
    ;
    private final BiConsumer<BattleBuffDTO, List<BattleBuffDTO>> doOverride;

    private static void doOverride(BattleBuffDTO battleBuffDTO, List<BattleBuffDTO> battleBuffDTOList,
                            BiFunction<BattleBuffDTO, BattleBuffDTO, Integer> biFunction) {
        BattleBuffDTO present = battleBuffDTOList.stream()
                .filter(buff -> buff.getBuffDTO().getBuffName().equals(battleBuffDTO.getBuffDTO().getBuffName()))
                .findFirst().orElse(null);
        if (Objects.isNull(present)) {
            battleBuffDTOList.add(battleBuffDTO);
        } else {
            present.setRound(biFunction.apply(present, battleBuffDTO));
        }
    }
}
