package com.dingCreator.astrology.dto.article;

import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.dto.organism.player.PlayerAssetDTO;
import com.dingCreator.astrology.enums.ArticleTypeEnum;
import com.dingCreator.astrology.enums.AssetTypeEnum;
import com.dingCreator.astrology.service.PlayerService;
import com.dingCreator.astrology.vo.ArticleItemVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author ding
 * @date 2024/12/4
 */
@Getter
@Setter
@ToString
public class ArticleDiamondItem extends ArticleItemDTO {

    /**
     * 数量
     */
    private Long cnt;

    public ArticleDiamondItem() {
        super(ArticleTypeEnum.DIAMOND.getType());
    }

    public ArticleDiamondItem(Long cnt) {
        super(ArticleTypeEnum.DIAMOND.getType());
        this.cnt = cnt;
    }

    @Override
    public void send2Player(Long playerId, int cnt) {
        PlayerAssetDTO asset = PlayerAssetDTO.builder().playerId(playerId).diamond(this.cnt).build();
        PlayerService.getInstance().changeAsset(PlayerCache.getPlayerById(playerId), asset);
    }

    @Override
    public ArticleItemVO view() {
        return ArticleItemVO.builder()
                .name(AssetTypeEnum.DIAMOND.getChnName())
                .description(AssetTypeEnum.DIAMOND.getDesc())
                .count(this.cnt)
                .build();
    }
}
