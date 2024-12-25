package com.dingCreator.astrology.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dingCreator.astrology.dto.organism.player.PlayerAssetDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ding
 * @date 2024/10/24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("astrology_player_asset")
public class PlayerAsset {
    /**
     * 玩家ID 主键
     */
    @TableId("player_id")
    private Long playerId;
    /**
     * 圣星币
     */
    @TableField("astrology_coin")
    private Long astrologyCoin;
    /**
     * 缘石
     */
    @TableField("diamond")
    private Long diamond;

    public static final String PLAYER_ID = "player_id";

    public PlayerAssetDTO convert() {
        return PlayerAssetDTO.builder()
                .playerId(this.playerId)
                .astrologyCoin(this.astrologyCoin)
                .diamond(this.diamond).build();
    }
}
