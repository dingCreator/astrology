package com.dingCreator.astrology.dto.article;

import cn.hutool.core.lang.Assert;
import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.dto.organism.player.PlayerAssetDTO;
import com.dingCreator.astrology.enums.ArticleTypeEnum;
import com.dingCreator.astrology.enums.AssetTypeEnum;
import com.dingCreator.astrology.enums.exception.PlayerExceptionEnum;
import com.dingCreator.astrology.service.PlayerService;
import com.dingCreator.astrology.vo.ArticleItemVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author ding
 * @date 2025/5/30
 */
@Getter
@ToString
public class ArticleAssetItem extends ArticleItemDTO {

    private String assetType;

    public ArticleAssetItem(String assetType) {
        super(ArticleTypeEnum.ASSET.getType());
        Assert.notNull(AssetTypeEnum.getByCode(assetType));
        this.assetType = assetType;
    }

    public ArticleAssetItem(String assetType, long cnt) {
        super(ArticleTypeEnum.ASSET.getType());
        Assert.notNull(AssetTypeEnum.getByCode(assetType));
        this.assetType = assetType;
        this.cnt = cnt;
    }

    public void setAssetType(String assetType) {
        Assert.notNull(AssetTypeEnum.getByCode(assetType));
        this.assetType = assetType;
    }

    @Override
    public void changeCnt(Long playerId, int cnt) {
        List<PlayerAssetDTO> assetList = Collections.singletonList(
                PlayerAssetDTO.builder().playerId(playerId).assetType(this.assetType).assetCnt(this.cnt * cnt).build());
        PlayerService.getInstance().changeAsset(PlayerCache.getPlayerById(playerId), assetList);
    }

    @Override
    public void checkCnt(long playerId, int requireCnt) {
        if (requireCnt <= 0) {
            return;
        }
        PlayerAssetDTO assetDTO = PlayerService.getInstance().getPlayerDTOById(playerId).getAssetList().stream()
                .filter(asset -> Objects.equals(asset.getAssetType(), this.assetType))
                .findFirst().orElse(null);
        if (assetDTO == null) {
            throw AssetTypeEnum.getByCode(this.assetType).getNotEnoughException().getException();
        }
        if (assetDTO.getAssetCnt() < requireCnt) {
            throw AssetTypeEnum.getByCode(assetDTO.getAssetType()).getNotEnoughException().getException();
        }
    }

    @Override
    public ArticleItemVO fillView(ArticleItemVO vo) {
        vo.setName(AssetTypeEnum.getByCode(assetType).getChnName());
        vo.setDescription(AssetTypeEnum.ASTROLOGY_COIN.getDesc());
        return vo;
    }
}
