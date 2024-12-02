package com.dingCreator.astrology.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.database.DatabaseProvider;
import com.dingCreator.astrology.dto.activity.ActivityDTO;
import com.dingCreator.astrology.dto.activity.ActivityStaticsDTO;
import com.dingCreator.astrology.dto.activity.BaseActivityAwardRuleDTO;
import com.dingCreator.astrology.dto.article.ArticleItemDTO;
import com.dingCreator.astrology.dto.organism.player.PlayerInfoDTO;
import com.dingCreator.astrology.entity.Activity;
import com.dingCreator.astrology.entity.ActivityRecord;
import com.dingCreator.astrology.entity.ActivityStatics;
import com.dingCreator.astrology.mapper.ActivityMapper;
import com.dingCreator.astrology.mapper.ActivityRecordMapper;
import com.dingCreator.astrology.mapper.ActivityStaticsMapper;
import com.dingCreator.astrology.util.LockUtil;
import com.dingCreator.astrology.vo.ArticleItemVO;

import java.util.List;
import java.util.Objects;

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
    default ActivityDTO getById(long activityId) {
        Activity activity = DatabaseProvider.getInstance().executeReturn(sqlSession ->
                sqlSession.getMapper(ActivityMapper.class).selectById(activityId)
        );
        ActivityDTO activityDTO = new ActivityDTO();
        BeanUtil.copyProperties(activity, activityDTO);
        return activityDTO;
    }

    /**
     * 参加活动
     *
     * @param activity 活动
     * @param statics  参与统计
     * @param info     玩家信息
     * @return 参与结果
     */
    default List<ArticleItemVO> joinActivity(ActivityDTO activity, ActivityStaticsDTO statics, PlayerInfoDTO info) {
        String lockKey = Constants.JOIN_ACTIVITY_LOCK_PREFIX + activity.getId() + Constants.UNDERLINE + info.getPlayerDTO().getId();
        return LockUtil.execute(lockKey, () -> {
            // 公共校验
            validateCommon(activity, statics, info);
            // 活动类型个性化校验
            validate();
            // 参与活动
            List<ArticleItemDTO> awards = join(activity);
            // 发放奖品并写入记录
            DatabaseProvider.getInstance().batchTransactionExecute(sqlSession -> {
                awards.forEach(award -> award.send2Player(info.getPlayerDTO().getId()));
                ActivityRecordMapper activityRecordMapper = sqlSession.getMapper(ActivityRecordMapper.class);
                activityRecordMapper.insert(new ActivityRecord());
                ActivityStaticsMapper activityStaticsMapper = sqlSession.getMapper(ActivityStaticsMapper.class);
                ActivityStatics newStatics = new ActivityStatics();
                if (Objects.nonNull(statics)) {

                } else {
                    activityStaticsMapper.insert(newStatics);
                }
            });
            // 转化奖品信息
            return null;
        });
    }


    /**
     * 公共校验
     *
     * @param activity 活动
     * @param statics  参与统计
     * @param info     玩家信息
     */
    default void validateCommon(ActivityDTO activity, ActivityStaticsDTO statics, PlayerInfoDTO info) {

    }

    /**
     * 具体活动校验
     */
    void validate();

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
     * @return 参与结果
     */
    List<ArticleItemDTO> join(ActivityDTO activity);
}
