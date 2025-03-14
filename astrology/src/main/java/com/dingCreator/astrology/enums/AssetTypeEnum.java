package com.dingCreator.astrology.enums;

import com.dingCreator.astrology.dto.organism.player.PlayerAssetDTO;
import com.dingCreator.astrology.entity.PlayerAsset;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
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
            (asset, cost) -> asset.getAstrologyCoin() >= cost,
            cost -> PlayerAssetDTO.builder().astrologyCoin(cost).build()),

    DIAMOND("diamond", "缘石", "缘石", "被缘神力量浸染的石头，或许有特殊的用处",
            (asset, cost) -> asset.getDiamond() >= cost,
            cost -> PlayerAssetDTO.builder().diamond(cost).build()),
    ;

    private final String code;

    private final String chnName;

    private final String showName;

    private final String desc;

    private final BiFunction<PlayerAssetDTO, Long, Boolean> validateCache;

    private final Function<Long, PlayerAssetDTO> transfer2Dto;

    public static AssetTypeEnum getByCode(String code) {
        return Arrays.stream(values()).filter(e -> e.getCode().equals(code)).findFirst().orElse(null);
    }

    public static AssetTypeEnum getByChnName(String chnName) {
        return Arrays.stream(values()).filter(e -> e.getChnName().equals(chnName)).findFirst().orElse(null);
    }
}
