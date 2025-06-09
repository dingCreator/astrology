package com.dingCreator.astrology.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.database.DatabaseProvider;
import com.dingCreator.astrology.dto.activity.ActivityDTO;
import com.dingCreator.astrology.dto.activity.ActivityStaticsDTO;
import com.dingCreator.astrology.dto.activity.BaseActivityAwardRuleDTO;
import com.dingCreator.astrology.dto.article.ArticleItemDTO;
import com.dingCreator.astrology.dto.organism.player.PlayerAssetDTO;
import com.dingCreator.astrology.dto.organism.player.PlayerInfoDTO;
import com.dingCreator.astrology.entity.Activity;
import com.dingCreator.astrology.entity.ActivityRecord;
import com.dingCreator.astrology.entity.ActivityStatics;
import com.dingCreator.astrology.enums.activity.ActivityLimitTypeEnum;
import com.dingCreator.astrology.enums.exception.ActivityExceptionEnum;
import com.dingCreator.astrology.mapper.ActivityMapper;
import com.dingCreator.astrology.mapper.ActivityRecordMapper;
import com.dingCreator.astrology.mapper.ActivityStaticsMapper;
import com.dingCreator.astrology.request.ActivityAwardSettingReq;
import com.dingCreator.astrology.request.ActivityPageQryReq;
import com.dingCreator.astrology.util.ActivityUtil;
import com.dingCreator.astrology.util.LockUtil;
import com.dingCreator.astrology.vo.ArticleItemVO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author ding
 * @date 2024/11/26
 */
public interface ActivityService {

    /**
     * 根据ID获取活动信息
     *
     * @param activityId 活动ID
     * @return 活动信息
     */
    static ActivityDTO getById(long activityId) {
        Activity activity = DatabaseProvider.getInstance().executeReturn(sqlSession ->
                sqlSession.getMapper(ActivityMapper.class).selectById(activityId));
        if (Objects.isNull(activity)) {
            throw ActivityExceptionEnum.ACTIVITY_NOT_EXIST.getException();
        }
        return activity.convert();
    }

    /**
     * 根据活动名称获取活动
     *
     * @param activityName 活动名称
     * @return 活动信息
     */
    static ActivityDTO getByName(String activityName) {
        Activity activity = DatabaseProvider.getInstance().executeReturn(sqlSession ->
                sqlSession.getMapper(ActivityMapper.class).selectOne(new QueryWrapper<Activity>()
                        .eq(Activity.ACTIVITY_NAME, activityName)));
        if (Objects.isNull(activity)) {
            throw ActivityExceptionEnum.ACTIVITY_NOT_EXIST.getException();
        }
        return activity.convert();
    }

    /**
     * 活动列表
     *
     * @param qryReq 查询条件
     * @return 活动列表
     */
    static List<ActivityDTO> listActivity(ActivityPageQryReq qryReq) {
        List<Activity> activityList = DatabaseProvider.getInstance().executeReturn(sqlSession -> {
                    boolean admin = Objects.nonNull(qryReq.getAdmin()) && qryReq.getAdmin();
                    QueryWrapper<Activity> wrapper = new QueryWrapper<Activity>()
                            .eq(Objects.nonNull(qryReq.getActivityType()), Activity.ACTIVITY_TYPE, qryReq.getActivityType())
                            .eq(admin, Activity.ENABLED, true)
                            .ge(admin, Activity.START_TIME, LocalDateTime.now())
                            .le(admin, Activity.END_TIME, LocalDateTime.now());
                    return sqlSession.getMapper(ActivityMapper.class).selectList(wrapper);
                }
        );
        return activityList.stream().map(Activity::convert).collect(Collectors.toList());
    }

    /**
     * 获取参与统计
     *
     * @param activityId    活动ID
     * @param playerId      玩家ID
     * @param limitTypeEnum 限制类型
     * @return 参与统计
     */
    default ActivityStaticsDTO getStatics(Long activityId, Long playerId, ActivityLimitTypeEnum limitTypeEnum) {
        ActivityStatics statics = DatabaseProvider.getInstance().executeReturn(sqlSession -> {
            String pointInTime = limitTypeEnum.getPointInTime().get();
            return sqlSession.getMapper(ActivityStaticsMapper.class)
                    .selectOne(new QueryWrapper<ActivityStatics>()
                            .eq(ActivityStatics.ACTIVITY_ID, activityId)
                            .eq(ActivityStatics.PLAYER_ID, playerId)
                            .eq(Objects.nonNull(pointInTime), ActivityStatics.DATE_TIME, pointInTime));
        });
        if (Objects.nonNull(statics)) {
            return ActivityStaticsDTO.builder()
                    .id(statics.getId())
                    .activityId(statics.getActivityId())
                    .playerId(statics.getPlayerId())
                    .dateTime(statics.getDateTime())
                    .count(statics.getCount()).build();
        }
        return null;
    }

