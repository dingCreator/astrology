package com.dingCreator.astrology.dto.article;

import com.dingCreator.astrology.entity.EquipmentBelongTo;
import com.dingCreator.astrology.enums.ArticleTypeEnum;
import com.dingCreator.astrology.enums.BelongToEnum;
import com.dingCreator.astrology.enums.equipment.EquipmentEnum;
import com.dingCreator.astrology.service.EquipmentBelongToService;
import com.dingCreator.astrology.vo.ArticleItemVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author ding
 * @date 2024/10/28
 */
@Getter
@Setter
@ToString
public class ArticleEquipmentItem extends ArticleItemDTO {
    /**
     * 装备ID
     */
    private Long equipmentId;

    public ArticleEquipmentItem(Long equipmentId) {
        super(ArticleTypeEnum.EQUIPMENT.getType());
        this.equipmentId = equipmentId;
    }

    @Override
    public void send2Player(Long playerId, int cnt) {
        EquipmentBelongTo equipmentBelongTo = new EquipmentBelongTo();
        equipmentBelongTo.setBelongTo(BelongToEnum.PLAYER.getBelongTo());
        equipmentBelongTo.setBelongToId(playerId);
        equipmentBelongTo.setEquipmentId(equipmentId);
        equipmentBelongTo.setEquipmentLevel(1);
        equipmentBelongTo.setEquip(false);
        equipmentBelongTo.setTotalCnt(cnt);
        EquipmentBelongToService.getInstance().addBelongTo(equipmentBelongTo);
    }

    @Override
    public ArticleItemVO fillView(ArticleItemVO vo) {
        EquipmentEnum equipmentEnum = EquipmentEnum.getById(equipmentId);
        vo.setName("【" + equipmentEnum.getEquipmentRankEnum().getRankChnDesc() + "】" + equipmentEnum.getName());
        vo.setDescription(equipmentEnum.getDesc());
        vo.setRare(equipmentEnum.getEquipmentRankEnum().getRare());
        return vo;
    }
}
