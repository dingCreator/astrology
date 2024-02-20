package com.dingCreator.astrology.behavior;

import com.dingCreator.astrology.behavior.response.BattleBehaviorResponse;
import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.dto.OrganismDTO;
import com.dingCreator.astrology.dto.PlayerDTO;
import com.dingCreator.astrology.entity.RankUpBoss;
import com.dingCreator.astrology.util.BattleUtil;

/**
 * @author ding
 * @date 2024/1/28
 */
public class RankBehavior {

    private PlayerCache playerCache;

    /**
     * 突破
     * @param id 突破者 ID
     */
    public void rankUp(Long id) {
        PlayerDTO playerDTO = playerCache.getPlayerById(id);
        OrganismDTO player = new OrganismDTO();
        player.setOrganism(playerDTO.getPlayer());

        RankUpBoss rankUpBoss = new RankUpBoss();
        OrganismDTO boss = new OrganismDTO();
        boss.setOrganism(rankUpBoss);

        BattleBehaviorResponse response = BattleUtil.battle(player, boss);
    }
}