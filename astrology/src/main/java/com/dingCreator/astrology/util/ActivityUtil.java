package com.dingCreator.astrology.util;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.dingCreator.astrology.dto.activity.ActivityDTO;
import com.dingCreator.astrology.enums.AssetTypeEnum;
import com.dingCreator.astrology.enums.activity.ActivityLimitTypeEnum;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        activity.setLimitTypeCnt(getActivityLimitCnt(limit));
    }

    /**
     * 获取限制类型
     *
     * @param limit 限制值
     * @return 限制类型
     */
    public static ActivityLimitTypeEnum getActivityLimitType(Integer limit) {
        int code = limit >> 24;
        return ActivityLimitTypeEnum.getByCode(code);
    }

    /**
     * 获取限制次数
     *
     * @param limit 限制值
     * @return 限制次数
     */
    public static int getActivityLimitCnt(Integer limit) {
        return limit << 8 >> 8;
    }

    /**
     * 获取限制值
     *
     * @param limitTypeEnum 限制类型
     * @param limitCnt      限制次数
     * @return 限制值
     */
    public static int getLimitBit(ActivityLimitTypeEnum limitTypeEnum, int limitCnt) {
        return (limitTypeEnum.getCode() << 24) + limitCnt;
    }

    public static void fillCost(ActivityDTO activity, String costJson) {
        Map<String, Long> map = JSONObject.parseObject(costJson, new TypeReference<Map<String, Long>>() {});
        Map<AssetTypeEnum, Long> costMap = map.entrySet().stream()
                .collect(Collectors.toMap(entry -> AssetTypeEnum.getByCode(entry.getKey()), Map.Entry::getValue));
        activity.setCostMap(costMap);
    }

    public static String formatCostMapJson(Map<AssetTypeEnum, Long> costMap) {
        Map<String, Long> map = costMap.entrySet().stream()
                .collect(Collectors.toMap(entry -> entry.getKey().getCode(), Map.Entry::getValue));
        return JSONObject.toJSONString(map);
    }
}
