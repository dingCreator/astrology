package com.dingCreator.astrology.enums.task;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author ding
 * @date 2024/7/26
 */
@Getter
@AllArgsConstructor
public enum TaskScheduleEnum {
    /**
     * 未开始
     */
    NOT_START("NOT_START"),
    /**
     * 进行中
     */
    IN_PROGRESS("IN_PROGRESS"),
    /**
     * 已完成
     */
    COMPLETE("COMPLETE"),
    /**
     * 失败
     */
    FAILED("FAILED"),
    ;

    private final String type;
}
