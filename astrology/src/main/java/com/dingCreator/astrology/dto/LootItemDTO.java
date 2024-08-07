package com.dingCreator.astrology.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author ding
 * @date 2024/4/10
 */
@Data
public class LootItemDTO implements Serializable {
    /**
     * 掉落物类型
     */
    protected String lootItemType;
    /**
     * 爆率
     */
    protected Float rate;

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class EquipmentItem extends LootItemDTO {
        /**
         * 装备ID
         */
        private Long equipmentId;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class TitleItem extends LootItemDTO {
        /**
         * 称号ID
         */
        private Long titleId;
    }
}
