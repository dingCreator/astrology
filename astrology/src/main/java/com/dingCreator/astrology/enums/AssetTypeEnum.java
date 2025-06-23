package com.dingCreator.astrology.enums;

import com.dingCreator.astrology.dto.organism.player.PlayerAssetDTO;
import com.dingCreator.astrology.entity.PlayerAsset;
import com.dingCreator.astrology.enums.exception.PlayerExceptionEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author ding
 * @date 2024/12/4
 */
@Getter
@AllArgsConstructor
public enum AssetTypeEnum {
    /**
     * 货币类型
     */
    ASTROLOGY_COIN("astrologyCoin", "圣星币", "圣星币", "圣星王朝发行的货币，大陆的通用货币",
            (assetList, cost) -> validateCache(assetList, "astrologyCoin", cost),
            PlayerExceptionEnum.NOT_ENOUGH_ASTROLOGY_COIN, true
    ),

    DIAMOND("diamond", "缘石", "缘石", "被缘神力量浸染的石头，或许有特殊的用处",
            (assetList, cost) -> validateCache(assetList, "diamond", cost),
            PlayerExceptionEnum.NOT_ENOUGH_DIAMOND, true
    ),

    WORLD_BOSS_TOKEN("worldBossToken", "世界boss令牌", "世界boss令牌", "世界boss令牌，可用于参与挑战世界boss",
            (assetList, cost) -> validateCache(assetList, "worldBossToken", cost),
            PlayerExceptionEnum.NOT_ENOUGH_WORLD_BOSS_TOKEN, false
    ),

    MIN_SHUI_HE_XI("minShuiHeXi", "冥水河隙", "冥水河隙", "挑战M10获得的奖励，可用于商店购买商品",
            (assetList, cost) -> validateCache(assetList, "minShuiHeXi", cost),
            PlayerExceptionEnum.NOT_ENOUGH_MIN_SHUI_HE_XI, false
    ),

    ORIGIN_DRAGON_BALL("originDragonBall", "起源龙珠", "起源龙珠", "挑战M10获得的奖励，可用于商店购买商品",
            (assetList, cost) -> validateCache(assetList, "minShuiHeXi", cost),
            PlayerExceptionEnum.NOT_ENOUGH_ORIGIN_DRAGON_BALL, false
    ),

    TRIAL_AFTERGLOW("trialAfterglow", "审判余晖", "审判余晖", "",
            (assetList, cost) -> validateCache(assetList, "trialAfterglow", cost),
            PlayerExceptionEnum.NOT_ENOUGH_TRIAL_AFTERGLOW, false
    ),
    ;

    private final String code;

    private final String chnName;

    private final String showName;

    private final String desc;

    private final BiFunction<List<PlayerAssetDTO>, Long, Boolean> validateCache;
    /**
     * 资产不足时抛出的异常
     */
    private final PlayerExceptionEnum notEnoughException;
    /**
     * 是否属于交易流通资产
     */
    private final Boolean dealAsset;

    public static AssetTypeEnum getByCode(String code) {
        return Arrays.stream(values()).filter(e -> e.getCode().equals(code)).findFirst().orElse(null);
    }

    public static AssetTypeEnum getByChnName(String chnName) {
        return Arrays.stream(values()).filter(e -> e.getChnName().equals(chnName)).findFirst().orElse(null);
    }

    public static boolean validateCache(List<PlayerAssetDTO> assetList, String assetType, long assetVal) {
        return assetList.stream().filter(asset -> asset.getAssetType().equals(assetType))
                .map(asset -> asset.getAssetCnt() >= assetVal).findFirst().orElse(false);
    }
}
