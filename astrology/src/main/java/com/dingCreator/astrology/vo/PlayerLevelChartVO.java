package com.dingCreator.astrology.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ding
 * @date 2025/8/27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerLevelChartVO {

    private Integer rank;

    private Long playerId;

    private String playerName;

    private String job;

    private Integer playerRank;

    private Integer playerLevel;
}
