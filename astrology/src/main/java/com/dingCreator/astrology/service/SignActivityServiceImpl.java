package com.dingCreator.astrology.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dingCreator.astrology.database.DatabaseProvider;
import com.dingCreator.astrology.dto.activity.ActivityDTO;
import com.dingCreator.astrology.dto.activity.BaseActivityAwardRuleDTO;
import com.dingCreator.astrology.dto.activity.SignActivityAwardRuleDTO;
import com.dingCreator.astrology.dto.article.ArticleItemDTO;
import com.dingCreator.astrology.entity.Activity;
import com.dingCreator.astrology.enums.activity.ActivityTypeEnum;
import com.dingCreator.astrology.enums.activity.SignAwardTypeEnum;
import com.dingCreator.astrology.enums.exception.ActivityExceptionEnum;
import com.dingCreator.astrology.mapper.ActivityMapper;
import com.dingCreator.astrology.request.ActivityAwardSettingReq;
import com.dingCreator.astrology.request.SignActivityAwardSettingReq;
import com.dingCreator.astrology.util.ArticleUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author ding
 * @date 2024/11/28
 */
public class SignActivityServiceImpl implements ActivityService {

    @Override
    public ActivityDTO getDefaultActivity() {
        Activity activity = DatabaseProvider.getInstance().executeReturn(sqlSession ->
                sqlSession.getMapper(ActivityMapper.class).selectOne(new QueryWrapper<Activity>()
                        .eq(Activity.ACTIVITY_TYPE, ActivityTypeEnum.SIGN.getCode())
                        .eq(Activity.DEFAULT_FLAG, true)));
        if (Objects.isNull(activity)) {
            throw ActivityExceptionEnum.DEFAULT_ACTIVITY_NOT_EXIST.getException();
        }
        return activity.convert();
    }

    @Override
    public void validateCreate(ActivityDTO activityDTO) {

    }

    @Override
    public void validateJoin() {

    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BaseActivityAwardRuleDTO> List<T> getAwardRule(String awardRuleJson) {
        List<JSONObject> jsonObjList = JSONObject.parseArray(awardRuleJson, JSONObject.class);
        List<SignActivityAwardRuleDTO> awardRuleList = jsonObjList.stream()
                .map(jsonObj -> {
                    SignActivityAwardRuleDTO sign = new SignActivityAwardRuleDTO();
                    String articleJson = jsonObj.getString(BaseActivityAwardRuleDTO.FIELD_ARTICLE_ITEM_SET);
                    String signAwardType = jsonObj.getString(SignActivityAwardRuleDTO.FIELD_SIGN_AWARD_TYPE);
                    sign.setSignAwardType(signAwardType);
                    sign.setArticleItemSet(ArticleUtil.convertSet(articleJson));
                    return sign;
                }).collect(Collectors.toList());
        return (List<T>) awardRuleList;
    }

    @Override
    public List<ArticleItemDTO> join(ActivityDTO activity, int times) {
        List<SignActivityAwardRuleDTO> awardRuleList = activity.getAwardRuleList().stream()
                .map(award -> (SignActivityAwardRuleDTO) award)
                .filter(award -> SignAwardTypeEnum.SINGLE.getTypeCode().equals(award.getSignAwardType()))
                .collect(Collectors.toList());
        return awardRuleList.stream()
                .flatMap(award -> award.getArticleItemSet().stream())
                .collect(Collectors.toList());
    }

    @Override
    public <T extends BaseActivityAwardRuleDTO> String parseAwardRule2Json(List<T> list) {
        return JSONObject.toJSONString(list);
    }

    @Override
    public void easySettingAward(ActivityDTO activityDTO, ActivityAwardSettingReq activityAwardSettingReq) {
        SignActivityAwardSettingReq req = (SignActivityAwardSettingReq) activityAwardSettingReq;
        List<BaseActivityAwardRuleDTO> ruleList = req.getSignAwardMap().entrySet().stream()
                .map(award -> {
                    SignActivityAwardRuleDTO rule = new SignActivityAwardRuleDTO();
                    rule.setSignAwardType(award.getKey().getTypeCode());
                    rule.setArticleItemSet(award.getValue());
                    return rule;
                }).collect(Collectors.toList());
        activityDTO.setAwardRuleList(ruleList);
    }

    @Override
    public List<String> queryAwardList(ActivityDTO activityDTO) {
        List<BaseActivityAwardRuleDTO> ruleList = activityDTO.getAwardRuleList();
        if (Objects.isNull(ruleList)) {
            return new ArrayList<>();
        }
        return ruleList.stream().map(rule -> {
            SignActivityAwardRuleDTO signRule = (SignActivityAwardRuleDTO) rule;
            SignAwardTypeEnum e = SignAwardTypeEnum.getByTypeCode(signRule.getSignAwardType());
            if (Objects.isNull(e)) {
                return "";
            }
            return e.getChnDesc() + ":" + signRule.getArticleItemSet().stream()
                    .map(articleItemDTO -> articleItemDTO.view().getName() + "*" + articleItemDTO.view().getCount())
                    .collect(Collectors.joining(","));
        }).collect(Collectors.toList());
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
