package com.dingCreator.astrology.vo;

import lombok.Data;

import java.util.List;

/**
 * @author ding
 * @date 2025/5/27
 */
@Data
public class WorldBossResultVO {
    /**
     * 伤害量
     */
    private Long damage;
    /**
     * 物品列表
     */
    private List<ArticleItemVO> articleItemVOList;
}
