package com.dingCreator.astrology.entity;

import lombok.Data;

/**
 * 突破boss
 *
 * @author ding
 * @date 2024/2/2
 */
@Data
public class RankUpBoss {
    /**
     * 主键
     */
    private Long id;
    /**
     * 职业
     */
    private String job;
    /**
     * 突破前阶级
     */
    private Integer rank;
    /**
     * boss的ID
     */
    private Long bossId;
}
