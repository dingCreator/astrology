package com.dingCreator.astrology.dto.article;

import com.dingCreator.astrology.enums.ArticleTypeEnum;
import com.dingCreator.astrology.enums.skill.SkillEnum;
import com.dingCreator.astrology.service.SkillBagService;
import com.dingCreator.astrology.vo.ArticleItemVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author ding
 * @date 2024/10/28
 */
@Getter
@Setter
@ToString
public class ArticleSkillItem extends ArticleItemDTO {
    /**
     * 技能ID
     */
    private Long skillId;

    public ArticleSkillItem(Long skillId) {
        super(ArticleTypeEnum.SKILL.getType());
        this.skillId = skillId;
    }

    @Override
    public void changeCnt(Long playerId, int cnt) {
        SkillEnum.getById(skillId);
        SkillBagService.getInstance().sendSkill(playerId, skillId, cnt);
    }

    @Override
    public void checkCnt(long playerId, int requireCnt) {

    }

    @Override
    public ArticleItemVO fillView(ArticleItemVO vo) {
        SkillEnum skillEnum = SkillEnum.getById(skillId);
        vo.setName(skillEnum.getName());
        vo.setDescription(skillEnum.getDesc());
        return vo;
    }
}
