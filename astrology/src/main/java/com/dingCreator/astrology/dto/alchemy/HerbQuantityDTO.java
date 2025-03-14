package com.dingCreator.astrology.dto.alchemy;

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
public class HerbQuantityDTO {
    /**
     *
     */
    private Integer vigor;
    /**
     * 温热
     */
    private Integer warn;
    /**
     * 寒韵
     */
    private Integer cold;
    /**
     * 毒性
     */
    private Integer toxicity;
    /**
     * 品质
     */
    private Integer quality;
    /**
     *
     */
    private Integer star;
}
