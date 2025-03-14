package com.dingCreator.astrology.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.database.DatabaseProvider;
import com.dingCreator.astrology.entity.PlayerHerb;
import com.dingCreator.astrology.mapper.PlayerHerbMapper;
import com.dingCreator.astrology.util.LockUtil;

import java.util.List;
import java.util.Objects;

/**
 * @author ding
 * @date 2025/3/11
 */
public class PlayerHerbService {

    public void addHerb(long playerId, long herbId, int cnt) {
        LockUtil.execute(Constants.PILL_LOCK_PREFIX + playerId,
                () -> DatabaseProvider.getInstance().transactionExecute(sqlSession -> {
                    PlayerHerbMapper mapper = sqlSession.getMapper(PlayerHerbMapper.class);
                    PlayerHerb playerHerb = mapper.selectOne(new QueryWrapper<PlayerHerb>()
                            .eq(PlayerHerb.PLAYER_ID, playerId)
                            .eq(PlayerHerb.HERB_ID, herbId));
                    if (Objects.nonNull(playerHerb)) {
                        playerHerb.setHerbCnt(playerHerb.getHerbCnt() + cnt);
                        mapper.updateById(playerHerb);
                    } else {
                        playerHerb = PlayerHerb.builder().playerId(playerId).herbId(herbId).herbCnt(cnt).build();
                        mapper.insert(playerHerb);
                    }
                })
        );
    }

    public List<PlayerHerb> listPlayerHerb(Long playerId, int pageIndex, int pageSize) {
        int index = pageIndex - 1;
        return DatabaseProvider.getInstance().executeReturn(sqlSession -> {
            PlayerHerbMapper mapper = sqlSession.getMapper(PlayerHerbMapper.class);
            return mapper.selectList(new QueryWrapper<PlayerHerb>()
                    .eq(PlayerHerb.PLAYER_ID, playerId)
                    .gt(PlayerHerb.HERB_CNT, 0)
                    .orderByDesc(PlayerHerb.HERB_ID)
                    .last("limit " + index * pageSize + "," + pageSize));
        });
    }

    public int countPlayerHerb(Long playerId) {
        return DatabaseProvider.getInstance().executeReturn(sqlSession -> sqlSession.getMapper(PlayerHerbMapper.class)
                .selectCount(new QueryWrapper<PlayerHerb>().eq(PlayerHerb.PLAYER_ID, playerId).gt(PlayerHerb.HERB_CNT, 0)));
    }

    private PlayerHerbService() {

    }

    public static PlayerHerbService getInstance() {
        return Holder.HOLDER;
    }

    private static class Holder {
        public static final PlayerHerbService HOLDER = new PlayerHerbService();
    }
}
