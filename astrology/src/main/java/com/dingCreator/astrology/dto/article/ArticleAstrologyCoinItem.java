package com.dingCreator.astrology.dto.article;

import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.dto.organism.player.PlayerAssetDTO;
import com.dingCreator.astrology.enums.ArticleTypeEnum;
import com.dingCreator.astrology.enums.AssetTypeEnum;
import com.dingCreator.astrology.service.PlayerService;
import com.dingCreator.astrology.vo.ArticleItemVO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author ding
 * @date 2024/12/30
 */
@ToString
@Getter
@Setter
public class ArticleAstrologyCoinItem extends ArticleItemDTO {

    private Long cnt;

    public ArticleAstrologyCoinItem() {
        super(ArticleTypeEnum.ASTROLOGY_COIN.getType());
    }

    @Override
    public void send2Player(Long playerId) {
        PlayerAssetDTO asset = PlayerAssetDTO.builder().playerId(playerId).astrologyCoin(this.cnt).build();
        PlayerService.getInstance().changeAsset(PlayerCache.getPlayerById(playerId), asset);
    }

    @Override
    public ArticleItemVO view() {
        return ArticleItemVO.builder()
                .name(AssetTypeEnum.ASTROLOGY_COIN.getChnName())
                .description(AssetTypeEnum.ASTROLOGY_COIN.getDesc())
                .count(this.cnt)
                .rare(100)
                .build();
    }
}
