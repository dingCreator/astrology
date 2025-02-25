package com.dingCreator.astrology.behavior;

import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.dto.activity.ActivityDTO;
import com.dingCreator.astrology.enums.activity.ActivityTypeEnum;
import com.dingCreator.astrology.request.ActivityAwardSettingReq;
import com.dingCreator.astrology.request.LuckyActivityAwardSettingReq;
import com.dingCreator.astrology.request.ActivityPageQryReq;
import com.dingCreator.astrology.response.PageResponse;
import com.dingCreator.astrology.service.ActivityService;
import com.dingCreator.astrology.util.PageUtil;
import com.dingCreator.astrology.vo.ActivityAwardVO;
import com.dingCreator.astrology.vo.ArticleItemVO;

import java.util.List;

/**
 * @author ding
 * @date 2024/11/26
 */
public class ActivityBehavior {

    /**
     * 参加活动
     *
     * @param activityName 活动名称
     * @param playerId     玩家ID
     * @return 参与结果
     */
    public List<ArticleItemVO> joinActivity(String activityName, long playerId) {
        ActivityDTO activityDTO = ActivityService.getByName(activityName);
        return activityDTO.getActivityType().getService()
                .joinActivity(activityDTO, PlayerCache.getPlayerById(playerId));
    }

    /**
     * 参与指定类型的活动
     *
     * @param activityTypeEnum 活动类型
     * @param playerId         玩家ID
     * @param times            连续参与次数
     * @return 参与结果
     */
    public List<ArticleItemVO> joinActivity(ActivityTypeEnum activityTypeEnum, long playerId, int times) {
        ActivityService service = activityTypeEnum.getService();
        ActivityDTO activity = service.getDefaultActivity();
        return service.joinActivity(activity, PlayerCache.getPlayerById(playerId), times);
    }

    /**
     * 查询活动列表
     *
     * @param pageIndex 页码
     * @param pageSize  页面大小
     * @param qryReq    查询条件
     * @return 活动列表
     */
    public PageResponse<ActivityDTO> qryActivityPage(int pageIndex, int pageSize, ActivityPageQryReq qryReq) {
        List<ActivityDTO> activityList = ActivityService.listActivity(qryReq);
        return PageUtil.buildPage(activityList, pageIndex, pageSize);
    }

    /**
     * 编辑活动
     *
     * @param activity 活动
     */
    public void updateActivity(ActivityDTO activity) {
        activity.getActivityType().getService().updateActivity(activity);
    }

    /**
     * 创建活动
     *
     * @param activityDTO 活动
     */
    public void createActivity(ActivityDTO activityDTO) {
        activityDTO.getActivityType().getService().createActivity(activityDTO);
    }

    public PageResponse<String> queryAwardPage(String activityName, int pageIndex, int pageSize) {
        ActivityDTO activityDTO = ActivityService.getByName(activityName);
        ActivityService service = activityDTO.getActivityType().getService();
        List<String> list = service.queryAwardList(activityDTO);
        return PageUtil.buildPage(list, pageIndex, pageSize);
    }

    public PageResponse<String> queryAwardPage(ActivityTypeEnum activityTypeEnum, int pageIndex, int pageSize) {
        ActivityService service = activityTypeEnum.getService();
        ActivityDTO activityDTO = service.getDefaultActivity();
        List<String> list = service.queryAwardList(activityDTO);
        return PageUtil.buildPage(list, pageIndex, pageSize);
    }

    public void easySettingAward(ActivityDTO activityDTO, ActivityAwardSettingReq activityAwardSettingReq) {
        activityDTO.getActivityType().getService().easySettingAward(activityDTO, activityAwardSettingReq);
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
