package com.dingCreator.astrology.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dingCreator.astrology.dto.activity.ActivityDTO;
import com.dingCreator.astrology.enums.activity.ActivityTypeEnum;
import com.dingCreator.astrology.util.ActivityUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author ding
 * @date 2024/11/26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("astrology_activity")
public class Activity {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 活动类型
     */
    @TableField("activity_type")
    private String activityType;

    /**
     * 活动名称
     */
    @TableField("activity_name")
    private String activityName;

    /**
     * 高8位表示限制类型，低24位表示次数
     */
    @TableField("`limit`")
    private Integer limit;

    /**
     * 是否可用
     */
    @TableField("enabled")
    private Boolean enabled;

    /**
     * 是否默认
     */
    @TableField("default_flag")
    private Boolean defaultFlag;

    /**
     * 活动开始时间
     */
    @TableField("start_time")
    private LocalDateTime startTime;

    /**
     * 活动结束时间
     */
    @TableField("end_time")
    private LocalDateTime endTime;

    /**
     * 参与活动花费
     */
    @TableField("cost_json")
    private String costJson;

    /**
     * 奖品规则
     */
    @TableField("award_rule_json")
    private String awardRuleJson;

    public static final String ACTIVITY_NAME = "activity_name";

    public static final String ACTIVITY_TYPE = "activity_type";

    public static final String DEFAULT_FLAG = "default_flag";

    public static final String START_TIME = "start_time";

    public static final String END_TIME = "end_time";

    public static final String ENABLED = "enabled";

    public ActivityDTO convert() {
        ActivityDTO dto = ActivityDTO.builder()
                .id(this.id)
                .activityName(this.activityName)
                .activityType(ActivityTypeEnum.getByCode(this.activityType))
                .startTime(this.startTime)
                .endTime(this.endTime)
                .enabled(this.enabled)
                .build();
        dto.setAwardRuleList(dto.getActivityType().getService().getAwardRule(this.awardRuleJson));
        ActivityUtil.fillCost(dto, this.costJson);
        ActivityUtil.fillLimit(dto, this.limit);
        return dto;
    }
}
