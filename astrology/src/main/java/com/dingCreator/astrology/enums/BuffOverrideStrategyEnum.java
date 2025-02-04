package com.dingCreator.astrology.enums;

import com.dingCreator.astrology.dto.BattleBuffDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

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
    MAX_ROUND((battleBuffDTO, battleBuffDTOList) -> doOverride(battleBuffDTO, battleBuffDTOList, (present, now) -> {
        BattleBuffDTO newBuff = now.getRound() > present.getRound() ? now : present;
        battleBuffDTOList.remove(present);
        battleBuffDTOList.add(newBuff);
    })),

    /**
     * 取最小回合数
     */
    MIN_ROUND((battleBuffDTO, battleBuffDTOList) -> doOverride(battleBuffDTO, battleBuffDTOList, (present, now) -> {
        BattleBuffDTO newBuff = now.getRound() < present.getRound() ? now : present;
        battleBuffDTOList.remove(present);
        battleBuffDTOList.add(newBuff);
    })),

    /**
     * 回合数相加
     */
    ROUND_PLUS((battleBuffDTO, battleBuffDTOList) -> doOverride(battleBuffDTO, battleBuffDTOList, (present, now) ->
            present.setRound(present.getRound() + now.getRound())
    )),

    /**
     * 以新buff覆盖
     */
    OVERRIDE((battleBuffDTO, battleBuffDTOList) -> doOverride(battleBuffDTO, battleBuffDTOList, (present, now) -> {
        battleBuffDTOList.remove(present);
        battleBuffDTOList.add(now);
    })),

    /**
     * 忽略新buff
     */
    IGNORE((battleBuffDTO, battleBuffDTOList) -> doOverride(battleBuffDTO, battleBuffDTOList, (present, now) -> {
    })),

    /**
     * 根据等级覆盖
     */
    DEPENDS_ON_LEVEL((battleBuffDTO, battleBuffDTOList) -> doOverride(battleBuffDTO, battleBuffDTOList, (present, now) -> {
        BattleBuffDTO newBuff = present.getBuffDTO().getLevel() > now.getBuffDTO().getLevel() ? present : now;
        battleBuffDTOList.remove(present);
        battleBuffDTOList.add(newBuff);
    })),

    /**
     * 互不干扰
     */
    NONINTERFERENCE((battleBuffDTO, battleBuffDTOList) -> doOverride(battleBuffDTO, battleBuffDTOList, (present, now) ->
            battleBuffDTOList.add(now)
    )),

    ;
    private final BiConsumer<BattleBuffDTO, List<BattleBuffDTO>> doOverride;

    private static void doOverride(BattleBuffDTO battleBuffDTO, List<BattleBuffDTO> battleBuffDTOList,
                                   BiConsumer<BattleBuffDTO, BattleBuffDTO> biConsumer) {
        BattleBuffDTO present = battleBuffDTOList.stream()
                .filter(buff -> buff.getBuffDTO().getBuffName().equals(battleBuffDTO.getBuffDTO().getBuffName()))
                .findFirst().orElse(null);
        if (Objects.isNull(present)) {
            battleBuffDTOList.add(battleBuffDTO);
        } else {
            biConsumer.accept(present, battleBuffDTO);
        }
    }
}
