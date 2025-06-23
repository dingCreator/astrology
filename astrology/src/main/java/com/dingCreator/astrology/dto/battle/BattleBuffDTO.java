package com.dingCreator.astrology.dto.battle;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author ding
 * @date 2024/2/26
 */
@Data
@Builder
public class BattleBuffDTO implements Serializable {
    /**
     * Buff来源
     */
    private BattleDTO buffFrom;
    /**
     * buff内容
     */
    private BuffDTO buffDTO;
    /**
     * 剩余回合
     */
    private Integer round;
}
