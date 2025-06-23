package com.dingCreator.astrology.vo;

import com.dingCreator.astrology.enums.AssetTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author ding
 * @date 2024/4/11
 */
@Data
public class LootVO implements Serializable {
    /**
     * 货币
     */
    private Map<AssetTypeEnum, Long> assetMap;
    /**
     * 经验值
     */
    private Long exp;
    /**
     * 掉落物名称集合
     */
    private List<ArticleItemVO> itemVOList;
}
