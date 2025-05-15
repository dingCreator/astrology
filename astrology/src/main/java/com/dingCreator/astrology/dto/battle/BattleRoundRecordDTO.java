package com.dingCreator.astrology.dto.battle;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author ding
 * @date 2025/4/21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BattleRoundRecordDTO {
    /**
     * 本回合行动者
     */
    private BattleDTO from;

    /**
     * 本回合行动者友方
     */
    private List<BattleDTO> our;

    /**
     * 本回合行动者敌方
     */
    private List<BattleDTO> enemy;

    /**
     * 战场
     */
    private BattleFieldRecordDTO battleFieldRecord;
}
