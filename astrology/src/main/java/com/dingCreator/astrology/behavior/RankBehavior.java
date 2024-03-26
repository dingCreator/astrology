package com.dingCreator.astrology.behavior;

import com.dingCreator.astrology.vo.BattleResultVO;
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
    public void rankUp(Long id) {
        PlayerDTO playerDTO = PlayerCache.getPlayerById(id);
        OrganismDTO player = new OrganismDTO();
        player.setOrganism(playerDTO.getPlayer());

        RankUpBoss rankUpBoss = new RankUpBoss();
        OrganismDTO boss = new OrganismDTO();

//        BattleResultVO response = BattleUtil.battle(player, boss);
    }
}