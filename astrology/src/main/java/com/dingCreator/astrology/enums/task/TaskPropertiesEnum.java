package com.dingCreator.astrology.enums.task;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author ding
 * @date 2024/10/11
 */
@Getter
@AllArgsConstructor
public enum TaskPropertiesEnum {
    /**
     * 中文描述-字段对应关系
     */
    NAME("名称", "name"),
    DESC("说明", "description"),
    TYPE("类型", "taskType"),
    MAP("地图", "map"),
    SORT("顺序", "sort"),
    EXCLUSION("互斥", "exclusion"),
    TEAM("组队", "team"),
    REPEATABLE("重复接取", "repeatable"),
    ;
    /**
     * 中文描述
     */
    private final String chnDesc;
    /**
     * 字段属性
     */
    private final String field;
}
