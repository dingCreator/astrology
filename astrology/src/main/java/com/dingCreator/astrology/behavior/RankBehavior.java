package com.dingCreator.astrology.behavior;

import cn.hutool.core.collection.CollectionUtil;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.entity.Player;
import com.dingCreator.astrology.entity.base.Monster;
import com.dingCreator.astrology.enums.BelongToEnum;
import com.dingCreator.astrology.enums.RankEnum;
import com.dingCreator.astrology.enums.exception.MonsterExceptionEnum;
import com.dingCreator.astrology.enums.exception.RankExceptionEnum;
import com.dingCreator.astrology.enums.job.JobEnum;
import com.dingCreator.astrology.enums.job.JobInitPropertiesEnum;
import com.dingCreator.astrology.service.MonsterService;
import com.dingCreator.astrology.service.RankUpBossService;
import com.dingCreator.astrology.vo.BattleResultVO;
import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.dto.OrganismDTO;
import com.dingCreator.astrology.dto.PlayerDTO;
import com.dingCreator.astrology.entity.RankUpBoss;
import com.dingCreator.astrology.util.BattleUtil;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author ding
 * @date 2024/1/28
 */
public class RankBehavior {

    private static class Holder {
        private static final RankBehavior BEHAVIOR = new RankBehavior();
    }

    private RankBehavior() {

    }

    public static RankBehavior getInstance() {
        return RankBehavior.Holder.BEHAVIOR;
    }

    /**
     * 突破
     *
     * @param id 突破者 ID
     */
    public BattleResultVO rankUp(Long id) {
        Player player = PlayerCache.getPlayerById(id).getPlayer();
        if (Constants.MAX_RANK <= player.getRank()) {
            throw RankExceptionEnum.RANK_NOT_FOUND.getException();
        }
        if (player.getLevel() < RankEnum.getEnum(player.getJob(), player.getRank()).getMaxLevel()) {
            throw RankExceptionEnum.LEVEL_NOT_ENOUGH.getException();
        }
        List<RankUpBoss> rankUpBossList = RankUpBossService.getRankUpBoss(player.getJob(), player.getRank());
        if (CollectionUtil.isEmpty(rankUpBossList)) {
            throw RankExceptionEnum.RANK_BOSS_NOT_FOUND.getException();
        }

        BattleResultVO response = BattleUtil.battlePVE(id, rankUpBossList.stream().map(RankUpBoss::getMonsterId)
                .collect(Collectors.toList()), false);
        if (BattleResultVO.BattleResult.WIN.equals(response.getBattleResult())
                || BattleResultVO.BattleResult.DRAW.equals(response.getBattleResult())) {
            player.setRank(player.getRank() + 1);
            // 突破成功赠送1经验触发升级
            ExpBehavior.getInstance().getExp(player.getId(), 1L);
            // 属性提升
            player.setHp((long) Math.round(player.getMaxHp() * 1.5F));
            player.setMaxHp((long) Math.round(player.getMaxHp() * 1.5F));
            player.setMp((long) Math.round(player.getMaxMp() * 1.5F));
            player.setMaxMp((long) Math.round(player.getMaxMp() * 1.5F));
            player.setAtk((long) Math.round(player.getAtk() * 1.5F));
            player.setMagicAtk((long) Math.round(player.getMagicAtk() * 1.5F));
            player.setDef((long) Math.round(player.getDef() * 1.5F));
            player.setMagicDef((long) Math.round(player.getMagicDef() * 1.5F));
            player.setBehaviorSpeed((long) Math.round(player.getBehaviorSpeed() * 1.5F));
            player.setHit((long) Math.round(player.getHit() * 1.5F));
            player.setDodge((long) Math.round(player.getDodge() * 1.5F));
        } else {
            player.setExp((long) Math.round(player.getExp() / 2F));
        }
        PlayerCache.flush(Collections.singletonList(player.getId()));
        return response;
    }
}