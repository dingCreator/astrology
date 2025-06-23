package com.dingCreator.astrology.dto.article;

import com.dingCreator.astrology.enums.ArticleTypeEnum;
import com.dingCreator.astrology.enums.alchemy.HerbEnum;
import com.dingCreator.astrology.service.PlayerHerbService;
import com.dingCreator.astrology.vo.ArticleItemVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author ding
 * @date 2025/3/13
 */
@Getter
@Setter
@ToString
public class ArticleHerbItem extends ArticleItemDTO {

    private Long herbId;

    public ArticleHerbItem(Long herbId) {
        super(ArticleTypeEnum.HERB.getType());
        this.herbId = herbId;
    }

    @Override
    public void changeCnt(Long playerId, int cnt) {
        PlayerHerbService.getInstance().addHerb(playerId, herbId, this.cnt.intValue() * cnt);
    }

    @Override
    public void checkCnt(long playerId, int requireCnt) {

    }

    @Override
    public ArticleItemVO fillView(ArticleItemVO vo) {
        vo.setName(HerbEnum.getById(herbId).getName());
        vo.setDescription("缘神没有写药材介绍");
        return vo;
    }
}
