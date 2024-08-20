package com.dingCreator.astrology.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author ding
 * @date 2024/8/15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorldBossChartVO {
    /**
     * 排名
     */
    private Integer rank;
    /**
     * 参与者名称
     */
    private List<String> memberNames;
    /**
     * 伤害量
     */
    private Long damage;
    /**
     * 页码
     */
    private Integer pageIndex;
    /**
     * 页面大小
     */
    private Integer pageSize;
}
