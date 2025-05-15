package com.dingCreator.astrology.enums;

import com.dingCreator.astrology.entity.base.Organism;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * @author ding
 * @date 2024/8/22
 */
@Getter
@AllArgsConstructor
public enum OrganismPropertiesEnum {
    /**
     * 血量
     */
    HP("hp", "血量", (o, val) -> o.setHp(Long.parseLong(val))),
    /**
     * 血量
     */
    MAX_HP("maxHp", "血量上限", (o, val) -> o.setMaxHp(Long.parseLong(val))),
    /**
     * 蓝量
     */
    MP("mp", "蓝量", (o, val) -> o.setMp(Long.parseLong(val))),
    /**
     * 蓝量
     */
    MAX_MP("maxMp", "蓝量上限", (o, val) -> o.setMaxMp(Long.parseLong(val))),
    /**
     * 物攻
     */
    ATK("atk", "攻击", (o, val) -> o.setAtk(Long.parseLong(val))),
    /**
     * 法强
     */
    MAGIC_ATK("magicAtk", "法强", (o, val) -> o.setMagicAtk(Long.parseLong(val))),
    /**
     * 防御力
     */
    DEF("def", "防御", (o, val) -> o.setDef(Long.parseLong(val))),
    /**
     * 法抗
     */
    MAGIC_DEF("magicDef", "法抗", (o, val) -> o.setMagicDef(Long.parseLong(val))),
    /**
     * 穿透
     */
    PENETRATE("penetrate", "穿甲", (o, val) -> o.setPenetrate(Float.parseFloat(val))),
    /**
     * 法穿
     */
    MAGIC_PENETRATE("magicPenetrate", "法穿", (o, val) -> o.setMagicPenetrate(Float.parseFloat(val))),
    /**
     * 暴击率
     */
    CRITICAL_RATE("criticalRate", "暴击", (o, val) -> o.setCriticalRate(Float.parseFloat(val))),
    /**
     * 抗暴率
     */
    CRITICAL_REDUCTION_RATE("criticalReductionRate", "抗暴",
            (o, val) -> o.setCriticalReductionRate(Float.parseFloat(val))),
    /**
     * 爆伤倍率
     */
    CRITICAL_DAMAGE("criticalDamage", "暴伤", (o, val) -> o.setCriticalDamage(Float.parseFloat(val))),
    /**
     * 爆伤减免
     */
    CRITICAL_DAMAGE_REDUCTION("criticalDamageReduction", "暴免",
            (o, val) -> o.setCriticalDamageReduction(Float.parseFloat(val))),
    /**
     * 行动速度
     */
    BEHAVIOR_SPEED("behaviorSpeed", "速度", (o, val) -> o.setBehaviorSpeed(Long.parseLong(val))),
    /**
     * 吸血
     */
    LIFE_STEAL("lifeStealing", "吸血", (o, val) -> o.setLifeStealing(Float.parseFloat(val))),
    /**
     * 命中
     */
    HIT("hit", "命中", (o, val) -> o.setHit(Long.parseLong(val))),
    /**
     * 闪避
     */
    DODGE("dodge", "闪避", (o, val) -> o.setDodge(Long.parseLong(val))),
    /**
     * 等级
     */
    LEVEL("level", "等级", (o, val) -> o.setLevel(Integer.parseInt(val))),
    /**
     * 阶级
     */
    RANK("rank", "阶级", (o, val) -> o.setRank(Integer.parseInt(val))),
    ;

    private final String fieldName;
    private final String chnDesc;
    private final BiConsumer<Organism, String> doSetMethod;

    private static final Map<String, List<OrganismPropertiesEnum>> PROP_MAP;

    static {
        PROP_MAP = Arrays.stream(OrganismPropertiesEnum.values())
                .collect(Collectors.groupingBy(OrganismPropertiesEnum::getChnDesc));
    }

    public static OrganismPropertiesEnum getByChnDesc(String chnDesc) {
        return Arrays.stream(values()).filter(e -> e.getChnDesc().equals(chnDesc)).findFirst().orElse(null);
    }

    public static OrganismPropertiesEnum getByFieldName(String fieldName) {
        return Arrays.stream(values()).filter(e -> e.getFieldName().equals(fieldName)).findFirst().orElse(null);
    }
}
