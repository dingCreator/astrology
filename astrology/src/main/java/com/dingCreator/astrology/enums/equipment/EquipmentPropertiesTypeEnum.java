package com.dingCreator.astrology.enums.equipment;

import com.dingCreator.astrology.enums.OrganismPropertiesEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author ding
 * @date 2024/3/24
 */
@Getter
@AllArgsConstructor
public enum EquipmentPropertiesTypeEnum {
    /**
     * 血量
     */
    HP(OrganismPropertiesEnum.HP.getFieldName(), OrganismPropertiesEnum.HP.getChnDesc()),
    /**
     * 蓝量
     */
    MP(OrganismPropertiesEnum.MP.getFieldName(), OrganismPropertiesEnum.MP.getChnDesc()),
    /**
     * 物攻
     */
    ATK(OrganismPropertiesEnum.ATK.getFieldName(), OrganismPropertiesEnum.ATK.getChnDesc()),
    /**
     * 魔攻
     */
    MAGIC_ATK(OrganismPropertiesEnum.MAGIC_ATK.getFieldName(), OrganismPropertiesEnum.MAGIC_ATK.getChnDesc()),
    /**
     * 防御力
     */
    DEF(OrganismPropertiesEnum.DEF.getFieldName(), OrganismPropertiesEnum.DEF.getChnDesc()),
    /**
     * 魔抗
     */
    MAGIC_DEF(OrganismPropertiesEnum.MAGIC_DEF.getFieldName(), OrganismPropertiesEnum.MAGIC_DEF.getChnDesc()),
    /**
     * 穿透
     */
    PENETRATE(OrganismPropertiesEnum.PENETRATE.getFieldName(), OrganismPropertiesEnum.PENETRATE.getChnDesc()),
    /**
     * 法穿
     */
    MAGIC_PENETRATE(OrganismPropertiesEnum.MAGIC_PENETRATE.getFieldName(), OrganismPropertiesEnum.MAGIC_PENETRATE.getChnDesc()),
    /**
     * 暴击率
     */
    CRITICAL_RATE(OrganismPropertiesEnum.CRITICAL_RATE.getFieldName(), OrganismPropertiesEnum.CRITICAL_RATE.getChnDesc()),
    /**
     * 抗暴率
     */
    CRITICAL_REDUCTION_RATE(OrganismPropertiesEnum.CRITICAL_REDUCTION_RATE.getFieldName(),
            OrganismPropertiesEnum.CRITICAL_REDUCTION_RATE.getChnDesc()),
    /**
     * 爆伤倍率
     */
    CRITICAL_DAMAGE(OrganismPropertiesEnum.CRITICAL_DAMAGE.getFieldName(),
            OrganismPropertiesEnum.CRITICAL_DAMAGE.getChnDesc()),
    /**
     * 爆伤减免
     */
    CRITICAL_DAMAGE_REDUCTION(OrganismPropertiesEnum.CRITICAL_DAMAGE_REDUCTION.getFieldName(),
            OrganismPropertiesEnum.CRITICAL_DAMAGE_REDUCTION.getChnDesc()),
    /**
     * 行动速度
     */
    BEHAVIOR_SPEED(OrganismPropertiesEnum.BEHAVIOR_SPEED.getFieldName(), OrganismPropertiesEnum.BEHAVIOR_SPEED.getChnDesc()),
    /**
     * 命中
     */
    HIT(OrganismPropertiesEnum.HIT.getFieldName(), OrganismPropertiesEnum.HIT.getChnDesc()),
    /**
     * 闪避
     */
    DODGE(OrganismPropertiesEnum.DODGE.getFieldName(), OrganismPropertiesEnum.DODGE.getChnDesc()),
    /**
     * 吸血
     */
    LIFE_STEALING(OrganismPropertiesEnum.LIFE_STEAL.getFieldName(), OrganismPropertiesEnum.LIFE_STEAL.getChnDesc()),
    ;

    /**
     * 英文名称
     */
    private final String nameEn;
    /**
     * 中文名称
     */
    private final String nameCh;

    public static EquipmentPropertiesTypeEnum getByNameEn(String nameEn) {
        return Arrays.stream(values()).filter(e -> e.getNameEn().equals(nameEn)).findFirst().orElse(null);
    }
}
