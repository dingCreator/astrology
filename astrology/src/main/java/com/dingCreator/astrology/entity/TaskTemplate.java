package com.dingCreator.astrology.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;

/**
 * @author ding
 * @date 2024/8/27
 */
@Data
@TableName("astrology_task_template")
public class TaskTemplate {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 任务名称
     */
    @TableField("task_name")
    private String taskName;
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
     * 父任务id，若为一级任务此字段为空
     */
    @TableField("parent_id")
    private Long parentId;
    /**
     * 子任务是否必须按顺序完成
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
     * 任务内容
     */
    private List<TaskTemplateDetail> details;
}
