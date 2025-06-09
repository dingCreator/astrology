package com.dingCreator.astrology.dto;

import com.dingCreator.astrology.dto.article.ArticleItemDTO;
import lombok.*;

import java.util.List;

/**
 * @author ding
 * @date 2025/5/23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorldBossAwardDTO {
    /**
     * 奖励类型
     */
    private String awardType;

    /*----- 单次伤害奖励用到的值 -----*/
    /**
     * 伤害区间起始值
     */
    private Long damageRangeStart;
    /**
     * 伤害区间结束值
     */
    private Long damageRangeEnd;

    /*----- 排名奖励用到的值 -----*/
    /**
     * 起始排名
     */
    private Integer rankRangeStart;
    /**
     * 结束排名
     */
    private Integer rankRangeEnd;
    /**
     * 奖品
     */
    private List<ArticleItemDTO> itemList;

    @Getter
    @AllArgsConstructor
    public enum WorldBossAwardTypeEnum {
        RANK_AWARD("rank", "排名奖励"),
        DAMAGE_AWARD("damage", "伤害奖励"),
        ;
        private final String code;
        private final String chnDesc;
    }
}
