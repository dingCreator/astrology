package com.dingCreator.immortal.entity;

import com.dingCreator.immortal.entity.base.Boss;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 突破boss
 *
 * @author ding
 * @date 2024/2/2
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RankUpBoss extends Boss {
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
}
