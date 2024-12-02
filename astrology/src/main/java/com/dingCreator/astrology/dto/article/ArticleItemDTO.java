package com.dingCreator.astrology.dto.article;

import com.dingCreator.astrology.vo.ArticleItemVO;
import lombok.Data;

import java.io.Serializable;

/**
 * @author ding
 * @date 2024/4/10
 */
@Data
public abstract class ArticleItemDTO implements Serializable {

    /**
     * 物品类型
     */
    protected String itemType;

    public ArticleItemDTO(String itemType) {
        this.itemType = itemType;
    }

    /**
     * 发放方式
     *
     * @param playerId 玩家ID
     */
    public abstract void send2Player(Long playerId);

    /**
     * 反显
     *
     * @return 反显内容
     */
    public abstract ArticleItemVO view();
}
