package com.dingCreator.astrology.vo;

import com.dingCreator.astrology.enums.RankEnum;
import com.dingCreator.astrology.enums.job.JobEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author ding
 * @date 2024/12/27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimplePlayerInfoVO {
    /**
     * id
     */
    private Long id;
    /**
     * 阶级
     */
    private Integer rank;
    /**
     * 名称
     */
    private String name;
    /**
     * 攻击力
     */
    protected Long atk;
    /**
     * 法强
     */
    protected Long magicAtk;

    /**
     * HP
     */
    private Long hp;
    /**
     * 最大HP
     */
    private Long maxHp;
    /**
     * MP
     */
    private Long mp;
    /**
     * 最大MP
     */
    private Long maxMp;
    /**
     * 等级
     */
    private Integer level;
    /**
     * 经验值
     */
    private Long exp;
    /**
     * 当前等级最大经验值
     */
    private Long currentLevelMaxExp;
    /**
     * 职业
     */
    private String job;
    /**
     * 状态
     */
    private String status;
}
