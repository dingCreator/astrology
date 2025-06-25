package com.dingCreator.astrology.dto.organism.player;

import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.dto.organism.OrganismDTO;
import com.dingCreator.astrology.entity.Player;
import com.dingCreator.astrology.entity.base.Organism;
import com.dingCreator.astrology.enums.PlayerStatusEnum;
import com.dingCreator.astrology.enums.equipment.EquipmentPropertiesTypeEnum;
import com.dingCreator.astrology.enums.exception.PlayerExceptionEnum;
import com.dingCreator.astrology.util.EquipmentUtil;
import com.dingCreator.astrology.util.MapUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;

/**
 * @author ding
 * @date 2024/8/2
 */
@Data
@NoArgsConstructor
public class PlayerDTO extends OrganismDTO {
    /**
     * 玩家ID
     */
    private Long id;
    /**
     * 经验值
     */
    private Long exp;
    /**
     * 职业
     */
    private String job;
    /**
     * 状态
     */
    private volatile String status;
    /**
     * 进入此状态时间
     */
    private LocalDateTime statusStartTime;
    /**
     * 是否可用
     */
    private Boolean enabled;

    @Override
    protected synchronized void initPropWithAddition() {
        this.maxHpWithAddition = EquipmentUtil.getLongVal(this.maxHp, EquipmentPropertiesTypeEnum.HP,
                PlayerCache.getPlayerById(this.id).getEquipmentBarDTO());
        this.hpWithAddition = (long) Math.round((float) this.maxHpWithAddition * (float) this.hp / (float) this.maxHp);
        this.maxMpWithAddition = EquipmentUtil.getLongVal(this.maxMp, EquipmentPropertiesTypeEnum.MP,
                PlayerCache.getPlayerById(this.id).getEquipmentBarDTO());
        this.mpWithAddition = (long) Math.round((float)this.maxMpWithAddition * (float) this.mp / (float) this.maxMp);
    }

    public void copyProperties(Player player) {
        this.id = player.getId();
        this.name = player.getName();
        this.hp = player.getHp();
        this.maxHp = player.getMaxHp();
        this.mp = player.getMp();
        this.maxMp = player.getMaxMp();
        this.atk = player.getAtk();
        this.magicAtk = player.getMagicAtk();
        this.def = player.getDef();
        this.magicDef = player.getMagicDef();
        this.penetrate = player.getPenetrate();
        this.magicPenetrate = player.getMagicPenetrate();
        this.criticalRate = player.getCriticalRate();
        this.criticalReductionRate = player.getCriticalReductionRate();
        this.criticalDamage = player.getCriticalDamage();
        this.criticalDamageReduction = player.getCriticalDamageReduction();
        this.behaviorSpeed = player.getBehaviorSpeed();
        this.hit = player.getHit();
        this.dodge = player.getDodge();
        this.lifeStealing = player.getLifeStealing();
        this.rank = player.getRank();
        this.level = player.getLevel();
        this.exp = player.getExp();
        this.job = player.getJob();
        this.status = player.getStatus();
        this.statusStartTime = player.getStatusStartTime();
        this.enabled = player.getEnabled();
    }

    public void copyProperties2Player(Player player) {
        player.setId(this.id);
        player.setName(this.name);
        player.setHp(this.hp);
        player.setMaxHp(this.maxHp);
        player.setMp(this.mp);
        player.setMaxMp(this.maxMp);
        player.setAtk(this.atk);
        player.setMagicAtk(this.magicAtk);
        player.setDef(this.def);
        player.setMagicDef(this.magicDef);
        player.setPenetrate(this.penetrate);
        player.setMagicPenetrate(this.magicPenetrate);
        player.setCriticalRate(this.criticalRate);
        player.setCriticalReductionRate(this.criticalReductionRate);
        player.setCriticalDamage(this.criticalDamage);
        player.setCriticalDamageReduction(this.criticalDamageReduction);
        player.setBehaviorSpeed(this.behaviorSpeed);
        player.setHit(this.hit);
        player.setDodge(this.dodge);
        player.setLifeStealing(this.lifeStealing);
        player.setRank(this.rank);
        player.setLevel(this.level);
        player.setExp(this.exp);
        player.setJob(this.job);
        player.setStatus(this.status);
        player.setStatusStartTime(this.statusStartTime);
        player.setEnabled(this.enabled);
    }
}