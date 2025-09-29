package com.dingCreator.astrology.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.database.DatabaseProvider;
import com.dingCreator.astrology.entity.PlayerHerb;
import com.dingCreator.astrology.entity.SkillBag;
import com.dingCreator.astrology.enums.exception.HerbExceptionEnum;
import com.dingCreator.astrology.mapper.PlayerHerbMapper;
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
                        if (playerHerb.getHerbCnt() + cnt < 0) {
                            throw HerbExceptionEnum.NOT_ENOUGH_HERB.getException();
                        }
                        playerHerb.setHerbCnt(playerHerb.getHerbCnt() + cnt);
                        mapper.updateById(playerHerb);
                    } else {
                        if (cnt < 0) {
                            throw HerbExceptionEnum.NOT_ENOUGH_HERB.getException();
                        }
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

    public void batchSendHerb(List<PlayerHerb> herbList) {
        List<String> lockNames = herbList.stream().map(PlayerHerb::getPlayerId).map(Objects::toString).collect(Collectors.toList());
        LockUtil.execute(Constants.HERB_LOCK_PREFIX, lockNames, () ->
                DatabaseProvider.getInstance().batchTransactionExecute(sqlSession -> {
                    PlayerHerbMapper mapper = sqlSession.getMapper(PlayerHerbMapper.class);
                    List<Long> herbIds = new ArrayList<>();
                    List<Long> playerIds = new ArrayList<>();
                    herbList.forEach(herb -> {
                        herbIds.add(herb.getHerbId());
                        playerIds.add(herb.getPlayerId());
                    });
                    List<PlayerHerb> oldList = mapper.selectList(new QueryWrapper<PlayerHerb>()
                            .in(PlayerHerb.PLAYER_ID, playerIds).in(PlayerHerb.HERB_ID, herbIds));
                    Map<String, PlayerHerb> playerHerbMap = oldList.stream().collect(Collectors.toMap(
                            playerHerb -> playerHerb.getPlayerId() + "_" + playerHerb.getHerbId(), Function.identity()
                    ));
                    herbList.forEach(playerHerb -> {
                        String key = playerHerb.getPlayerId() + "_" + playerHerb.getHerbId();
                        if (playerHerbMap.containsKey(key)) {
                            PlayerHerb old = playerHerbMap.get(key);
                            old.setHerbCnt(playerHerb.getHerbCnt() + old.getHerbCnt());
                            mapper.updateById(old);
                        } else {
                            mapper.insert(playerHerb);
                        }
                    });
                })
        );
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
