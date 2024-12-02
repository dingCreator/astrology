package com.dingCreator.astrology.behavior;

import com.dingCreator.astrology.vo.ArticleItemVO;

import java.util.List;

/**
 * @author ding
 * @date 2024/11/26
 */
public class ActivityBehavior {

    public List<ArticleItemVO> joinActivity(long activityId, long playerId) {

        return null;
    }

    private static class Holder {
        private static final ActivityBehavior BEHAVIOR = new ActivityBehavior();
    }

    private ActivityBehavior() {

    }

    public static ActivityBehavior getInstance() {
        return ActivityBehavior.Holder.BEHAVIOR;
    }
}
