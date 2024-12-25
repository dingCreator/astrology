package com.dingCreator.astrology.request;

import lombok.Data;

/**
 * @author ding
 * @date 2024/12/23
 */
@Data
public class ActivityPageQryReq {
    /**
     * 活动类型
     */
    private String activityType;
    /**
     * 是否管理员查询
     */
    private Boolean admin;
}
