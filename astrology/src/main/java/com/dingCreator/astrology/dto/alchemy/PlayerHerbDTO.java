package com.dingCreator.astrology.dto.alchemy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ding
 * @date 2025/3/14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerHerbDTO {
    /**
     * 药材阶级
     */
    private Integer herbRank;
    /**
     * 药材名称
     */
    private String herbName;
    /**
     * 生机
     */
    private Integer vigor;
    /**
     * 温热
     */
    private Integer warn;
    /**
     * 寒蕴
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
     * 星辰之力
     */
    private Integer star;
    /**
     * 药材描述
     */
    private String herbDesc;
    /**
     * 药材数量
     */
    private Integer herbCnt;

}
