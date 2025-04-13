package com.dingCreator.astrology.util.context;

import com.dingCreator.astrology.dto.battle.BattleDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author ding
 * @date 2025/1/15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BattleBehaviorContext {

    private BattleDTO from;

    private BattleDTO tar;

    private List<BattleDTO> our;

    private List<BattleDTO> enemy;
}
