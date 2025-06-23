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
@TableName("astrology_player_herb")
public class PlayerHerb {
    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 玩家ID
     */
    @TableField("player_id")
    private Long playerId;
    /**
     * 药材ID
     */
    @TableField("herb_id")
    private Long herbId;
    /**
     * 药材数量
     */
    @TableField("herb_cnt")
    private Integer herbCnt;

    public static final String ID = "id";

    public static final String PLAYER_ID = "player_id";

    public static final String HERB_ID = "herb_id";

    public static final String HERB_CNT = "herb_cnt";
}
