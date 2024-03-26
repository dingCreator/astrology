package com.dingCreator.astrology.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ding
 * @date 2024/2/26
 */
@Data
public class BattleBuffDTO implements Serializable {
    /**
     * buff内容
     */
    private BuffDTO buffDTO;
    /**
     * 剩余回合
     */
    private Integer round;
}
