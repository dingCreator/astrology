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

    @TableField("")
    private Integer exp;

    /**
     * 炼丹术
     */
    @TableField("alchemy_skill_id")
    private Long alchemySkillId;
}
