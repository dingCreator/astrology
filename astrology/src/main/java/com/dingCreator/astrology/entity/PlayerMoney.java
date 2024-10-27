package com.dingCreator.astrology.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author ding
 * @date 2024/10/24
 */
@Data
@TableName("astrology_player_money")
public class PlayerMoney {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 玩家ID
     */
    @TableField("player_id")
    private Long playerId;
    /**
     * 钱
     */
    @TableField("money")
    private Long money;
}
