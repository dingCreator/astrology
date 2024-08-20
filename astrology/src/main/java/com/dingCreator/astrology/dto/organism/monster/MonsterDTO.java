package com.dingCreator.astrology.dto.organism.monster;

import com.dingCreator.astrology.dto.organism.OrganismDTO;
import com.dingCreator.astrology.entity.base.Monster;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ding
 * @date 2024/8/17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonsterDTO extends OrganismDTO {
    /**
     * ID
     */
    protected Long id;
    /**
     * 怪物描述
     */
    protected String description;

    public void copyProperties(Monster monster) {
        this.id = monster.getId();
        this.name = monster.getName();
        this.hp = monster.getHp();
        this.maxHp = monster.getMaxHp();
        this.mp = monster.getMp();
        this.maxMp = monster.getMaxMp();
        this.atk = monster.getAtk();
        this.magicAtk = monster.getMagicAtk();
        this.def = monster.getDef();
        this.magicDef = monster.getMagicDef();
        this.penetrate = monster.getPenetrate();
        this.magicPenetrate = monster.getMagicPenetrate();
        this.criticalRate = monster.getCriticalRate();
        this.criticalReductionRate = monster.getCriticalReductionRate();
        this.criticalDamage = monster.getCriticalDamage();
        this.criticalDamageReduction = monster.getCriticalDamageReduction();
        this.behaviorSpeed = monster.getBehaviorSpeed();
        this.hit = monster.getHit();
        this.dodge = monster.getDodge();
        this.lifeStealing = monster.getLifeStealing();
        this.rank = monster.getRank();
        this.level = monster.getLevel();
        this.description = monster.getDescription();
    }
}