    /**
     * 参加活动
     *
     * @param activity 活动
     * @param info     玩家信息
     * @return 参与结果
     */
    default List<ArticleItemVO> joinActivity(ActivityDTO activity, PlayerInfoDTO info) {
        return joinActivity(activity, info, 1);
    }

    /**
     * 参加活动
     *
     * @param activity 活动
     * @param info     玩家信息
     * @param times    连续参与次数
     * @return 参与结果
     */
    default List<ArticleItemVO> joinActivity(ActivityDTO activity, PlayerInfoDTO info, int times) {
        String lockKey = Constants.JOIN_ACTIVITY_LOCK_PREFIX + activity.getId() + Constants.UNDERLINE + info.getPlayerDTO().getId();
        return LockUtil.execute(lockKey, () -> {
            // 查询参与统计
            ActivityStaticsDTO statics = getStatics(activity.getId(), info.getPlayerDTO().getId(), activity.getLimitTypeEnum());
            // 公共校验
            validateJoinCommon(activity, statics, info, times);
            // 活动类型个性化校验
            validateJoin();
            // 参与活动
            List<ArticleItemDTO> awards = join(activity, times);
            // 发放奖品并写入记录
            DatabaseProvider.getInstance().transactionExecute(sqlSession -> {
                Long playerId = info.getPlayerDTO().getId();
                // 扣除参与费用
                List<PlayerAssetDTO> changeList = activity.getCostMap().entrySet().stream()
                        .map(entry -> PlayerAssetDTO.builder().playerId(playerId)
                                .assetType(entry.getKey().getCode()).assetCnt(entry.getValue() * times).build())
                        .collect(Collectors.toList());
                PlayerService.getInstance().changeAsset(info, changeList);
                // 发放奖励
                awards.forEach(award -> award.send2Player(info.getPlayerDTO().getId(), 1));
                // 插入记录
                ActivityRecordMapper activityRecordMapper = sqlSession.getMapper(ActivityRecordMapper.class);
                ActivityStaticsMapper activityStaticsMapper = sqlSession.getMapper(ActivityStaticsMapper.class);

                ActivityRecord record = ActivityRecord.builder()
                        .activityId(activity.getId())
                        .playerId(info.getPlayerDTO().getId())
                        .createTime(LocalDateTime.now())
                        .joinTimes(times)
                        .build();
                activityRecordMapper.insert(record);

                ActivityStatics newStatics;
                if (Objects.nonNull(statics)) {
                    statics.setCount(statics.getCount() + times);
                    newStatics = ActivityStatics.builder()
                            .id(statics.getId())
                            .activityId(statics.getActivityId())
                            .playerId(statics.getPlayerId())
                            .dateTime(statics.getDateTime())
                            .count(statics.getCount()).build();
                    activityStaticsMapper.updateById(newStatics);
                } else {
                    String pointInTime = activity.getLimitTypeEnum().getPointInTime().get();
                    newStatics = ActivityStatics.builder()
                            .activityId(activity.getId())
                            .count(times)
                            .dateTime(pointInTime)
                            .playerId(playerId).build();
                    activityStaticsMapper.insert(newStatics);
                }
            });
            // 转化奖品信息
            return awards.stream().map(ArticleItemDTO::view).collect(Collectors.toList());
        });
    }

    /**
     * 创建活动
     *
     * @param activityDTO 活动
     */
    default void createActivity(ActivityDTO activityDTO) {
        validateCreate(activityDTO);
        DatabaseProvider.getInstance().execute(sqlSession -> {
            Activity activity = Activity.builder()
                    .activityName(activityDTO.getActivityName())
                    .activityType(activityDTO.getActivityType().getCode())
                    .awardRuleJson(parseAwardRule2Json(activityDTO.getAwardRuleList()))
                    .limit(ActivityUtil.getLimitBit(activityDTO.getLimitTypeEnum(), activityDTO.getLimitTypeCnt()))
                    .costJson(ActivityUtil.formatCostMapJson(activityDTO.getCostMap()))
                    .defaultFlag(activityDTO.getDefaultFlag())
                    .startTime(activityDTO.getStartTime())
                    .endTime(activityDTO.getEndTime())
                    .enabled(true)
                    .build();
            sqlSession.getMapper(ActivityMapper.class).insert(activity);
        });
    }

    /**
     * 获取默认活动
     *
     * @return 活动
     */
    ActivityDTO getDefaultActivity();

    /**
     * 创建活动校验
     *
     * @param activityDTO 创建活动参数
     */
    void validateCreate(ActivityDTO activityDTO);

    /**
     * 参与活动公共校验
     *
     * @param activity 活动
     * @param statics  参与统计
     * @param info     玩家信息
     * @param times    连续参与次数
     */
    default void validateJoinCommon(ActivityDTO activity, ActivityStaticsDTO statics, PlayerInfoDTO info, int times) {
        validateJoinTimes(activity, statics, times);
        validateTime(activity);
        validateEnabled(activity);
        validateCost(activity, info, times);
    }

