package com.dingCreator.astrology.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.database.DatabaseProvider;
import com.dingCreator.astrology.entity.SkillBag;
import com.dingCreator.astrology.enums.BelongToEnum;
import com.dingCreator.astrology.enums.exception.SkillExceptionEnum;
import com.dingCreator.astrology.mapper.SkillBagMapper;
import com.dingCreator.astrology.util.LockUtil;

import java.util.List;
import java.util.Objects;

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
                    bag = SkillBag.builder().playerId(playerId).skillId(skillId).skillCnt(count).build();
                    mapper.insert(bag);
                } else {
                    bag.setSkillCnt(bag.getSkillCnt() + count);
                    mapper.updateById(bag);
                }
            });
        });
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
    }

    public List<SkillBag> pageSkillBag(Long playerId, int pageIndex, int pageSize) {
        return DatabaseProvider.getInstance().executeReturn(sqlSession -> sqlSession.getMapper(SkillBagMapper.class)
                .selectList(new QueryWrapper<SkillBag>()
                        .eq(SkillBag.PLAYER_ID, playerId)
                        .ge(SkillBag.SKILL_CNT, 0)
                        .last(" limit " + (pageIndex - 1) * pageSize + " , " + pageSize)
                )
        );
    }

    public int countSkillBag(Long playerId) {
        return DatabaseProvider.getInstance().executeReturn(sqlSession -> sqlSession.getMapper(SkillBagMapper.class)
                .selectCount(new QueryWrapper<SkillBag>().eq(SkillBag.PLAYER_ID, playerId).ge(SkillBag.SKILL_CNT, 0)));
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
