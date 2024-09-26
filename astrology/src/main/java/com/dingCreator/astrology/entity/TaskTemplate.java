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
     * 任务标题ID
     */
    @TableField("title_id")
    private Long titleId;
    /**
     * 任务描述
     */
    @TableField("description")
    private String description;
    /**
     * 任务详情
     */
    private List<TaskTemplateDetail> detailList;
}
