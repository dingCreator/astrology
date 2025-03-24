package com.dingCreator.astrology.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.database.DatabaseProvider;
import com.dingCreator.astrology.dto.alchemy.PillDTO;
import com.dingCreator.astrology.entity.Pill;
import com.dingCreator.astrology.entity.PlayerHerb;
import com.dingCreator.astrology.entity.PlayerPill;
import com.dingCreator.astrology.enums.exception.AlchemyExceptionEnum;
import com.dingCreator.astrology.mapper.PlayerPillMapper;
import com.dingCreator.astrology.util.AlchemyUtil;
import com.dingCreator.astrology.util.LockUtil;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.Lock;

/**
 * @author ding
 * @date 2025/3/11
 */
public class PlayerPillService {

    public void addPill(Long playerId, Long pillId, int cnt) {
        LockUtil.execute(Constants.PILL_LOCK_PREFIX + playerId, () -> {
            DatabaseProvider.getInstance().reuseTransactionExecute(sqlSession -> {
                PlayerPillMapper playerPillMapper = sqlSession.getMapper(PlayerPillMapper.class);
                PlayerPill playerPill = playerPillMapper.selectOne(new QueryWrapper<PlayerPill>()
                        .eq(PlayerPill.PLAYER_ID, playerId).eq(PlayerPill.PLAYER_ID, pillId));
                if (Objects.isNull(playerPill)) {
                    playerPill = new PlayerPill();
                    playerPill.setPlayerId(playerId);
                    playerPill.setPillId(pillId);
                    playerPill.setPillCnt(cnt);
                    playerPillMapper.insert(playerPill);
                } else {
                    playerPill.setPillCnt(playerPill.getPillCnt() + cnt);
                    playerPillMapper.updateById(playerPill);
                }
            });
        });
    }


    public String usePill(Long playerId, Pill pill, int cnt) {
        return LockUtil.execute(Constants.PILL_LOCK_PREFIX + playerId,
                () -> DatabaseProvider.getInstance().reuseTransactionExecuteReturn(sqlSession -> {
                    PlayerPillMapper mapper = sqlSession.getMapper(PlayerPillMapper.class);
                    PlayerPill playerPill = mapper.selectOne(
                            new QueryWrapper<PlayerPill>().eq(PlayerPill.PLAYER_ID, playerId).eq(PlayerPill.PILL_ID, pill.getId())
                    );
                    if (cnt > playerPill.getPillCnt()) {
                        throw AlchemyExceptionEnum.NOT_ENOUGH_PILL.getException(cnt, playerPill.getPillCnt());
                    }
                    playerPill.setPillCnt(playerPill.getPillCnt() - cnt);
                    mapper.updateById(playerPill);
                    return AlchemyUtil.doPillEffect(playerId, pill);
                })
        );
    }

    public List<PlayerPill> listPill(Long playerId, int pageIndex, int pageSize) {
        int index = pageIndex - 1;
        return DatabaseProvider.getInstance().executeReturn(sqlSession -> {
            PlayerPillMapper mapper = sqlSession.getMapper(PlayerPillMapper.class);
            return mapper.selectList(
                    new QueryWrapper<PlayerPill>()
                            .eq(PlayerPill.PLAYER_ID, playerId)
                            .gt(PlayerPill.PILL_CNT, 0)
                            .orderByDesc(PlayerPill.PILL_CNT)
                            .last("limit " + index * pageSize + "," + pageSize)
            );
        });
    }

    public Integer selectPlayerPillCount(Long playerId) {
        return DatabaseProvider.getInstance().executeReturn(
                sqlSession -> sqlSession.getMapper(PlayerPillMapper.class)
                        .selectCount(
                                new QueryWrapper<PlayerPill>()
                                        .eq(PlayerPill.PLAYER_ID, playerId)
                                        .gt(PlayerPill.PILL_CNT, 0)
                        )
        );
    }

    private PlayerPillService() {

    }

    public static PlayerPillService getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static final PlayerPillService INSTANCE = new PlayerPillService();
    }
}
