package com.dingCreator.astrology.dto.article;

import com.dingCreator.astrology.enums.ArticleTypeEnum;
import com.dingCreator.astrology.vo.ArticleItemVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author ding
 * @date 2024/10/28
 */
@Getter
@Setter
@ToString
public class ArticleTitleItem extends ArticleItemDTO {
    /**
     * 称号ID
     */
    private Long titleId;

    public ArticleTitleItem(Long titleId) {
        super(ArticleTypeEnum.TITLE.getType());
        this.titleId = titleId;
    }

    @Override
    public void send2Player(Long playerId, int cnt) {

    }

    @Override
    public ArticleItemVO fillView(ArticleItemVO vo) {
        vo.setName("功能开发中");
        vo.setDescription("功能开发中");
        return vo;
    }
}
