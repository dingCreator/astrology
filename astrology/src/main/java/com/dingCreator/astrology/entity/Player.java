package com.dingCreator.astrology.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("astrology_player")
public class Player extends Organism {
    /**
     * 玩家ID
     */
    @TableId("id")
    private Long id;
    /**
     * 经验值
     */
    @TableField("exp")
    private Long exp;
    /**
     * 职业
     */
    @TableField("job")
    private String job;
    /**
     * 所处位置
     */
    @TableField("map_id")
    private Long mapId;
    /**
     * 状态
     */
    @TableField("`status`")
    private String status;
    /**
     * 进入此状态时间
     */
    @TableField("status_start_time")
    private Date statusStartTime;
    /**
     * 是否可用
     */
    @TableField("`enabled`")
    private Boolean enabled;
}
