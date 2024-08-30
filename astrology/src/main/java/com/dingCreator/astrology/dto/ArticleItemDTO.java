package com.dingCreator.astrology.dto;

import com.dingCreator.astrology.entity.EquipmentBelongTo;
import com.dingCreator.astrology.enums.BelongToEnum;
import com.dingCreator.astrology.enums.equipment.EquipmentEnum;
import com.dingCreator.astrology.service.EquipmentBelongToService;
import com.dingCreator.astrology.vo.ArticleItemVO;
import lombok.Data;

import java.io.Serializable;

/**
 * @author ding
 * @date 2024/4/10
 */
@Data
public abstract class ArticleItemDTO implements Serializable {
    /**
     * 物品类型
     */
    protected String itemType;

    /**
     * 发放方式
     *
     * @param playerId 玩家ID
     */
    public abstract void send2Player(Long playerId);

    /**
     * 反显
     *
     * @return 反显内容
     */
    public abstract ArticleItemVO view();

    @Data
    public static class EquipmentItem extends ArticleItemDTO {
        /**
         * 装备ID
         */
        private Long equipmentId;

        @Override
        public void send2Player(Long playerId) {
            EquipmentBelongTo equipmentBelongTo = new EquipmentBelongTo();
            equipmentBelongTo.setBelongTo(BelongToEnum.PLAYER.getBelongTo());
            equipmentBelongTo.setBelongToId(playerId);
            equipmentBelongTo.setEquipmentId(equipmentId);
            equipmentBelongTo.setLevel(1);
            equipmentBelongTo.setEquip(false);
            EquipmentBelongToService.addBelongTo(equipmentBelongTo);
        }

        @Override
        public ArticleItemVO view() {
            EquipmentEnum equipmentEnum = EquipmentEnum.getById(equipmentId);
            return ArticleItemVO.builder().name(equipmentEnum.getName()).description(equipmentEnum.getDesc()).build();
        }
    }

    @Data
    public static class TitleItem extends ArticleItemDTO {
        /**
         * 称号ID
         */
        private Long titleId;

        @Override
        public void send2Player(Long playerId) {

        }

        @Override
        public ArticleItemVO view() {
            return null;
        }
    }

    @Data
    public static class SkillItem extends ArticleItemDTO {
        /**
         * 技能ID
         */
        private Long skillId;

        @Override
        public void send2Player(Long playerId) {

        }

        @Override
        public ArticleItemVO view() {
            return null;
        }
    }
}
