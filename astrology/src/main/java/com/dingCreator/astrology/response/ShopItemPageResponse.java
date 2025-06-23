package com.dingCreator.astrology.response;

import com.dingCreator.astrology.dto.ShopItemDTO;
import com.dingCreator.astrology.enums.AssetTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author ding
 * @date 2025/6/21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopItemPageResponse {

    private List<AssetTypeEnum> costAssetTypeList;

    private PageResponse<ShopItemDTO> pageResponse;
}
