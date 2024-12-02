package com.dingCreator.astrology.service;

import com.alibaba.fastjson.JSONObject;
import com.dingCreator.astrology.dto.activity.ActivityDTO;
import com.dingCreator.astrology.dto.activity.BaseActivityAwardRuleDTO;
import com.dingCreator.astrology.dto.activity.LuckyActivityAwardRuleDTO;
import com.dingCreator.astrology.dto.article.ArticleItemDTO;
import com.dingCreator.astrology.dto.organism.player.PlayerInfoDTO;
import com.dingCreator.astrology.util.RandomUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author ding
 * @date 2024/11/28
 */
public class LuckyActivityServiceImpl implements ActivityService {

    private static final int MAGNIFICATION = 100000;
    private static final int RANGE = 100 * MAGNIFICATION;

    @Override
    public void validate() {

    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BaseActivityAwardRuleDTO> List<T> getAwardRule(String awardRuleJson) {
        return (List<T>) new ArrayList<>(JSONObject.parseArray(awardRuleJson, LuckyActivityAwardRuleDTO.class));
    }

    @Override
    public List<ArticleItemDTO> join(ActivityDTO activity) {
        List<LuckyActivityAwardRuleDTO> awardRuleList = activity.getAwardList().stream()
                .map(award -> (LuckyActivityAwardRuleDTO) award)
                .collect(Collectors.toList());
        final int result = RandomUtil.rangeIntRandom(RANGE);
        AtomicInteger index = new AtomicInteger(result);
        LuckyActivityAwardRuleDTO rule = awardRuleList.stream().filter(award -> {
            int rate = award.getRate();
            if (rate > index.get()) {
                return true;
            }
            index.addAndGet(-rate);
            return false;
        }).findFirst().orElseThrow(() -> new IllegalArgumentException("抽奖规则配置有误"));
        return rule.getArticleItemList();
    }

    private static class Holder {
        private static final LuckyActivityServiceImpl SERVICE = new LuckyActivityServiceImpl();
    }

    private LuckyActivityServiceImpl() {

    }

    public static LuckyActivityServiceImpl getInstance() {
        return Holder.SERVICE;
    }
}
