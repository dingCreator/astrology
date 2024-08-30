package com.dingCreator.astrology.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author ding
 * @date 2024/8/27
 */
@Data
@TableName("astrology_peak_task_template")
public class PeakTaskTemplate {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 职业
     */
    @TableField("job")
    private String job;
    /**
     * 阶级
     */
    @TableField("`rank`")
    private Integer rank;
    /**
     * 任务模板ID
     */
    @TableField("task_template_id")
    private Long taskTemplateId;

    public static final String ID = "id";

    public static final String JOB = "job";

    public static final String RANK = "`rank`";

    public static final String TASK_TEMPLATE_ID= "task_template_id";
}