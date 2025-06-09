package com.dingCreator.astrology.dto.organism.player;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ding
 * @date 2024/12/4
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerAssetDTO {
    /**
     * 主键
     */
    private Long id;
    /**
     * 玩家ID
     */
    private Long playerId;
    /**
     * 圣星币
     */
    private String assetType;
    /**
     * 缘石
     */
    private Long assetCnt;
}
