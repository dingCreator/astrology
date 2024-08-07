package com.dingCreator.astrology.enums.task;

import lombok.AllArgsConstructor;
import lombok.Getter;

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
    PARENT(),
    /**
     * 战斗
     */
    BATTLE(),
    /**
     * 提交物资
     */
    SUBMIT(),
    ;
}
