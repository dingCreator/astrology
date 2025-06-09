package com.dingCreator.astrology.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.database.DatabaseProvider;
import com.dingCreator.astrology.dto.activity.ActivityDTO;
import com.dingCreator.astrology.dto.activity.BaseActivityAwardRuleDTO;
import com.dingCreator.astrology.dto.activity.LuckyActivityAwardRuleDTO;
import com.dingCreator.astrology.dto.article.ArticleItemDTO;
import com.dingCreator.astrology.entity.Activity;
import com.dingCreator.astrology.enums.activity.ActivityTypeEnum;
import com.dingCreator.astrology.enums.exception.ActivityExceptionEnum;
import com.dingCreator.astrology.mapper.ActivityMapper;
import com.dingCreator.astrology.request.ActivityAwardSettingReq;
import com.dingCreator.astrology.request.LuckyActivityAwardSettingReq;
import com.dingCreator.astrology.util.ArticleUtil;
import com.dingCreator.astrology.util.RandomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author ding
 * @date 2024/11/28
 */
public class LuckyActivityServiceImpl implements ActivityService {

    private final Logger logger = LoggerFactory.getLogger(LuckyActivityServiceImpl.class);

    @Override
    public ActivityDTO getDefaultActivity() {
        Activity activity = DatabaseProvider.getInstance().executeReturn(sqlSession ->
                sqlSession.getMapper(ActivityMapper.class).selectOne(new QueryWrapper<Activity>()
                        .eq(Activity.ACTIVITY_TYPE, ActivityTypeEnum.LUCKY.getCode())
                        .eq(Activity.DEFAULT_FLAG, true)));
        if (Objects.isNull(activity)) {
            throw ActivityExceptionEnum.DEFAULT_ACTIVITY_NOT_EXIST.getException();
        }
        return activity.convert();
    }

    @Override
    public void validateCreate(ActivityDTO activityDTO) {
        int totalRate = activityDTO.getAwardRuleList().stream()
                .map(awardRule -> ((LuckyActivityAwardRuleDTO) awardRule).getRate())
                .reduce(Integer::sum).orElse(0);
        if (totalRate != Constants.LUCKY_RANGE) {
            throw ActivityExceptionEnum.LUCKY_RATE_INVALID.getException();
        }
    }

    @Override
    public void validateJoin() {

    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BaseActivityAwardRuleDTO> List<T> getAwardRule(String awardRuleJson) {
        List<JSONObject> jsonObjList = JSONObject.parseArray(awardRuleJson, JSONObject.class);
        List<LuckyActivityAwardRuleDTO> awardRuleList = jsonObjList.stream()
                .map(jsonObj -> {
                    LuckyActivityAwardRuleDTO lucky = new LuckyActivityAwardRuleDTO();
                    lucky.setRate(jsonObj.getInteger(LuckyActivityAwardRuleDTO.FIELD_RATE));
                    String articleJson = jsonObj.getString(BaseActivityAwardRuleDTO.FIELD_ARTICLE_ITEM_SET);
                    Set<ArticleItemDTO> articleItemSet = ArticleUtil.convertSet(articleJson);
                    lucky.setArticleItemSet(articleItemSet);
                    return lucky;
                }).collect(Collectors.toList());
        return (List<T>) awardRuleList;
    }

    @Override
    public List<ArticleItemDTO> join(ActivityDTO activity, int times) {
        List<LuckyActivityAwardRuleDTO> awardRuleList = activity.getAwardRuleList().stream()
                .map(award -> (LuckyActivityAwardRuleDTO) award)
                .collect(Collectors.toList());
        List<ArticleItemDTO> joinAwardList = new ArrayList<>(times);
        for (int i = 0; i < times; i++) {
            final int result = RandomUtil.rangeIntRandom(Constants.LUCKY_RANGE);
            AtomicInteger index = new AtomicInteger(result);
            LuckyActivityAwardRuleDTO rule = awardRuleList.stream().filter(award -> {
                int rate = award.getRate();
                if (rate > index.get()) {
                    return true;
                }
                index.addAndGet(-rate);
                return false;
            }).findFirst().orElseThrow(() -> new IllegalArgumentException("抽奖规则配置有误"));
            joinAwardList.addAll(rule.getArticleItemSet());
        }
        return joinAwardList;
    }

    @Override
    public <T extends BaseActivityAwardRuleDTO> String parseAwardRule2Json(List<T> list) {
        return JSONObject.toJSONString(list);
    }

    @Override
    public void easySettingAward(ActivityDTO activityDTO, ActivityAwardSettingReq activityAwardSettingReq) {
        // 概率配置校验
        LuckyActivityAwardSettingReq settings = (LuckyActivityAwardSettingReq) activityAwardSettingReq;
        BigDecimal decimal = settings.getSettingItemList().stream()
                .map(LuckyActivityAwardSettingReq.AwardSettingItem::getRate)
                .reduce(BigDecimal::add).orElseThrow(() -> new IllegalArgumentException("概率参数配置有误"));
        if (decimal.floatValue() != Constants.LUCKY_RATE_PERCENT) {
            throw new IllegalArgumentException("概率之和不为1，当前概率之和：" + decimal.floatValue());
        }
        // 构建奖品
        List<LuckyActivityAwardRuleDTO> awardRuleList = settings.getSettingItemList().stream().map(item -> {
            // 计算每个奖品组的概率
            int size = item.getArticleItemList().size();
            BigDecimal range = item.getRate().multiply(BigDecimal.valueOf(Constants.LUCKY_MAGNIFICATION));

            int remaining = range.intValue() % size;
            int rate = range.intValue() / size;

            List<LuckyActivityAwardRuleDTO> ruleList = item.getArticleItemList().stream().map(articleItemGroup -> {
                LuckyActivityAwardRuleDTO rule = new LuckyActivityAwardRuleDTO();
                rule.setRate(rate);
                rule.setArticleItemSet(articleItemGroup);
                return rule;
            }).collect(Collectors.toList());

            // 有余数的加到最后一件奖品
            LuckyActivityAwardRuleDTO lastRule = ruleList.get(size - 1);
            lastRule.setRate(lastRule.getRate() + remaining);
            return ruleList;
        }).reduce((list1, list2) -> {
            list2.addAll(list1);
            return list2;
        }).orElseThrow(() -> new IllegalArgumentException("抽奖奖品配置有误"));
        activityDTO.setAwardRuleList(awardRuleList.stream()
                .map(award -> (BaseActivityAwardRuleDTO) award).collect(Collectors.toList()));
    }

    @Override
    public List<String> queryAwardList(ActivityDTO activityDTO) {
        return activityDTO.getAwardRuleList().stream()
                .map(award -> {
                    String awardName = award.getArticleItemSet().stream()
                            .map(item -> {
                                String name = item.view().getName();
                                if (Objects.nonNull(item.view().getCount())) {
                                    name += ("*" + item.view().getCount());
                                }
                                return name;
                            }).reduce((s1, s2) -> s1 + "，" + s2).orElse("");
                    return "[" + awardName + "] "
                            + (float) ((LuckyActivityAwardRuleDTO) award).getRate() / (float) Constants.LUCKY_MAGNIFICATION
                            + "%";
                })
                .collect(Collectors.toList());
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
