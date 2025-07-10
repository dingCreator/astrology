package com.dingCreator.astrology.dto.article;

import com.dingCreator.astrology.behavior.ExpBehavior;
import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.dto.organism.player.PlayerDTO;
import com.dingCreator.astrology.enums.ArticleTypeEnum;
import com.dingCreator.astrology.vo.ArticleItemVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

/**
 * @author ding
 * @date 2025/7/5
 */
@Getter
@Setter
@ToString
public class ArticleExpItem extends ArticleItemDTO {

    private Long val;
    private Float rate;
    private Long actualExp;

    public ArticleExpItem(long val) {
        super(ArticleTypeEnum.EXP.getType());
        this.val = val;
        this.rate = 0F;
    }

    public ArticleExpItem(float rate) {
        super(ArticleTypeEnum.EXP.getType());
        this.val = 0L;
        this.rate = rate;
    }

    public ArticleExpItem(long val, float rate) {
        super(ArticleTypeEnum.EXP.getType());
        this.val = val;
        this.rate = rate;
    }

    @Override
    public void changeCnt(Long playerId, int cnt) {
        PlayerDTO player = PlayerCache.getPlayerById(playerId).getPlayerDTO();
        long exp = 0L;
        if (this.val != 0L) {
            exp += this.val;
        }
        if (this.rate != 0F) {
            long currentLevelMaxExp = ExpBehavior.getInstance().getCurrentLevelMaxExp(player.getLevel());
            exp += Math.round(currentLevelMaxExp * this.rate);
        }
        ExpBehavior.LevelChange levelChange = ExpBehavior.getInstance().getExp(playerId, exp);
        this.actualExp = levelChange.getExp();
    }

    @Override
    public void checkCnt(long playerId, int requireCnt) {

    }

    @Override
    protected ArticleItemVO fillView(ArticleItemVO vo) {
        StringBuilder builder = new StringBuilder();
        if (Objects.isNull(actualExp)) {
            if (val != 0L) {
                builder.append(val).append("点经验值 ");
            }
            if (rate != 0F) {
                builder.append(rate * 100).append("%当前等级经验值上限");
            }
        } else {
            builder.append(actualExp).append("点经验值");
        }
        vo.setName(builder.toString());
        vo.setCount(this.cnt);
        vo.setDescription("三十年经验，三十秒拿去");
        return vo;
    }
}
