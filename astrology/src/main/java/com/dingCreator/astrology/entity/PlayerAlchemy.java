package com.dingCreator.astrology.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author ding
 * @date 2025/3/13
 */
@Data
@TableName("astrology_player_alchemy")
public class PlayerAlchemy {
    /**
     * ID
     */
    @TableId(value = "player_id")
    private Long playerId;

    /**
     * 属性丹经验值
     */
    @TableField("property_exp")
    private Integer propertyExp;

    /**
     * 属性丹炼制等级
     */
    @TableField("property_rank")
    private Integer propertyRank;

    /**
     * buff丹经验值
     */
    @TableField("buff_exp")
    private Integer buffExp;

    /**
     * buff丹炼制等级
     */
    @TableField("buff_rank")
    private Integer buffRank;

    /**
     * 状态丹经验值
     */
    @TableField("status_exp")
    private Integer statusExp;

    /**
     * 状态丹炼制等级
     */
    @TableField("status_rank")
    private Integer statusRank;

    /**
     * 炼丹术
     */
    @TableField("alchemy_skill_id")
    private Long alchemySkillId;
}
