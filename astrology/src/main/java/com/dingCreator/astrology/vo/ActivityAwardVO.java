package com.dingCreator.astrology.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ding
 * @date 2024/12/28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityAwardVO {
    /**
     * 物品
     */
    private ArticleItemVO articleItemVO;
    /**
     * 数量
     */
    private Integer count;
    /**
     * 是否稀有
     */
    private Boolean rare;
}
