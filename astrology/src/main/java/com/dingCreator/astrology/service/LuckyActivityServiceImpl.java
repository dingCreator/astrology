package com.dingCreator.astrology.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.database.DatabaseProvider;
import com.dingCreator.astrology.dto.activity.ActivityDTO;
import com.dingCreator.astrology.dto.activity.BaseActivityAwardRuleDTO;
import com.dingCreator.astrology.dto.activity.LuckyActivityAwardRuleDTO;
import com.dingCreator.astrology.dto.article.ArticleEquipmentItem;
import com.dingCreator.astrology.dto.article.ArticleItemDTO;
import com.dingCreator.astrology.entity.Activity;
import com.dingCreator.astrology.enums.ArticleTypeEnum;
import com.dingCreator.astrology.enums.activity.ActivityTypeEnum;
import com.dingCreator.astrology.enums.exception.ActivityExceptionEnum;
import com.dingCreator.astrology.mapper.ActivityMapper;
import com.dingCreator.astrology.util.RandomUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author ding
 * @date 2024/11/28
 */
public class LuckyActivityServiceImpl implements ActivityService {

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

                    List<JSONObject> articleObj =
                            jsonObj.<List<JSONObject>>getObject(BaseActivityAwardRuleDTO.FIELD_ARTICLE_ITEM_LIST, List.class);
                    List<ArticleItemDTO> articleItemList = articleObj.stream()
                            .map(artJson -> {
                                String artType = artJson.getString(Constants.ITEM_TYPE);
                                Class<? extends ArticleItemDTO> clazz = ArticleTypeEnum.getByType(artType).getClazz();
                                return artJson.toJavaObject(clazz);
                            }).collect(Collectors.toList());
                    lucky.setArticleItemList(articleItemList);
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
            joinAwardList.addAll(rule.getArticleItemList());
        }
        return joinAwardList;
    }

    @Override
    public <T extends BaseActivityAwardRuleDTO> String parseAwardRule2Json(List<T> list) {
        return JSONObject.toJSONString(list);
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
