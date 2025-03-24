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

    /**
     * 增加药材
     *
     * @param playerId 玩家ID
     * @param herbId   药材ID
     * @param cnt      数量
     */
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

    /**
     * 玩家药材列表
     *
     * @param playerId  玩家ID
     * @param pageIndex 页码
     * @param pageSize  页面大小
     * @return 药材列表
     */
    public List<PlayerHerb> listPlayerHerb(Long playerId, int pageIndex, int pageSize) {
        int index = pageIndex - 1;
        return DatabaseProvider.getInstance().executeReturn(sqlSession -> {
            PlayerHerbMapper mapper = sqlSession.getMapper(PlayerHerbMapper.class);
            return mapper.selectList(new QueryWrapper<PlayerHerb>()
                    .eq(PlayerHerb.PLAYER_ID, playerId)
                    .gt(PlayerHerb.HERB_CNT, 0)
                    .orderByDesc(PlayerHerb.HERB_CNT)
                    .last("limit " + index * pageSize + "," + pageSize));
        });
    }

    public PlayerHerb getHerbById(Long playerId, Long herbId) {
        return DatabaseProvider.getInstance().executeReturn(sqlSession ->
                sqlSession.getMapper(PlayerHerbMapper.class).selectOne(
                        new QueryWrapper<PlayerHerb>()
                                .eq(PlayerHerb.PLAYER_ID, playerId)
                                .eq(PlayerHerb.HERB_ID, herbId)
                                .gt(PlayerHerb.HERB_CNT, 0)
                ));
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
