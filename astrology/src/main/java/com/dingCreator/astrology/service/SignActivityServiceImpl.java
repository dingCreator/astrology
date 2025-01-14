package com.dingCreator.astrology.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.database.DatabaseProvider;
import com.dingCreator.astrology.dto.activity.ActivityDTO;
import com.dingCreator.astrology.dto.activity.BaseActivityAwardRuleDTO;
import com.dingCreator.astrology.dto.activity.SignActivityAwardRuleDTO;
import com.dingCreator.astrology.dto.article.ArticleItemDTO;
import com.dingCreator.astrology.entity.Activity;
import com.dingCreator.astrology.enums.ArticleTypeEnum;
import com.dingCreator.astrology.enums.activity.ActivityTypeEnum;
import com.dingCreator.astrology.enums.exception.ActivityExceptionEnum;
import com.dingCreator.astrology.mapper.ActivityMapper;
import com.dingCreator.astrology.request.ActivityAwardSettingReq;

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

                    List<JSONObject> articleObj =
                            jsonObj.<List<JSONObject>>getObject(BaseActivityAwardRuleDTO.FIELD_ARTICLE_ITEM_LIST, List.class);
                    List<ArticleItemDTO> articleItemList = articleObj.stream()
                            .map(artJson -> {
                                String artType = artJson.getString(Constants.ITEM_TYPE);
                                Class<? extends ArticleItemDTO> clazz = ArticleTypeEnum.getByType(artType).getClazz();
                                return artJson.toJavaObject(clazz);
                            }).collect(Collectors.toList());
                    sign.setArticleItemList(articleItemList);
                    return sign;
                }).collect(Collectors.toList());
        return (List<T>) awardRuleList;
    }

    @Override
    public List<ArticleItemDTO> join(ActivityDTO activity, int times) {
        List<SignActivityAwardRuleDTO> awardRuleList = activity.getAwardRuleList().stream()
                .map(award -> (SignActivityAwardRuleDTO) award)
                .collect(Collectors.toList());
        return awardRuleList.stream()
                .flatMap(award -> award.getArticleItemList().stream())
                .collect(Collectors.toList());
    }

    @Override
    public <T extends BaseActivityAwardRuleDTO> String parseAwardRule2Json(List<T> list) {
        return JSONObject.toJSONString(list);
    }

    @Override
    public void easySettingAward(ActivityDTO activityDTO, ActivityAwardSettingReq activityAwardSettingReq) {
        activityDTO.getActivityType().getService().easySettingAward(activityDTO, activityAwardSettingReq);
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
