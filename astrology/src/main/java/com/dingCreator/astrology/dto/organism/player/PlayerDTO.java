package com.dingCreator.astrology.dto.organism.player;

import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.dto.organism.OrganismDTO;
import com.dingCreator.astrology.entity.Player;
import com.dingCreator.astrology.entity.base.Organism;
import com.dingCreator.astrology.enums.PlayerStatusEnum;
import com.dingCreator.astrology.enums.equipment.EquipmentPropertiesTypeEnum;
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
     * 所处位置
     */
    private Long mapId;
    /**
     * 状态
     */
    private String status;
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
        this.hpWithAddition = this.maxHpWithAddition * Math.round((float) this.hp / (float) this.maxHp);
        this.maxMpWithAddition = EquipmentUtil.getLongVal(this.maxMp, EquipmentPropertiesTypeEnum.MP,
                PlayerCache.getPlayerById(this.id).getEquipmentBarDTO());
        this.mpWithAddition = this.maxMpWithAddition * Math.round((float) this.mp / (float) this.maxMp);
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
        this.mapId = player.getMapId();
        this.status = player.getStatus();
        this.statusStartTime = player.getStatusStartTime();
        this.enabled = player.getEnabled();
    }

    public synchronized void setStatus(String status) {
        getStatus();
        this.status = status;
        this.statusStartTime = LocalDateTime.now();
    }

    public synchronized String getStatus() {
        if (PlayerStatusEnum.MOVING.getCode().equals(status)) {
            long targetMapId = MapUtil.getTargetLocation(id);
            if (targetMapId == 0L) {
                status = PlayerStatusEnum.FREE.getCode();
            } else {
                long seconds = MapUtil.moveTime(id, MapUtil.getNowLocation(id), targetMapId);
                LocalDateTime statusEndTime = Objects.isNull(statusStartTime) ?
                        LocalDateTime.MIN : statusStartTime.plusSeconds(seconds);
                if (LocalDateTime.now().isAfter(statusEndTime)) {
                    // 已经到了
                    status = PlayerStatusEnum.FREE.getCode();
                    statusStartTime = LocalDateTime.now();
                    mapId = MapUtil.getTargetLocation(id);
                }
            }
        }
        return status;
    }
}