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

    public ArticleHerbItem() {
        super(ArticleTypeEnum.HERB.getType());
    }

    @Override
    public void send2Player(Long playerId, int cnt)  {
        PlayerHerbService.getInstance().addHerb(playerId, herbId, cnt);
    }

    @Override
    public ArticleItemVO view() {
        return ArticleItemVO.builder()
                .name(HerbEnum.getById(herbId).getName())
                .description("缘神没有写药材介绍")
                .rare(100)
                .count(1L)
                .build();
    }
}
