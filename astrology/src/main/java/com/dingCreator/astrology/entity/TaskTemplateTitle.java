package com.dingCreator.astrology.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;

/**
 * @author ding
 * @date 2024/9/23
 */
@Data
@TableName("astrology_task_template_head")
public class TaskTemplateTitle {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 任务标题
     */
    @TableField("title")
    private String title;
    /**
     * 任务描述
     */
    @TableField("description")
    private String description;
    /**
     * 任务类型
     *
     * @see com.dingCreator.astrology.enums.task.TaskTypeEnum
     */
    @TableField("task_type")
    private String taskType;
    /**
     * 可完成任务的地图，逗号分隔，为空表示无限制
     */
    @TableField("limit_map_id")
    private String limitMapId;
    /**
     * 任务是否必须按顺序完成
     */
    @TableField("children_sort")
    private Boolean childrenSort;
    /**
     * 是否与其他任务互斥
     */
    @TableField("mutual_exclusion")
    private Boolean mutualExclusion;
    /**
     * 是否允许组队
     */
    @TableField("allow_team")
    private Boolean allowTeam;
    /**
     * 是否允许多次接取
     */
    @TableField("allow_repeatable_receive")
    private Boolean allowRepeatableReceive;
    /**
     * 子任务
     */
    private List<TaskTemplate> templateList;
}
