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

import java.util.Collections;
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
                || BattleResultVO.BattleResult.DRAW.equals(response.getBattleResult())) {
            playerDTO.setRank(playerDTO.getRank() + 1);
            // 突破成功赠送1经验触发升级
            ExpBehavior.getInstance().getExp(playerDTO.getId(), 1L);
            // 属性提升
            playerDTO.setHp((long) Math.round(playerDTO.getMaxHp() * 1.5F));
            playerDTO.setMaxHp((long) Math.round(playerDTO.getMaxHp() * 1.5F));
            // 蓝不受突破影响
            playerDTO.setAtk((long) Math.round(playerDTO.getAtk() * 1.5F));
            playerDTO.setMagicAtk((long) Math.round(playerDTO.getMagicAtk() * 1.5F));
            playerDTO.setDef((long) Math.round(playerDTO.getDef() * 1.5F));
            playerDTO.setMagicDef((long) Math.round(playerDTO.getMagicDef() * 1.5F));
            playerDTO.setBehaviorSpeed((long) Math.round(playerDTO.getBehaviorSpeed() * 1.5F));
            playerDTO.setHit((long) Math.round(playerDTO.getHit() * 1.5F));
            playerDTO.setDodge((long) Math.round(playerDTO.getDodge() * 1.5F));
        } else {
            // 突破失败扣除一半经验
            playerDTO.setExp((long) Math.round(playerDTO.getExp() / 2F));
        }
        PlayerCache.flush(Collections.singletonList(playerDTO.getId()));
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