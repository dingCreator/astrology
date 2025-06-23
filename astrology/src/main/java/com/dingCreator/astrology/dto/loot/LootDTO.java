package com.dingCreator.astrology.dto.loot;

import com.dingCreator.astrology.enums.AssetTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author ding
 * @date 2024/8/27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LootDTO {
    /**
     * 资产
     */
    private Map<AssetTypeEnum, Long> assetMap;
    /**
     * 经验
     */
    private Long exp;
    /**
     * 扩展信息
     */
    private String extInfo;
    /**
     * 实物
     */
    private List<LootItemDTO> itemList;
}
