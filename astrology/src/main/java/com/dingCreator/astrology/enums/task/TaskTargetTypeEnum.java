package com.dingCreator.astrology.enums.task;

import com.dingCreator.astrology.service.MonsterService;
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
    BATTLE("battle", "战斗任务", (playerId, monsterId) -> {
        BattleResultVO resultVO = BattleUtil.battlePVE(playerId, monsterId);
        return BattleResultVO.BattleResult.WIN.equals(resultVO.getBattleResult());
    }, monsterId -> MonsterService.getMonsterById(monsterId).getName()),
    /**
     * 提交物资
     */
    SUBMIT("submit", "提交任务", (playerId, equipmentId) -> {
        // todo 提交物资的场景待完善
        return true;
    }, equipmentId -> "功能开发中"),
    ;
    /**
     * 编码
     */
    private final String code;
    /**
     * 名称
     */
    private final String name;
    /**
     * 完成任务逻辑
     */
    private final BiFunction<Long, Long, Boolean> biFunction;
    /**
     * 目标实体名称
     */
    private final Function<Long, String> targetEntityName;

    public static TaskTargetTypeEnum getByCode(String code) {
        return Arrays.stream(TaskTargetTypeEnum.values()).filter(t -> t.getCode().equals(code)).findFirst().orElse(null);
    }
}
