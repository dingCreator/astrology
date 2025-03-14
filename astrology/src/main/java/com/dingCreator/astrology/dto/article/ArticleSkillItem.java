package com.dingCreator.astrology.dto.article;

import com.dingCreator.astrology.enums.ArticleTypeEnum;
import com.dingCreator.astrology.enums.BelongToEnum;
import com.dingCreator.astrology.enums.skill.SkillEnum;
import com.dingCreator.astrology.service.SkillBelongToService;
import com.dingCreator.astrology.vo.ArticleItemVO;
import lombok.Getter;
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

    public ArticleSkillItem() {
        super(ArticleTypeEnum.SKILL.getType());
    }

    public ArticleSkillItem(Long skillId) {
        super(ArticleTypeEnum.SKILL.getType());
        this.skillId = skillId;
    }

    @Override
    public void send2Player(Long playerId, int cnt) {
        SkillEnum.getById(skillId);
        SkillBelongToService.getInstance().createSkillBelongTo(BelongToEnum.PLAYER.getBelongTo(), playerId, skillId);
    }

    @Override
    public ArticleItemVO view() {
        SkillEnum skillEnum = SkillEnum.getById(skillId);
        return ArticleItemVO.builder().name(skillEnum.getName()).rare(100).description(skillEnum.getDesc()).build();
    }
}
