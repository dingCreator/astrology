package com.dingCreator.astrology.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dingCreator.astrology.dto.activity.ActivityDTO;
import com.dingCreator.astrology.dto.activity.BaseActivityAwardRuleDTO;
import com.dingCreator.astrology.enums.activity.ActivityTypeEnum;
import com.dingCreator.astrology.util.ActivityUtil;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author ding
 * @date 2024/11/26
 */
@Data
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
     * 高4位表示限制类型，中8位表示范围，低20位表示次数
     */
    @TableField("limit")
    private Integer limit;

    /**
     * 是否可用
     */
    @TableField("enabled")
    private Boolean enabled;

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
     * 奖品规则
     */
    private String awardRuleJson;

    public ActivityDTO convert() {
        ActivityDTO dto = ActivityDTO.builder()
                .id(this.id)
                .activityName(this.activityName)
                .activityType(ActivityTypeEnum.getByCode(this.activityType))
                .startTime(this.startTime)
                .endTime(this.endTime)
                .enabled(this.enabled)
                .build();
        dto.setAwardList(dto.getActivityType().getService().getAwardRule(this.awardRuleJson));
        ActivityUtil.fillLimit(dto, this.limit);
        return dto;
    }
}
