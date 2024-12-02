package com.dingCreator.astrology.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ding
 * @date 2024/8/27
 */
@Data
@TableName("astrology_peak_task_template")
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    @TableField("task_template_title_id")
    private Long taskTemplateTitleId;

    public static final String ID = "id";

    public static final String JOB = "job";

    public static final String RANK = "`rank`";

    public static final String TASK_TEMPLATE_TITLE_ID= "task_template_title_id";
}
