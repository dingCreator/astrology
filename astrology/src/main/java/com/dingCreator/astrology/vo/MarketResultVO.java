package com.dingCreator.astrology.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ding
 * @date 2025/6/20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketResultVO {
    /**
     * 名称
     */
    private String itemName;
    /**
     * 数量
     */
    private Integer itemCnt;
}
