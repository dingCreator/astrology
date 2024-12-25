package com.dingCreator.astrology.dto.activity;

import com.dingCreator.astrology.enums.AssetTypeEnum;
import com.dingCreator.astrology.enums.activity.ActivityLimitTypeEnum;
import com.dingCreator.astrology.enums.activity.ActivityTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author ding
 * @date 2024/11/26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityDTO {

    /**
     * 主键
     */
    private Long id;

    /**
     * 活动类型
     */
    private ActivityTypeEnum activityType;

    /**
     * 活动名称
     */
    private String activityName;

    /**
     * 限制类型
     */
    private ActivityLimitTypeEnum limitTypeEnum;

    /**
     * 限制次数
     */
    private Integer limitTypeCnt;

    /**
     * 是否可用
     */
    private Boolean enabled;

    /**
     * 是否默认
     */
    private Boolean defaultFlag;

    /**
     * 活动开始时间
     */
    private LocalDateTime startTime;

    /**
     * 活动结束时间
     */
    private LocalDateTime endTime;

    /**
     * 参与花费
     */
    private Map<AssetTypeEnum, Long> costMap;

    /**
     * 奖品规则
     */
    private List<BaseActivityAwardRuleDTO> awardRuleList;
}
