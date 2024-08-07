package com.dingCreator.astrology.enums;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dingCreator.astrology.dto.LootItemDTO;
import com.dingCreator.astrology.entity.EquipmentBelongTo;
import com.dingCreator.astrology.enums.equipment.EquipmentEnum;
import com.dingCreator.astrology.enums.skill.SkillEnum;
import com.dingCreator.astrology.service.EquipmentBelongToService;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author ding
 * @date 2024/4/10
 */
@Getter
@AllArgsConstructor
public enum LootItemTypeEnum {
    /**
     * 装备
     */
    Equipment("Equipment",
            (jsonObject, belongToId) -> {
                LootItemDTO.EquipmentItem item = jsonObject.toJavaObject(LootItemDTO.EquipmentItem.class);
                EquipmentBelongTo equipmentBelongTo = new EquipmentBelongTo();
                equipmentBelongTo.setBelongTo(BelongToEnum.Player.getBelongTo());
                equipmentBelongTo.setBelongToId(belongToId);
                equipmentBelongTo.setEquipmentId(item.getEquipmentId());
                equipmentBelongTo.setLevel(1);
                equipmentBelongTo.setEquip(false);
                EquipmentBelongToService.addBelongTo(equipmentBelongTo);
            },
            (jsonObject -> {
                LootItemDTO.EquipmentItem item = jsonObject.toJavaObject(LootItemDTO.EquipmentItem.class);
                return EquipmentEnum.getById(item.getEquipmentId()).getName();
            })
    ),
    ;

    /**
     * 类型名称
     */
    private final String typeName;
    /**
     * 发放方式
     */
    private final BiConsumer<JSONObject, Long> distributionMethod;
    /**
     * 掉落物反显
     */
    private final Function<JSONObject, String> viewLoot;

    public static LootItemTypeEnum getByName(String typeName) {
        return Arrays.stream(LootItemTypeEnum.values()).filter(e -> e.getTypeName().equals(typeName))
                .findFirst().orElse(null);
    }
}