    /**
     * 参与次数校验
     *
     * @param activity 活动
     * @param statics  参与统计
     * @param times    连续参与次数
     */
    default void validateJoinTimes(ActivityDTO activity, ActivityStaticsDTO statics, int times) {
        if (ActivityLimitTypeEnum.NONE.equals(activity.getLimitTypeEnum())) {
            return;
        }
        if (Objects.isNull(statics) && activity.getLimitTypeCnt() < times) {
            throw ActivityExceptionEnum.OVER_JOIN_TIMES_LIMIT.getException();
        }
        if (Objects.nonNull(statics) && statics.getCount() + times > activity.getLimitTypeCnt()) {
            throw ActivityExceptionEnum.OVER_JOIN_TIMES_LIMIT.getException();
        }
    }

    /**
     * 参与时间校验
     *
     * @param activity 活动
     */
    default void validateTime(ActivityDTO activity) {
        if (LocalDateTime.now().isBefore(activity.getStartTime())) {
            throw ActivityExceptionEnum.ACTIVITY_NOT_START.getException();
        }
        if (LocalDateTime.now().isAfter(activity.getEndTime())) {
            throw ActivityExceptionEnum.ACTIVITY_HAS_BEEN_END.getException();
        }
    }

    /**
     * 启用禁用校验
     *
     * @param activity 活动
     */
    default void validateEnabled(ActivityDTO activity) {
        if (!activity.getEnabled()) {
            throw ActivityExceptionEnum.ACTIVITY_NOT_ENABLED.getException();
        }
    }

    /**
     * 参与花费校验
     *
     * @param activity 活动
     * @param info     玩家信息
     * @param times    连续参与次数
     */
    default void validateCost(ActivityDTO activity, PlayerInfoDTO info, int times) {
        if (CollectionUtils.isNotEmpty(activity.getCostMap())) {
            activity.getCostMap().forEach((assetTypeEnum, cost) -> {
                long realCost = cost * times;
                if (!assetTypeEnum.getValidateCache().apply(info.getAssetList(), realCost)) {
                    throw ActivityExceptionEnum.NOT_ENOUGH_ASSET.getException();
                }
            });
        }
    }

    /**
     * 参与活动校验
     */
    void validateJoin();

    /**
     * 奖品规则
     *
     * @param <T>           奖品规则实体类
     * @param awardRuleJson 奖品规则json
     * @return 奖品规则
     */
    <T extends BaseActivityAwardRuleDTO> List<T> getAwardRule(String awardRuleJson);

    /**
     * 具体的参与逻辑
     *
     * @param activity 活动
     * @param times    连续参与次数
     * @return 参与结果
     */
    List<ArticleItemDTO> join(ActivityDTO activity, int times);

    /**
     * 奖品规则转化为json
     *
     * @param tList 奖品规则
     * @param <T>   规则类型
     * @return json
     */
    <T extends BaseActivityAwardRuleDTO> String parseAwardRule2Json(List<T> tList);

    /**
     * 编辑活动
     *
     * @param activity 活动
     */
    default void updateActivity(ActivityDTO activity) {
        Assert.notNull(activity.getId(), "活动ID不能为空");
        Activity.ActivityBuilder builder = Activity.builder().id(activity.getId());
        // 只允许更新以下字段
        if (Objects.nonNull(activity.getActivityName())) {
            builder.activityName(activity.getActivityName());
        }
        if (Objects.nonNull(activity.getStartTime())) {
            builder.startTime(activity.getStartTime());
        }
        if (Objects.nonNull(activity.getEndTime())) {
            builder.endTime(activity.getEndTime());
        }
        if (Objects.nonNull(activity.getLimitTypeEnum()) && Objects.nonNull(activity.getLimitTypeCnt())) {
            builder.limit(ActivityUtil.getLimitBit(activity.getLimitTypeEnum(), activity.getLimitTypeCnt()));
        }
        if (CollectionUtils.isNotEmpty(activity.getCostMap())) {
            builder.costJson(ActivityUtil.formatCostMapJson(activity.getCostMap()));
        }
        if (Objects.nonNull(activity.getDefaultFlag())) {
            builder.defaultFlag(activity.getDefaultFlag());
        }
        if (CollectionUtils.isNotEmpty(activity.getAwardRuleList())) {
            builder.awardRuleJson(parseAwardRule2Json(activity.getAwardRuleList()));
        }
        if (Objects.nonNull(activity.getEnabled())) {
            builder.enabled(activity.getEnabled());
        }
        Activity newActivity = builder.build();
        DatabaseProvider.getInstance().execute(sqlSession -> sqlSession.getMapper(ActivityMapper.class).updateById(newActivity));
    }

    /**
     * 快捷生成奖品规则
     *
     * @param activityDTO             活动
     * @param activityAwardSettingReq 快捷设置奖品配置
     */
    void easySettingAward(ActivityDTO activityDTO, ActivityAwardSettingReq activityAwardSettingReq);

    List<String> queryAwardList(ActivityDTO activityDTO);
}
