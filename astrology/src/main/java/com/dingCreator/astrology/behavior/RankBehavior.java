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
import com.dingCreator.astrology.vo.RankUpResultVO;

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
    public RankUpResultVO rankUp(Long id) {
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
        RankUpResultVO resultVO = new RankUpResultVO();
        if (BattleResultVO.BattleResult.WIN.equals(response.getBattleResult())
//                || BattleResultVO.BattleResult.DRAW.equals(response.getBattleResult())
        ) {
            // 获取突破前阶级，触发突破成功奖励
            int oldRank = playerDTO.getRank();
            StringBuilder builder = new StringBuilder();
            RankEnum oldRankEnum = RankEnum.getEnum(playerDTO.getJob(), oldRank);
            oldRankEnum.getRankUpAward().accept(id, builder);
            builder.append("属性提升：\n");
            // 阶级+1
            playerDTO.setRank(oldRank + 1);
            // 突破成功赠送1经验触发升级
            ExpBehavior.getInstance().getExp(playerDTO.getId(), 1L);
            // 属性提升
            builder.append("最大血量：").append(playerDTO.getMaxHp());
            playerDTO.setMaxHp(BigDecimal.valueOf(playerDTO.getMaxHp()).multiply(BigDecimal.valueOf(1.5F)).longValue());
            builder.append(" -> ").append(playerDTO.getMaxHp()).append("\n");

            builder.append("最大蓝量：").append(playerDTO.getMaxMp());
            playerDTO.setMaxMp(playerDTO.getMaxMp() + 50L * oldRank);
            builder.append(" -> ").append(playerDTO.getMaxMp()).append("\n");

            playerDTO.setHp(playerDTO.getMaxHp());
            playerDTO.setMp(playerDTO.getMaxMp());

            builder.append("攻击：").append(playerDTO.getAtk());
            playerDTO.setAtk(BigDecimal.valueOf(playerDTO.getAtk()).multiply(BigDecimal.valueOf(1.5F)).longValue());
            builder.append(" -> ").append(playerDTO.getAtk()).append("\n");

            builder.append("法强：").append(playerDTO.getMagicAtk());
            playerDTO.setMagicAtk(BigDecimal.valueOf(playerDTO.getMagicAtk()).multiply(BigDecimal.valueOf(1.5F)).longValue());
            builder.append(" -> ").append(playerDTO.getMagicAtk()).append("\n");

            builder.append("防御：").append(playerDTO.getDef());
            playerDTO.setDef(BigDecimal.valueOf(playerDTO.getDef()).multiply(BigDecimal.valueOf(1.5F)).longValue());
            builder.append(" -> ").append(playerDTO.getDef()).append("\n");

            builder.append("法抗：").append(playerDTO.getMagicDef());
            playerDTO.setMagicDef(BigDecimal.valueOf(playerDTO.getMagicDef()).multiply(BigDecimal.valueOf(1.5F)).longValue());
            builder.append(" -> ").append(playerDTO.getMagicDef()).append("\n");

            builder.append("速度：").append(playerDTO.getBehaviorSpeed());
            playerDTO.setBehaviorSpeed(BigDecimal.valueOf(playerDTO.getBehaviorSpeed()).multiply(BigDecimal.valueOf(1.5F)).longValue());
            builder.append(" -> ").append(playerDTO.getBehaviorSpeed()).append("\n");

            builder.append("命中：").append(playerDTO.getHit());
            playerDTO.setHit(BigDecimal.valueOf(playerDTO.getHit()).multiply(BigDecimal.valueOf(1.5F)).longValue());
            builder.append(" -> ").append(playerDTO.getHit()).append("\n");

            builder.append("闪避：").append(playerDTO.getDodge());
            playerDTO.setDodge(BigDecimal.valueOf(playerDTO.getDodge()).multiply(BigDecimal.valueOf(1.5F)).longValue());
            builder.append(" -> ").append(playerDTO.getDodge()).append("\n");

            playerDTO.clearAdditionVal();
            resultVO.setSuccess(true);
            resultVO.setMessage(builder.toString());
        } else {
            long oldExp = playerDTO.getExp();
            // 突破失败扣除一半经验
            long newExp = Math.round(oldExp / 2F);
            playerDTO.setExp(newExp);
            resultVO.setSuccess(false);
            resultVO.setMessage("经验值：" + oldExp + " -> " + newExp);
        }
        PlayerCache.save(playerDTO.getId());
        return resultVO;
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