package com.dingCreator.astrology.dto.alchemy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ding
 * @date 2025/3/13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerHerbListDTO {
    /**
     * ID
     */
    private Long id;
    /**
     * 玩家ID
     */
    private Long playerId;
    /**
     * 药材ID
     */
    private Long herbId;
    /**
     * 药材名称
     */
    private String herbName;
    /**
     * 药材数量
     */
    private Integer herbCnt;
}
