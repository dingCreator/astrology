package com.dingCreator.astrology.vo;

import com.dingCreator.astrology.enums.AssetTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author ding
 * @date 2025/6/21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JoinActivityResultVO {
    /**
     * 消耗
     */
    private Map<AssetTypeEnum, Long> costMap;
    /**
     * 获得的奖品
     */
    private List<ArticleItemVO> itemVOList;
}
