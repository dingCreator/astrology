package com.dingCreator.astrology.dto.article;

import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.dto.organism.player.PlayerAssetDTO;
import com.dingCreator.astrology.enums.ArticleTypeEnum;
import com.dingCreator.astrology.service.PlayerService;
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

    public ArticleTitleItem() {
        super(ArticleTypeEnum.TITLE.getType());
    }
    public ArticleTitleItem(Long titleId) {
        super(ArticleTypeEnum.TITLE.getType());
        this.titleId = titleId;
    }

    @Override
    public void send2Player(Long playerId, int cnt) {

    }

    @Override
    public ArticleItemVO view() {
        return ArticleItemVO.builder().name("功能开发中").description("功能开发中").build();
    }
}
