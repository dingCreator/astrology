package com.dingCreator.astrology.enums.task;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

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
    NOT_START("NOT_START", 10),
    /**
     * 已完成
     */
    COMPLETE("COMPLETE", 20),
    /**
     * 进行中
     */
    IN_PROGRESS("IN_PROGRESS", 30),
    /**
     * 失败
     */
    FAILED("FAILED", 40),
    ;

    private final String type;

    private final Integer code;

    public static TaskScheduleEnum getByType(String type) {
        return Arrays.stream(TaskScheduleEnum.values()).filter(e -> e.getType().equals(type)).findFirst().orElse(null);
    }

    public static TaskScheduleEnum getWholeSchedule(List<TaskScheduleEnum> scheduleEnumList) {
        return scheduleEnumList.stream().max(Comparator.comparing(TaskScheduleEnum::getCode)).orElseThrow(NullPointerException::new);
    }

    public static boolean canComplete(TaskScheduleEnum scheduleEnum) {
        return TaskScheduleEnum.NOT_START.equals(scheduleEnum) || TaskScheduleEnum.IN_PROGRESS.equals(scheduleEnum);
    }
}
