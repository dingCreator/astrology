package com.dingCreator.astrology.dto.article;

import com.dingCreator.astrology.vo.ArticleItemVO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

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
     * 数量变更
     *
     * @param playerId  玩家ID
     */
    public void changeCnt(Long playerId, int cnt) {
        changeCnt(playerId, cnt, false);
    }

    /**
     * 数量变更
     *
     * @param playerId  玩家ID
     * @param checkBind 是否校验绑定关系
     */
    public abstract void changeCnt(Long playerId, int cnt, boolean checkBind);

    /**
     * 数量校验
     *
     * @param playerId   玩家ID
     * @param requireCnt 需求数量
     */
    public abstract void checkCnt(long playerId, int requireCnt);

    /**
     * 反显
     *
     * @return 反显内容
     */
    protected abstract ArticleItemVO fillView(ArticleItemVO vo);

    /**
     * 默认反显
     *
     * @return 反显内容
     */
    public ArticleItemVO view() {
        ArticleItemVO vo = ArticleItemVO.builder().count(this.cnt).rare(this.rare).build();
        return fillView(vo);
    }

    /**
     * 添加到发放队列
     *
     * @param sendingQueue 发放队列
     */
    public void add2SendingQueue(List<ArticleItemDTO> sendingQueue) {
        sendingQueue.add(this);
    }
}
