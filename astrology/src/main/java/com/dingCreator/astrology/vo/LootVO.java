package com.dingCreator.astrology.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ding
 * @date 2024/4/11
 */
@Data
public class LootVO implements Serializable {
    /**
     * 货币
     */
    private Long astrologyCoin;
    private Long diamond;
    /**
     * 经验值
     */
    private Long exp;
    /**
     * 掉落物名称集合
     */
    private List<ArticleItemVO> itemVOList;
}
