package com.dingCreator.astrology.behavior;

import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.dto.activity.ActivityDTO;
import com.dingCreator.astrology.enums.activity.ActivityTypeEnum;
import com.dingCreator.astrology.request.ActivityAwardSettingReq;
import com.dingCreator.astrology.request.ActivityListQryReq;
import com.dingCreator.astrology.response.PageResponse;
import com.dingCreator.astrology.service.ActivityService;
import com.dingCreator.astrology.util.PageUtil;
import com.dingCreator.astrology.vo.JoinActivityResultVO;

import java.util.List;
import java.util.stream.Collectors;

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
    public JoinActivityResultVO joinActivity(String activityName, long playerId, int times) {
        ActivityDTO activityDTO = ActivityService.getByName(activityName);
        return activityDTO.getActivityType().getService()
                .joinActivity(activityDTO, PlayerCache.getPlayerById(playerId), times);
    }

    /**
     * 参与指定类型的活动
     *
     * @param activityTypeEnum 活动类型
     * @param playerId         玩家ID
     * @param times            连续参与次数
     * @return 参与结果
     */
    public JoinActivityResultVO joinActivity(ActivityTypeEnum activityTypeEnum, long playerId, int times) {
        ActivityService service = activityTypeEnum.getService();
        ActivityDTO activity = service.getDefaultActivity();
        return service.joinActivity(activity, PlayerCache.getPlayerById(playerId), times);
    }

    /**
     * 参与指定类型的所有活动
     *
     * @param activityTypeEnum 活动类型
     * @param playerId         玩家ID
     * @return 参与结果
     */
    public JoinActivityResultVO joinAllActivity(ActivityTypeEnum activityTypeEnum, long playerId) {
        List<ActivityDTO> activities = ActivityService.listActivity(ActivityListQryReq.builder().activityType(activityTypeEnum.getCode()).admin(false).build());
        ActivityService service = activityTypeEnum.getService();
        List<JoinActivityResultVO> voList = activities.stream()
                .map(activity -> service.joinActivity(activity, PlayerCache.getPlayerById(playerId)))
                .collect(Collectors.toList());
        return voList.stream().reduce((vo1, vo2) -> {
            vo1.getCostMap().forEach((assetType, val) -> {
                if (vo2.getCostMap().containsKey(assetType)) {
                    vo2.getCostMap().put(assetType, vo2.getCostMap().get(assetType) + val);
                } else {
                    vo2.getCostMap().put(assetType, val);
                }
            });
            vo2.getItemVOList().addAll(vo1.getItemVOList());
            return vo2;
        }).orElse(null);
    }

    /**
     * 查询活动列表
     *
     * @param pageIndex 页码
     * @param pageSize  页面大小
     * @param qryReq    查询条件
     * @return 活动列表
     */
    public PageResponse<ActivityDTO> qryActivityPage(int pageIndex, int pageSize, ActivityListQryReq qryReq) {
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
