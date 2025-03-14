package com.dingCreator.astrology.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.database.DatabaseProvider;
import com.dingCreator.astrology.dto.alchemy.HerbQuantityDTO;
import com.dingCreator.astrology.dto.alchemy.PillDTO;
import com.dingCreator.astrology.entity.Pill;
import com.dingCreator.astrology.entity.PlayerHerb;
import com.dingCreator.astrology.enums.HerbEnum;
import com.dingCreator.astrology.enums.exception.AlchemyExceptionEnum;
import com.dingCreator.astrology.mapper.PillMapper;
import com.dingCreator.astrology.mapper.PlayerHerbMapper;
import com.dingCreator.astrology.util.AlchemyUtil;
import com.dingCreator.astrology.util.LockUtil;
import com.dingCreator.astrology.vo.AlchemyResultVO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author ding
 * @date 2025/3/11
 */
public class PillService {

    /**
     * 炼丹
     *
     * @param playerId 玩家ID
     * @param herbMap  投入的药材
     * @return 炼丹结果
     */
    public AlchemyResultVO alchemy(long playerId, Map<String, Integer> herbMap) {
        return LockUtil.execute(Constants.ALCHEMY_LOCK_PREFIX + playerId,
                () -> DatabaseProvider.getInstance().reuseTransactionExecuteReturn(sqlSession -> {
                    // 初始化mapper
                    PlayerHerbMapper playerHerbMapper = sqlSession.getMapper(PlayerHerbMapper.class);
                    PillMapper pillMapper = sqlSession.getMapper(PillMapper.class);
                    // 1. 扣除药材
                    List<Long> herbIds = new ArrayList<>(herbMap.size());
                    QueryWrapper<PlayerHerb> herbWrapper = new QueryWrapper<PlayerHerb>().eq(PlayerHerb.PLAYER_ID, playerId);
                    herbMap.keySet().forEach(herbName -> {
                        HerbEnum herbEnum = HerbEnum.getByName(herbName);
                        if (Objects.isNull(herbEnum)) {
                            throw AlchemyExceptionEnum.INVALID_HERB_NAME.getException().fillArgs(herbName);
                        }
                        herbIds.add(herbEnum.getId());
                    });
                    herbWrapper.in(PlayerHerb.HERB_ID, herbIds);
                    List<PlayerHerb> playerHerbList = playerHerbMapper.selectList(herbWrapper);
                    // 检查是否有足够的药材
                    if (playerHerbList.size() != herbMap.size()) {
                        throw AlchemyExceptionEnum.NOT_ENOUGH_HERB.getException();
                    }
                    playerHerbList.forEach(playerHerb -> {
                        String herbName = HerbEnum.getById(playerHerb.getHerbId()).getName();
                        int used = herbMap.get(herbName);
                        if (used <= 0) {
                           throw AlchemyExceptionEnum.INVALID_HERB_INPUT.getException().fillArgs(used);
                        }
                        if (used > playerHerb.getHerbCnt()) {
                            throw AlchemyExceptionEnum.NOT_ENOUGH_HERB.getException().fillArgs(herbName, used, playerHerb.getHerbCnt());
                        }
                        playerHerb.setHerbCnt(playerHerb.getHerbCnt() - used);
                        playerHerbMapper.updateById(playerHerb);
                    });
                    // 2.计算数值
                    HerbQuantityDTO quantity = herbMap.entrySet().stream()
                            .map(entry -> {
                                HerbEnum herb = HerbEnum.getByName(entry.getKey());
                                return HerbQuantityDTO.builder()
                                        .vigor(herb.getVigor() * entry.getValue())
                                        .warn(herb.getWarn() * entry.getValue())
                                        .cold(herb.getCold() * entry.getValue())
                                        .toxicity(herb.getToxicity() * entry.getValue())
                                        .quality(herb.getQuality() * entry.getValue())
                                        .star(herb.getStar() * entry.getValue()).build();
                            }).reduce((quantity1, quantity2) -> {
                                quantity2.setVigor(quantity1.getVigor() + quantity2.getVigor());
                                quantity2.setWarn(quantity1.getWarn() + quantity2.getWarn());
                                quantity2.setCold(quantity1.getCold() + quantity2.getCold());
                                quantity2.setToxicity(quantity1.getToxicity() + quantity2.getToxicity());
                                quantity2.setQuality(quantity1.getQuality() + quantity2.getQuality());
                                quantity2.setStar(quantity1.getStar() + quantity2.getStar());
                                return quantity2;
                            }).orElseThrow(() -> new IllegalArgumentException("计算数值失败"));
                    // 3. 获取丹药
                    Pill pill = pillMapper.getPill(quantity.getVigor(), quantity.getWarn(), quantity.getCold(),
                            quantity.getToxicity(), quantity.getQuality(), quantity.getStar());
                    if (Objects.isNull(pill)) {
                        return AlchemyResultVO.builder().success(false).alchemyMsg("投入的药材配比有误，险些炸炉，得到了一炉灰").build();
                    }
                    PlayerPillService.getInstance().addPill(playerId, pill.getId(), 1);
                    return AlchemyResultVO.builder().success(true).alchemyMsg("恭喜炼制出" + pill.getPillName()).build();
                })
        );
    }

    /**
     * 新建丹药
     *
     * @param pillDTO 丹药
     */
    public void createPill(PillDTO pillDTO) {
        DatabaseProvider.getInstance().reuseTransactionExecute(sqlSession -> {
            PillMapper mapper = sqlSession.getMapper(PillMapper.class);
            Pill pill = AlchemyUtil.convertPillDTO(pillDTO);
            mapper.insert(pill);
        });
    }

    public Pill getByName(String pillName) {
        return DatabaseProvider.getInstance().executeReturn(sqlSession ->
                sqlSession.getMapper(PillMapper.class).selectOne(new QueryWrapper<Pill>().eq(Pill.PILL_NAME, pillName)));
    }

    public List<Pill> listByIds(List<Long> pillIds) {
        return DatabaseProvider.getInstance().executeReturn(sqlSession ->
                sqlSession.getMapper(PillMapper.class).selectBatchIds(pillIds));
    }

    private PillService() {

    }

    public static PillService getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static final PillService INSTANCE = new PillService();
    }
}
