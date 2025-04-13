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
    /**
     * 旧等级
     */
    private Integer oldLevel;
    /**
     * 新等级
     */
    private Integer newLevel;
    /**
     * 回复的hp
     */
    private Long hp;
    /**
     * 回复的mp
     */
    private Long mp;
    /**
     * 信息
     */
    private String message;
}
