package com.dingCreator.astrology.enums.equipment;

import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.dto.equipment.EquipmentPropertiesDTO;
import com.dingCreator.astrology.dto.organism.player.PlayerDTO;
import com.dingCreator.astrology.util.EquipmentUtil;
import lombok.Getter;

import java.util.function.BiConsumer;

/**
 * @author ding
 * @date 2024/3/24
 */
@Getter
public enum EquipmentPropertiesTypeEnum {
    /**
     * 血量
     */
    HP("hp", "血量"),
    /**
     * 蓝量
     */
    MP("mp", "蓝量"),
    /**
     * 物攻
     */
    ATK("atk", "攻击",
            (playerId, prop) -> {},
            (playerId, prop) -> {}
    ),
    /**
     * 魔攻
     */
    MAGIC_ATK("magicAtk", "法强",
            (playerId, prop) -> {},
            (playerId, prop) -> {}
    ),
    /**
     * 防御力
     */
    DEF("def", "防御",
            (playerId, prop) -> {},
            (playerId, prop) -> {}
    ),
    /**
     * 魔抗
     */
    MAGIC_DEF("magicDef", "法抗",
            (playerId, prop) -> {},
            (playerId, prop) -> {}
    ),
    /**
     * 穿透
     */
    PENETRATE("penetrate", "穿甲",
            (playerId, prop) -> {},
            (playerId, prop) -> {}
    ),
    /**
     * 法穿
     */
    MAGIC_PENETRATE("magic_penetrate", "法穿",
            (playerId, prop) -> {},
            (playerId, prop) -> {}
    ),
    /**
     * 暴击率
     */
    CRITICAL_RATE("criticalRate", "暴击",
            (playerId, prop) -> {},
            (playerId, prop) -> {}
    ),
    /**
     * 抗暴率
     */
    CRITICAL_REDUCTION_RATE("criticalReductionRate", "抗暴",
            (playerId, prop) -> {},
            (playerId, prop) -> {}
    ),
    /**
     * 爆伤倍率
     */
    CRITICAL_DAMAGE("criticalDamage", "爆伤",
            (playerId, prop) -> {},
            (playerId, prop) -> {}
    ),
    /**
     * 爆伤减免
     */
    CRITICAL_DAMAGE_REDUCTION("criticalDamageReduction", "爆免",
            (playerId, prop) -> {},
            (playerId, prop) -> {}
    ),
    /**
     * 行动速度
     */
    BEHAVIOR_SPEED("behaviorSpeed", "速度",
            (playerId, prop) -> {},
            (playerId, prop) -> {}
    ),
    /**
     * 命中
     */
    HIT("hit", "命中",
            (playerId, prop) -> {},
            (playerId, prop) -> {}
    ),
    /**
     * 闪避
     */
    DODGE("dodge", "闪避",
            (playerId, prop) -> {},
            (playerId, prop) -> {}
    ),
    ;

    EquipmentPropertiesTypeEnum(String nameEn, String nameCh) {
        this.nameEn = nameEn;
        this.nameCh = nameCh;
        this.equip = EquipmentPropertiesTypeEnum::dealAddition;
        this.remove = EquipmentPropertiesTypeEnum::dealAddition;
    }

    EquipmentPropertiesTypeEnum(String nameEn, String nameCh, BiConsumer<Long, EquipmentPropertiesDTO.Prop> equip
            , BiConsumer<Long, EquipmentPropertiesDTO.Prop> remove) {
        this.nameEn = nameEn;
        this.nameCh = nameCh;
        this.equip = equip;
        this.remove = remove;
    }

    /**
     * 英文名称
     */
    private final String nameEn;
    /**
     * 中文名称
     */
    private final String nameCh;
    /**
     * 穿上装备
     */
    private final BiConsumer<Long, EquipmentPropertiesDTO.Prop> equip;
    /**
     * 卸下装备
     */
    private final BiConsumer<Long, EquipmentPropertiesDTO.Prop> remove;

    private static void dealAddition(Long playerId, EquipmentPropertiesDTO.Prop prop) {
        PlayerCache.getPlayerById(playerId).getPlayerDTO().clearAdditionVal();
    }
}
