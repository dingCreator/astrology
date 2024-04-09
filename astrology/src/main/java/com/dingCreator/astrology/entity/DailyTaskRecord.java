package com.dingCreator.astrology.entity;

import lombok.Data;

import java.util.Date;

/**
 * 每日任务
 *
 * @author ding
 * @date 2024/2/3
 */
@Data
public class DailyTaskRecord {
    /**
     * ID
     */
    private Long id;
    /**
     * 玩家ID
     */
    private Long playerId;
    /**
     * 周期月份
     */
    private String periodMonth;
    /**
     * 完成情况统计
     * 以,分隔三个数字
     * 三个数字采用bitmap储存，每个玩家一个月一条数据
     */
    private String completeStatics;
}
