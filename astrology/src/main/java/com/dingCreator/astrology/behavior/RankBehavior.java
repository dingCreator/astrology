package com.dingCreator.astrology.behavior;

import cn.hutool.core.collection.CollectionUtil;
import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.dto.organism.player.PlayerDTO;
import com.dingCreator.astrology.entity.RankUpBoss;
import com.dingCreator.astrology.enums.RankEnum;
import com.dingCreator.astrology.enums.exception.RankExceptionEnum;
import com.dingCreator.astrology.service.RankUpBossService;
import com.dingCreator.astrology.util.BattleUtil;
import com.dingCreator.astrology.vo.BattleResultVO;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ding
 * @date 2024/1/28
 */
public class RankBehavior {

    private final RankUpBossService rankUpBossService = RankUpBossService.getInstance();

    /**
     * 突破
     *
     * @param id 突破者 ID
     */
    public BattleResultVO rankUp(Long id) {
        PlayerDTO playerDTO = PlayerCache.getPlayerById(id).getPlayerDTO();
        if (Constants.MAX_RANK <= playerDTO.getRank()) {
            throw RankExceptionEnum.ALREADY_MAX_RANK.getException();
        }
        if (playerDTO.getLevel() < RankEnum.getEnum(playerDTO.getJob(), playerDTO.getRank()).getMaxLevel()) {
            throw RankExceptionEnum.LEVEL_NOT_ENOUGH.getException();
        }
        List<RankUpBoss> rankUpBossList = rankUpBossService.getRankUpBoss(playerDTO.getJob(), playerDTO.getRank());
        if (CollectionUtil.isEmpty(rankUpBossList)) {
            throw RankExceptionEnum.RANK_BOSS_NOT_FOUND.getException();
        }

        BattleResultVO response = BattleUtil.battlePVE(id, rankUpBossList.stream().map(RankUpBoss::getMonsterId)
                .collect(Collectors.toList()), false);
        if (BattleResultVO.BattleResult.WIN.equals(response.getBattleResult())
//                || BattleResultVO.BattleResult.DRAW.equals(response.getBattleResult())
        ) {
            // 获取突破前阶级，触发突破成功奖励
            int oldRank = playerDTO.getRank();
            RankEnum oldRankEnum = RankEnum.getEnum(playerDTO.getJob(), oldRank);
            oldRankEnum.getRankUpAward().accept(id);
            // 阶级+1
            playerDTO.setRank(oldRank + 1);
            // 突破成功赠送1经验触发升级
            ExpBehavior.getInstance().getExp(playerDTO.getId(), 1L);
            // 属性提升
            playerDTO.setMaxHp(BigDecimal.valueOf(playerDTO.getMaxHp()).multiply(BigDecimal.valueOf(1.5F)).longValue());
            playerDTO.setMaxMp(playerDTO.getMaxMp() + 50 * oldRank);
            playerDTO.setHp(playerDTO.getMaxHp());
            playerDTO.setMp(playerDTO.getMaxMp());
            playerDTO.setAtk(BigDecimal.valueOf(playerDTO.getAtk()).multiply(BigDecimal.valueOf(1.5F)).longValue());
            playerDTO.setMagicAtk(BigDecimal.valueOf(playerDTO.getMagicAtk()).multiply(BigDecimal.valueOf(1.5F)).longValue());
            playerDTO.setDef(BigDecimal.valueOf(playerDTO.getDef()).multiply(BigDecimal.valueOf(1.5F)).longValue());
            playerDTO.setMagicDef(BigDecimal.valueOf(playerDTO.getMagicDef()).multiply(BigDecimal.valueOf(1.5F)).longValue());
            playerDTO.setBehaviorSpeed(BigDecimal.valueOf(playerDTO.getBehaviorSpeed()).multiply(BigDecimal.valueOf(1.5F)).longValue());
            playerDTO.setHit(BigDecimal.valueOf(playerDTO.getHit()).multiply(BigDecimal.valueOf(1.5F)).longValue());
            playerDTO.setDodge(BigDecimal.valueOf(playerDTO.getDodge()).multiply(BigDecimal.valueOf(1.5F)).longValue());
            playerDTO.clearAdditionVal();
        } else {
            // 突破失败扣除一半经验
            playerDTO.setExp((long) Math.round(playerDTO.getExp() / 2F));
        }
        PlayerCache.save(playerDTO.getId());
        return response;
    }

    public static RankBehavior getInstance() {
        return RankBehavior.Holder.BEHAVIOR;
    }

    private static class Holder {
        private static final RankBehavior BEHAVIOR = new RankBehavior();
    }

    private RankBehavior() {

    }
}