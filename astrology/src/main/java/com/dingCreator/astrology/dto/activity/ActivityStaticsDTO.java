package com.dingCreator.astrology.dto.activity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ding
 * @date 2024/11/27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityStaticsDTO {
    /**
     * 主键
     */
    private Long id;

    /**
     * 活动ID
     */
    private Long activityId;

    /**
     * 玩家ID
     */
    private Long playerId;

    /**
     * 统计时间节点
     */
    private String dateTime;

    /**
     * 次数
     */
    private Integer count;
}
