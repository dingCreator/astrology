package com.dingCreator.astrology.service;

import com.dingCreator.astrology.dto.activity.ActivityDTO;
import com.dingCreator.astrology.dto.activity.BaseActivityAwardRuleDTO;
import com.dingCreator.astrology.dto.article.ArticleItemDTO;
import com.dingCreator.astrology.dto.organism.player.PlayerInfoDTO;
import com.dingCreator.astrology.vo.ArticleItemVO;

import java.util.List;

/**
 * @author ding
 * @date 2024/11/28
 */
public class SignActivityServiceImpl implements ActivityService {

    @Override
    public void validate() {

    }

    @Override
    public <T extends BaseActivityAwardRuleDTO> List<T> getAwardRule(String awardRuleJson) {
        return null;
    }

    @Override
    public List<ArticleItemDTO> join(ActivityDTO activity) {

        return null;
    }

    private static class Holder {
        private static final SignActivityServiceImpl SERVICE = new SignActivityServiceImpl();
    }

    private SignActivityServiceImpl() {

    }

    public static SignActivityServiceImpl getInstance() {
        return Holder.SERVICE;
    }
}
