package com.dingCreator.astrology.dto.article;

import com.dingCreator.astrology.vo.ArticleItemVO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author ding
 * @date 2024/4/10
 */
@Getter
@Setter
public abstract class ArticleItemDTO implements Serializable {

    /**
     * 物品类型
     */
    protected String itemType;

    /**
     * 数量
     */
    protected Long cnt;

    /**
     * 稀有度
     */
    protected Integer rare;

    public ArticleItemDTO(String itemType) {
        this.itemType = itemType;
        this.cnt = 1L;
        this.rare = 100;
    }

    /**
     * 发放方式
     *
     * @param playerId 玩家ID
     */
    public abstract void send2Player(Long playerId, int cnt);

    /**
     * 反显
     *
     * @return 反显内容
     */
    protected abstract ArticleItemVO fillView(ArticleItemVO vo);

    public ArticleItemVO view() {
        ArticleItemVO vo = ArticleItemVO.builder().count(this.cnt).rare(this.rare).build();
        return fillView(vo);
    }
}
