package com.dingCreator.astrology.enums.activity;

import com.dingCreator.astrology.service.ActivityService;
import com.dingCreator.astrology.service.LuckyActivityServiceImpl;
import com.dingCreator.astrology.service.SignActivityServiceImpl;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author ding
 * @date 2024/11/26
 */
@Getter
@AllArgsConstructor
public enum ActivityTypeEnum {
    /**
     * 活动类型
     */
    LUCKY("LUCKY", "抽奖", LuckyActivityServiceImpl.getInstance()),
    SIGN("SIGN", "签到", SignActivityServiceImpl.getInstance()),
    ;

    private final String code;

    private final String cheDesc;

    private final ActivityService service;

    public static ActivityTypeEnum getByCode(String code) {
        return Arrays.stream(values()).filter(e -> e.getCode().equals(code)).findFirst().orElse(null);
    }
}
