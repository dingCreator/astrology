package com.dingCreator.astrology.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ding
 * @date 2024/3/6
 */
@Data
public class HangUpVO implements Serializable {
    /**
     * 挂机时长
     */
    private Long hangUpTime;
    /**
     * 获得的经验值
     */
    private Long exp;
}
