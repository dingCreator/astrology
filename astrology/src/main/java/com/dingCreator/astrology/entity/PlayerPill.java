package com.dingCreator.astrology.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ding
 * @date 2025/3/11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("astrology_player_pill")
public class PlayerPill {
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
     * 丹药ID
     */
    @TableField("pill_id")
    private Long pillId;
    /**
     * 数量
     */
    @TableField("pill_cnt")
    private Integer pillCnt;

    public static final String ID = "id";

    public static final String PLAYER_ID = "player_id";

    public static final String PILL_ID = "pill_id";

    public static final String PILL_CNT = "pill_cnt";
}
