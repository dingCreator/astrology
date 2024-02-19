package com.dingCreator.yuanshen.util;

import cn.hutool.core.collection.CollectionUtil;
import com.dingCreator.yuanshen.behavior.response.BattleBehaviorResponse;
import com.dingCreator.yuanshen.dto.*;
import com.dingCreator.yuanshen.enums.BuffTypeEnum;
import com.dingCreator.yuanshen.enums.SkillEnum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author ding
 * @date 2024/2/3
 */
public class BattleUtil {

    /**
     * 战斗
     *
     * @param initiator 发起方
     * @param recipient 接收方
     * @return 战斗结果
     */
    public static BattleBehaviorResponse battle(OrganismDTO initiator, OrganismDTO recipient) {
        return battle(Collections.singletonList(initiator), Collections.singletonList(recipient));
    }

    /**
     * 战斗
     *
     * @param initiator 发起方
     * @param recipient 接收方
     * @return 战斗结果
     */
    public static synchronized BattleBehaviorResponse battle(List<OrganismDTO> initiator, List<OrganismDTO> recipient) {
        List<BattleDTO> initiatorBattleTmp = new ArrayList<>(initiator.size());
        List<BattleDTO> recipientBattleTmp = new ArrayList<>(recipient.size());

        // 包装
        long totalBehaviorSpeed = 0;
        for (OrganismDTO o : initiator) {
            totalBehaviorSpeed += o.getOrganism().getBehaviorSpeed();
            initiatorBattleTmp.add(buildBattleDTO(o));
        }
        for (OrganismDTO o : recipient) {
            totalBehaviorSpeed += o.getOrganism().getBehaviorSpeed();
            recipientBattleTmp.add(buildBattleDTO(o));
        }

        final long totalBehavior = totalBehaviorSpeed;
        final int maxRound = (initiator.size() + recipient.size()) * 100;
        final AtomicInteger round = new AtomicInteger(0);

        while (round.get() < maxRound
                && initiatorBattleTmp.stream().mapToLong(o -> o.getOrganismDTO().getOrganism().getHp()).sum() > 0
                && recipientBattleTmp.stream().mapToLong(o -> o.getOrganismDTO().getOrganism().getHp()).sum() > 0) {
            battleProcess(initiatorBattleTmp, recipientBattleTmp, totalBehavior, round);
            battleProcess(recipientBattleTmp, initiatorBattleTmp, totalBehavior, round);
            // todo 移除0血角色，减少计算量
        }
        // 判断胜负
        long initiatorAliveNum = initiatorBattleTmp.stream().filter(o -> o.getOrganismDTO().getOrganism().getHp() > 0).count();
        long recipientAliveNum = recipientBattleTmp.stream().filter(o -> o.getOrganismDTO().getOrganism().getHp() > 0).count();

        BattleBehaviorResponse response = new BattleBehaviorResponse();
        if (initiatorAliveNum > recipientAliveNum) {
            response.setBattleResult(BattleBehaviorResponse.BattleResult.WIN);
        } else if (initiatorAliveNum == recipientAliveNum) {
            response.setBattleResult(BattleBehaviorResponse.BattleResult.DRAW);
        } else {
            response.setBattleResult(BattleBehaviorResponse.BattleResult.LOSE);
        }
        return response;
    }

    private static BattleDTO buildBattleDTO(OrganismDTO organismDTO) {
        BattleDTO battleDTO = new BattleDTO();
        battleDTO.setOrganismDTO(organismDTO);
        return battleDTO;
    }

    /**
     * 战斗过程
     *
     * @param from          出手方
     * @param to            敌方
     * @param totalBehavior 行动值上限
     * @param round         轮次
     */
    private static void battleProcess(List<BattleDTO> from, List<BattleDTO> to, final long totalBehavior, final AtomicInteger round) {
        from.forEach(o -> {
            long behavior = o.getBehavior() + o.getOrganismDTO().getOrganism().getBehaviorSpeed();
            if (behavior > totalBehavior && o.getOrganismDTO().getOrganism().getHp() > 0) {
                attackBehavior(o, to);
                behavior -= totalBehavior;
                round.addAndGet(1);
                // buff轮次-1 并清除所有过期buff
                o.getBuffMap().values().stream()
                        .peek(buffList -> buffList.forEach(buff -> buff.setRound(buff.getRound() - 1)))
                        .forEach(buffList -> buffList.removeIf(buffDTO -> buffDTO.getRound() <= 0));
            }
            o.setBehavior(behavior);
        });
    }

    private static void attackBehavior(BattleDTO from, List<BattleDTO> to) {
        // 选择攻击目标
        BattleDTO target;
        // 先过滤掉0血角色
        List<BattleDTO> alive = to.stream().filter(t -> t.getOrganismDTO().getOrganism().getHp() > 0)
                .collect(Collectors.toList());
        List<BattleDTO> taunt = alive.stream().filter(t -> {
            List<BuffDTO> buffList = t.getBuffMap().getOrDefault(BuffTypeEnum.TAUNT.getName(), null);
            return CollectionUtil.isNotEmpty(buffList);
        }).collect(Collectors.toList());

        if (taunt.size() > 0) {
            target = taunt.stream().max(Comparator.comparing(t -> t.getOrganismDTO().getOrganism().getHp())).get();
        } else {
            target = alive.get(0);
        }

        // 获取技能
        SkillBarDTO bar = from.getOrganismDTO().getSkillBarDTO();
        // 技能轮转
        if (bar.getNext() != null) {
            from.getOrganismDTO().setSkillBarDTO(bar.getNext());
        } else {
            from.getOrganismDTO().setSkillBarDTO(bar.getHead());
        }

        // 计算是否命中
        // 命中率 = 1 + (命中 - 闪避) / 闪避
        float fromHit = BuffUtil.getHit(from);
        float targetDodge = BuffUtil.getDodge(target);
        float hitRate = 1 + (fromHit - targetDodge) / targetDodge;
        boolean isHit = RandomUtil.isHit(RandomUtil.format(hitRate, 4));
        if (!isHit) {
            return;
        }

        // 获取技能信息
        SkillEnum skillEnum = SkillEnum.getById(bar.getSkillId());
        // 获取技能效果
        List<SkillEffectDTO> effects = skillEnum.getSkillEffects();
        if (CollectionUtil.isNotEmpty(effects)) {
            effects.forEach(effect -> BuffUtil.addBuff(target, (BuffDTO) effect.getVal()));
        }

        // todo 装备
        // damage = atk - def * (1 - penetrate)
        long damage = Math.round(
                from.getOrganismDTO().getOrganism().getAtk() * skillEnum.getDamageRate()
                        - target.getOrganismDTO().getOrganism().getDef()
                        * (1 - from.getOrganismDTO().getOrganism().getPenetrate())
        );
        long hp = target.getOrganismDTO().getOrganism().getHp() - damage;
        hp = hp > 0 ? hp : 0;
        target.getOrganismDTO().getOrganism().setHp(hp);
    }
}
