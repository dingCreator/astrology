package com.dingCreator.astrology.dto.task;

import com.dingCreator.astrology.enums.RankEnum;
import com.dingCreator.astrology.enums.job.JobEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 巅峰任务
 *
 * @author ding
 * @date 2024/8/26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PeakTaskDTO {
    /**
     * 职业
     */
    private JobEnum jobEnum;
    /**
     * 阶级
     */
    private RankEnum rankEnum;
    /**
     * 基础信息
     */
    private TaskTemplateTitleDTO taskTitle;
}
