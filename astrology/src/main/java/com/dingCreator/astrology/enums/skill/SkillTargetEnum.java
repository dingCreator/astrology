package com.dingCreator.astrology.enums.skill;

import com.dingCreator.astrology.dto.BattleDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author ding
 * @date 2024/2/4
 */
@Getter
@AllArgsConstructor
public enum SkillTargetEnum {
    /**
     * 技能目标
     */
    ANY_ENEMY("ANY_ENEMY", "任意敌方，按正常锁定逻辑", true, (from, target) -> {
        // todo
        return target;
    }),
    FIRST_ENEMY("FIRST_ENEMY", "第一个敌方", true, (from, target) -> {
        // todo
        return target;
    }),
    ANY_OUR("ANY_OUR", "任意我方，按正常锁定逻辑", false, (from, target) -> target),
    ALL_ENEMY("ALL_ENEMY", "全体敌方", true, (from, target) -> target),
    ALL_OUR("ALL_ENEMY", "全体我方", false, (from, target) -> target),
    ME("ME", "自己", false, (from, target) -> {
        // todo
        return target;
    }),
    ;
    private final String code;

    private final String desc;

    private final boolean enemy;

    private final BiFunction<BattleDTO, List<BattleDTO>, List<BattleDTO>> getTarget;
}
