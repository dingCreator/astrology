package com.dingCreator.astrology.entity;

import com.dingCreator.astrology.entity.base.Organism;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 玩家
 *
 * @author ding
 * @date 2024/1/22
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class Player extends Organism {
    /**
     * 玩家ID
     */
    private Long id;
    /**
     * 经验值
     */
    private Long exp;
    /**
     * 职业
     */
    private String job;
    /**
     * 所处位置
     */
    private Long mapId;
    /**
     * 状态
     */
    private String status;
    /**
     * 开始挂机时间
     */
    private Date hangUpTime;
    /**
     * 是否可用
     */
    private Boolean enabled;
}
