package com.dingCreator.astrology.enums.skill;

import cn.hutool.core.collection.CollectionUtil;
import com.dingCreator.astrology.dto.battle.BattleBuffDTO;
import com.dingCreator.astrology.dto.battle.BattleDTO;
import com.dingCreator.astrology.enums.EffectTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ding
 * @date 2024/2/4
 */
@Getter
@AllArgsConstructor
public enum TargetEnum {
    /**
     * 技能目标
     */
    ANY_ENEMY("ANY_ENEMY", "任意敌方，按正常锁定逻辑", true, (from, our, enemy) -> getTaunt(enemy)),
    FIRST_ENEMY("FIRST_ENEMY", "第一个敌方", true, (from, our, enemy) -> Collections.singletonList(enemy.get(0))),
    ANY_OUR("ANY_OUR", "任意我方，按正常锁定逻辑", false, (from, our, enemy) -> Collections.singletonList(our.get(0))),
    ALL_ENEMY("ALL_ENEMY", "全体敌方", true, (from, our, enemy) -> enemy),
    ALL_OUR("ALL_ENEMY", "全体我方", false, (from, our, enemy) -> our),
    ME("ME", "自己", false, (from, our, enemy) -> Collections.singletonList(from)),
    ;
    private final String code;

    private final String desc;

    private final boolean enemy;

    private final Target<BattleDTO> target;

    @FunctionalInterface
    public interface Target<E extends BattleDTO> {

        default List<E> getTarget(E from, List<E> our, List<E> enemy) {
            List<E> aliveOur = our.stream()
                    .filter(b -> b.getOrganismInfoDTO().getOrganismDTO().getHpWithAddition() > 0)
                    .collect(Collectors.toList());
            List<E> aliveEnemy = enemy.stream()
                    .filter(b -> b.getOrganismInfoDTO().getOrganismDTO().getHpWithAddition() > 0)
                    .collect(Collectors.toList());
            return getTarget0(from, aliveOur, aliveEnemy);
        }

        List<E> getTarget0(E from, List<E> our, List<E> enemy);
    }

    static List<BattleDTO> getTaunt(List<BattleDTO> target) {
        if (CollectionUtil.isEmpty(target)) {
            return Collections.emptyList();
        }
        List<BattleDTO> taunt = target.stream().filter(t -> {
            List<BattleBuffDTO> buffList = t.getBuffMap().getOrDefault(EffectTypeEnum.TAUNT, null);
            return CollectionUtil.isNotEmpty(buffList);
        }).collect(Collectors.toList());

        if (!taunt.isEmpty()) {
            target = Collections.singletonList(
                    taunt.stream()
                            .max(Comparator.comparing(t -> t.getOrganismInfoDTO().getOrganismDTO().getHpWithAddition()))
                            .get()
            );
        } else {
            target = Collections.singletonList(target.get(0));
        }
        return target;
    }
}
