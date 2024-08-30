package com.dingCreator.astrology.enums.task;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 任务类型枚举
 *
 * @author ding
 * @date 2024/7/24
 */
@Getter
@AllArgsConstructor
public enum TaskTypeEnum {
    /**
     * 主任务
     */
    PARENT("parent"),
    /**
     * 巅峰任务
     */
    PEAK("peak"),
    /**
     * 日常任务
     */
    DAILY("daily"),
    ;

    private final String code;

    public static TaskTypeEnum getByCode(String code) {
        return Arrays.stream(values()).filter(e -> e.getCode().equals(code)).findFirst().orElse(null);
    }
}
