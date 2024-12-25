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
     * 玩家ID
     */
    private Long playerId;
    /**
     * 圣星币
     */
    private Long astrologyCoin;
    /**
     * 缘石
     */
    private Long diamond;
}
