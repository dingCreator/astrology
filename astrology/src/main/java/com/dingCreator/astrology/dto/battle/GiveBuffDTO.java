package com.dingCreator.astrology.dto.battle;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ding
 * @date 2025/4/27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GiveBuffDTO {

    private BattleDTO from;

    private BattleDTO tar;

    private BuffDTO buffDTO;

    private Integer round;
}
