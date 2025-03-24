package com.dingCreator.astrology.dto.alchemy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ding
 * @date 2025/2/27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PillDTO {
    /**
     * 丹药名称
     */
    private String pillName;
    /**
     * 丹药等级
     */
    private Integer pillRank;
    /**
     * 品质等级
     */
    private Integer qualityRank;
    /**
     * 生机
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
    private Integer qualityStart;
    /**
     * 品质
     */
    private Integer qualityEnd;
    /**
     * 星辰之力
     */
    private Integer starStart;
    /**
     * 星辰之力
     */
    private Integer starEnd;
    /**
     * 效果
     */
    private PillEffectDTO pillEffectDTO;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PillEffectDTO {

        private Integer atk;

        private Integer def;
    }
}
