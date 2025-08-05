package com.dingCreator.astrology.enums.skill;

import com.dingCreator.astrology.dto.organism.OrganismDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author ding
 * @date 2025/7/26
 */
@Getter
@AllArgsConstructor
public enum SummonTemplateEnum {
    UNDEAD("亡灵",
            50000L, 0F, 50000L, 0F,
            0L, 0F, 0L, 0F,
            8500L, 0F, 0L, 0F,
            5200L, 0F, 5200L, 0F,
            0F, 0F, 0F, 0F,
            0.1F, 0F, 0F, 0F,
            1.5F, 0F, 0F, 0F,
            6500L, 0F, 6500L, 0F,
            3000L, 0F, 0F, 0F
    ),

    ;
    /**
     * 名称
     */
    private final String name;
    /**
     * 血量
     * 血量百分比
     */
    private final Long hp;
    private final Float hpRate;
    /**
     * 血量上限
     * 血量上限百分比
     */
    private final Long maxHp;
    private final Float maxHpRate;
    /**
     * 蓝
     * 蓝量百分比
     */
    private final Long mp;
    private final Float mpRate;
    /**
     * 蓝上限
     * 蓝量上限百分比
     */
    private final Long maxMp;
    private final Float maxMpRate;
    /**
     * 攻击力
     * 攻击力百分比
     */
    private final Long atk;
    private final Float atkRate;
    /**
     * 法强
     * 法强百分比
     */
    private final Long magicAtk;
    private final Float magicAtkRate;
    /**
     * 防御
     * 防御百分比
     */
    private final Long def;
    private final Float defRate;
    /**
     * 法抗
     * 法抗百分比
     */
    private final Long magicDef;
    private final Float magicDefRate;
    /**
     * 穿透
     * 穿透百分比
     */
    private final Float penetrate;
    private final Float penetrateRate;
    /**
     * 法穿
     * 法穿百分比
     */
    private final Float magicPenetrate;
    private final Float magicPenetrateRate;
    /**
     * 暴击率
     * 暴击率百分比
     */
    private final Float criticalRate;
    private final Float criticalRateRate;
    /**
     * 抗暴率
     * 抗暴率百分比
     */
    private final Float criticalReductionRate;
    private final Float criticalReductionRateRate;
    /**
     * 爆伤倍率
     * 爆伤倍率百分比
     */
    private final Float criticalDamage;
    private final Float criticalDamageRate;
    /**
     * 爆伤减免
     * 爆伤减免百分比
     */
    private final Float criticalDamageReduction;
    private final Float criticalDamageReductionRate;
    /**
     * 命中率
     * 命中率百分比
     */
    private final Long hit;
    private final Float hitRate;
    /**
     * 闪避率
     * 闪避率百分比
     */
    private final Long dodge;
    private final Float dodgeRate;
    /**
     * 行动速度
     * 行动速度百分比
     */
    private final Long behaviorSpeed;
    private final Float behaviorSpeedRate;
    /**
     * 吸血
     * 吸血百分比
     */
    private final Float lifeStealing;
    private final Float lifeStealingRate;

    public OrganismDTO constructOrganism(OrganismDTO summoner) {
        OrganismDTO organismDTO = new OrganismDTO();
        organismDTO.setMaxHp(maxHp + Math.round(summoner.getMaxHpWithAddition() * maxHpRate));
        organismDTO.setMaxMp(maxMp + Math.round(summoner.getMaxMpWithAddition() * maxMpRate));
        organismDTO.setHp(hp + Math.round(summoner.getHpWithAddition() * hpRate));
        organismDTO.setMp(mp + Math.round(summoner.getMpWithAddition() * mpRate));
        organismDTO.setAtk(atk + Math.round(summoner.getAtk() * atkRate));
        organismDTO.setMagicAtk(magicAtk + Math.round(summoner.getMagicAtk() * magicAtkRate));
        organismDTO.setDef(def + Math.round(summoner.getDef() * defRate));
        organismDTO.setMagicDef(magicDef + Math.round(summoner.getMagicDef() * magicDefRate));
        organismDTO.setPenetrate(penetrate + Math.round(summoner.getPenetrate() * penetrateRate));
        organismDTO.setMagicPenetrate(magicPenetrate + Math.round(summoner.getMagicPenetrate() * magicPenetrateRate));
        organismDTO.setCriticalRate(criticalRate + Math.round(summoner.getCriticalRate() * criticalRateRate));
        organismDTO.setCriticalReductionRate(criticalReductionRate + Math.round(summoner.getCriticalReductionRate() * criticalReductionRateRate));
        organismDTO.setCriticalDamage(criticalDamage + Math.round(summoner.getCriticalDamage() * criticalDamageRate));
        organismDTO.setCriticalDamageReduction(criticalDamageReduction + Math.round(summoner.getCriticalDamageReduction() * criticalDamageReductionRate));
        organismDTO.setHit(hit + Math.round(summoner.getHit() * hitRate));
        organismDTO.setDodge(dodge + Math.round(summoner.getDodge() * dodgeRate));
        organismDTO.setBehaviorSpeed(behaviorSpeed + Math.round(summoner.getBehaviorSpeed() * behaviorSpeedRate));
        organismDTO.setLifeStealing(lifeStealing + Math.round(summoner.getLifeStealing() * lifeStealingRate));
        return organismDTO;
    }
}
