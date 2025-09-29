package com.dingCreator.astrology.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ding
 * @date 2024/12/23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityListQryReq {
    /**
     * 活动类型
     */
    private String activityType;
    /**
     * 是否管理员查询
     */
    private Boolean admin;
}
