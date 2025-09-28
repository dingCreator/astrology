package com.dingCreator.astrology.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dingCreator.astrology.cache.SkillCache;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.database.DatabaseProvider;
import com.dingCreator.astrology.entity.SkillBag;
import com.dingCreator.astrology.enums.BelongToEnum;
import com.dingCreator.astrology.enums.exception.SkillExceptionEnum;
import com.dingCreator.astrology.mapper.SkillBagMapper;
import com.dingCreator.astrology.util.LockUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author ding
 * @date 2025/3/31
 */
public class SkillBagService {

    public void sendSkill(Long playerId, Long skillId, int count) {
        LockUtil.execute(Constants.SKILL_BAG_LOCK_PREFIX + playerId, () -> {
            DatabaseProvider.getInstance().execute(sqlSession -> {
                SkillBagMapper mapper = sqlSession.getMapper(SkillBagMapper.class);
                SkillBag bag = mapper.selectOne(new QueryWrapper<SkillBag>()
                        .eq(SkillBag.PLAYER_ID, playerId)
                        .eq(SkillBag.SKILL_ID, skillId)
                );
                if (Objects.isNull(bag)) {
                    if (count < 0) {
                        throw SkillExceptionEnum.NOT_ENOUGH_SKILL.getException();
                    }
                    bag = SkillBag.builder().playerId(playerId).skillId(skillId).skillCnt(count).build();
                    mapper.insert(bag);
                } else {
                    if (bag.getSkillCnt() + count < 0) {
                        throw SkillExceptionEnum.NOT_ENOUGH_SKILL.getException();
                    }
                    bag.setSkillCnt(bag.getSkillCnt() + count);
                    mapper.updateById(bag);
                }
            });
        });
    }

    public void batchSendSkill(List<SkillBag> skillBagList) {
        List<String> lockNames = skillBagList.stream().map(SkillBag::getPlayerId).map(Objects::toString).collect(Collectors.toList());
        LockUtil.execute(Constants.SKILL_BAG_LOCK_PREFIX, lockNames, () ->
            DatabaseProvider.getInstance().batchTransactionExecute(sqlSession -> {
                SkillBagMapper mapper = sqlSession.getMapper(SkillBagMapper.class);
                List<Long> skillIds = new ArrayList<>();
                List<Long> playerIds = new ArrayList<>();
                skillBagList.forEach(skillBag -> {
                    skillIds.add(skillBag.getSkillId());
                    playerIds.add(skillBag.getPlayerId());
                });

                List<SkillBag> oldList = mapper.selectList(new QueryWrapper<SkillBag>()
                        .in(SkillBag.PLAYER_ID, playerIds).in(SkillBag.SKILL_ID, skillIds));
                Map<String, SkillBag> skillBagMap = oldList.stream().collect(Collectors.toMap(
                        skillBag -> skillBag.getPlayerId() + "_" + skillBag.getSkillId(), Function.identity()
                ));
                skillBagList.forEach(skillBag -> {
                    String key = skillBag.getPlayerId() + "_" + skillBag.getSkillId();
                    if (skillBagMap.containsKey(key)) {
                        SkillBag old = skillBagMap.get(key);
                        old.setSkillCnt(skillBag.getSkillCnt() + old.getSkillCnt());
                        mapper.updateById(old);
                    } else {
                        mapper.insert(skillBag);
                    }
                });
            })
        );
    }

    public void learnSkill(Long playerId, Long skillId) {
        LockUtil.execute(Constants.SKILL_BAG_LOCK_PREFIX + playerId, () ->
                DatabaseProvider.getInstance().transactionExecute(sqlSession -> {
                    SkillBagMapper mapper = sqlSession.getMapper(SkillBagMapper.class);
                    SkillBag skillBag = mapper.selectOne(
                            new QueryWrapper<SkillBag>().eq(SkillBag.PLAYER_ID, playerId).eq(SkillBag.SKILL_ID, skillId));
                    if (Objects.isNull(skillBag) || skillBag.getSkillCnt() <= 0) {
                        throw SkillExceptionEnum.DONT_HAVE_SKILL.getException();
                    }
                    skillBag.setSkillCnt(skillBag.getSkillCnt() - 1);
                    mapper.updateById(skillBag);
                    SkillBelongToService.getInstance().createSkillBelongTo(BelongToEnum.PLAYER.getBelongTo(), playerId, skillId);
                })
        );
        SkillCache.deleteInactiveSkillCache(playerId);
    }

    public List<SkillBag> pageSkillBag(Long playerId, int pageIndex, int pageSize) {
        return DatabaseProvider.getInstance().executeReturn(sqlSession -> sqlSession.getMapper(SkillBagMapper.class)
                .selectList(new QueryWrapper<SkillBag>()
                        .eq(SkillBag.PLAYER_ID, playerId)
                        .gt(SkillBag.SKILL_CNT, 0)
                        .last(" limit " + (pageIndex - 1) * pageSize + " , " + pageSize)
                )
        );
    }

    public int countSkillBag(Long playerId) {
        return DatabaseProvider.getInstance().executeReturn(sqlSession -> sqlSession.getMapper(SkillBagMapper.class)
                .selectCount(new QueryWrapper<SkillBag>().eq(SkillBag.PLAYER_ID, playerId).gt(SkillBag.SKILL_CNT, 0)));
    }

    private SkillBagService() {

    }

    public static SkillBagService getInstance() {
        return Holder.SERVICE;
    }

    private static class Holder {
        public static final SkillBagService SERVICE = new SkillBagService();
    }
}
