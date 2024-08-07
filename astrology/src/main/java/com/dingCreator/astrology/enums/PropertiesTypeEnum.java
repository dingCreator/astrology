package com.dingCreator.astrology.enums;

import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.dto.equipment.EquipmentPropertiesDTO;
import com.dingCreator.astrology.entity.Player;
import com.dingCreator.astrology.util.EquipmentUtil;
import lombok.Getter;

import java.util.function.BiConsumer;

/**
 * @author ding
 * @date 2024/3/24
 */
@Getter
public enum PropertiesTypeEnum {
    /**
     * 血量
     */
    HP("hp", "血量", (playerId, prop) -> {
        Player player = PlayerCache.getPlayerById(playerId).getPlayerDTO();
        player.setMaxHp(EquipmentUtil.getVal(player.getMaxHp(), prop.getVal(), prop.getRate()));
        player.setHp(EquipmentUtil.getVal(player.getMaxHp(), prop.getVal(), prop.getRate()));
    }),
    /**
     * 蓝量
     */
    MP("mp", "蓝量", (playerId, prop) -> {
        Player player = PlayerCache.getPlayerById(playerId).getPlayerDTO();
        player.setMaxMp(EquipmentUtil.getVal(player.getMaxMp(), prop.getVal(), prop.getRate()));
        player.setMp(EquipmentUtil.getVal(player.getMp(), prop.getVal(), prop.getRate()));
    }),
    /**
     * 物攻
     */
    ATK("atk", "攻击", (playerId, prop) -> {
        Player player = PlayerCache.getPlayerById(playerId).getPlayerDTO();
        player.setAtk(EquipmentUtil.getVal(player.getAtk(), prop.getVal(), prop.getRate()));
    }),
    /**
     * 魔攻
     */
    MAGIC_ATK("magicAtk", "法强", (playerId, prop) -> {
        Player player = PlayerCache.getPlayerById(playerId).getPlayerDTO();
        player.setAtk(EquipmentUtil.getVal(player.getAtk(), prop.getVal(), prop.getRate()));
    }),
    /**
     * 防御力
     */
    DEF("def", "防御", (playerId, prop) -> {
        Player player = PlayerCache.getPlayerById(playerId).getPlayerDTO();
        player.setAtk(EquipmentUtil.getVal(player.getAtk(), prop.getVal(), prop.getRate()));
    }),
    /**
     * 魔抗
     */
    MAGIC_DEF("magicDef", "法抗", (playerId, prop) -> {
        Player player = PlayerCache.getPlayerById(playerId).getPlayerDTO();
        player.setAtk(EquipmentUtil.getVal(player.getAtk(), prop.getVal(), prop.getRate()));
    }),
    /**
     * 穿透
     */
    PENETRATE("penetrate", "穿甲", (playerId, prop) -> {
        Player player = PlayerCache.getPlayerById(playerId).getPlayerDTO();
        player.setAtk(EquipmentUtil.getVal(player.getAtk(), prop.getVal(), prop.getRate()));
    }),
    /**
     * 法穿
     */
    MAGIC_PENETRATE("magic_penetrate", "法穿", (playerId, prop) -> {
        Player player = PlayerCache.getPlayerById(playerId).getPlayerDTO();
        player.setAtk(EquipmentUtil.getVal(player.getAtk(), prop.getVal(), prop.getRate()));
    }),
    /**
     * 暴击率
     */
    CRITICAL_RATE("criticalRate", "暴击", (playerId, prop) -> {
        Player player = PlayerCache.getPlayerById(playerId).getPlayerDTO();
        player.setAtk(EquipmentUtil.getVal(player.getAtk(), prop.getVal(), prop.getRate()));
    }),
    /**
     * 抗暴率
     */
    CRITICAL_REDUCTION_RATE("criticalReductionRate", "抗暴", (playerId, prop) -> {
        Player player = PlayerCache.getPlayerById(playerId).getPlayerDTO();
        player.setAtk(EquipmentUtil.getVal(player.getAtk(), prop.getVal(), prop.getRate()));
    }),
    /**
     * 爆伤倍率
     */
    CRITICAL_DAMAGE("criticalDamage", "爆伤", (playerId, prop) -> {
        Player player = PlayerCache.getPlayerById(playerId).getPlayerDTO();
        player.setAtk(EquipmentUtil.getVal(player.getAtk(), prop.getVal(), prop.getRate()));
    }),
    /**
     * 爆伤减免
     */
    CRITICAL_DAMAGE_REDUCTION("criticalDamageReduction", "爆免", (playerId, prop) -> {
        Player player = PlayerCache.getPlayerById(playerId).getPlayerDTO();
        player.setAtk(EquipmentUtil.getVal(player.getAtk(), prop.getVal(), prop.getRate()));
    }),
    /**
     * 行动速度
     */
    BEHAVIOR_SPEED("behaviorSpeed", "速度", (playerId, prop) -> {
        Player player = PlayerCache.getPlayerById(playerId).getPlayerDTO();
        player.setAtk(EquipmentUtil.getVal(player.getAtk(), prop.getVal(), prop.getRate()));
    }),
    /**
     * 命中
     */
    HIT("hit", "命中", (playerId, prop) -> {
        Player player = PlayerCache.getPlayerById(playerId).getPlayerDTO();
        player.setAtk(EquipmentUtil.getVal(player.getAtk(), prop.getVal(), prop.getRate()));
    }),
    /**
     * 闪避
     */
    DODGE("dodge", "闪避", (playerId, prop) -> {
        Player player = PlayerCache.getPlayerById(playerId).getPlayerDTO();
        player.setAtk(EquipmentUtil.getVal(player.getAtk(), prop.getVal(), prop.getRate()));
    }),
    ;

    PropertiesTypeEnum(String nameEn, String nameCh, BiConsumer<Long, EquipmentPropertiesDTO.Prop> biConsumer) {
        this.nameEn = nameEn;
        this.nameCh = nameCh;
        this.equip = biConsumer;
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
//    /**
//     * 卸下装备
//     */
//    private final Consumer<Long> unequip;
}
