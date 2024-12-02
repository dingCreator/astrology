package com.dingCreator.astrology.util;

import com.dingCreator.astrology.dto.activity.ActivityDTO;
import com.dingCreator.astrology.enums.activity.ActivityLimitTypeEnum;

/**
 * @author ding
 * @date 2024/11/28
 */
public class ActivityUtil {

    /**
     * 补充活动限制信息
     *
     * @param activity 活动
     * @param limit    限制配置
     */
    public static void fillLimit(ActivityDTO activity, Integer limit) {
        activity.setLimitTypeEnum(getActivityLimitType(limit));
        activity.setLimitTypeRange(getActivityLimitRange(limit));
        activity.setLimitTypeCnt(getActivityLimitCnt(limit));
    }

    public static ActivityLimitTypeEnum getActivityLimitType(Integer limit) {
        int code = limit >> 28;
        return ActivityLimitTypeEnum.getByCode(code);
    }

    public static int getActivityLimitRange(Integer limit) {
        return limit << 4 >> 24;
    }

    public static int getActivityLimitCnt(Integer limit) {
        return limit << 8;
    }
}
