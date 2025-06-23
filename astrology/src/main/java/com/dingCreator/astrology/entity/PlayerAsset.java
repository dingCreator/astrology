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
     * 资产类型
     */
    @TableField("asset_type")
    private String assetType;
    /**
     * 资产量
     */
    @TableField("asset_cnt")
    private Long assetCnt;

    public static final String PLAYER_ID = "player_id";

    public PlayerAssetDTO convert() {
        return PlayerAssetDTO.builder()
                .id(this.id)
                .playerId(this.playerId)
                .assetType(this.assetType)
                .assetCnt(this.assetCnt).build();
    }
}
