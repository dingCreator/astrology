package com.dingCreator.astrology.enums.equipment;

import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.dto.equipment.EquipmentBarDTO;
import com.dingCreator.astrology.dto.equipment.EquipmentDTO;
import com.dingCreator.astrology.dto.organism.player.PlayerInfoDTO;
import com.dingCreator.astrology.entity.EquipmentBelongTo;
import com.dingCreator.astrology.enums.exception.EquipmentExceptionEnum;
import com.dingCreator.astrology.service.EquipmentBelongToService;
import com.dingCreator.astrology.util.EquipmentUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author ding
 * @date 2024/3/24
 */
@Getter
@AllArgsConstructor
public enum EquipmentTypeEnum {
    /**
     * 武器
     */
    WEAPON("weapon", "武器",
            (playerId, equipmentBelongTo) -> {
                EquipmentUtil.validate(playerId, equipmentBelongTo);
                PlayerInfoDTO playerInfoDTO = PlayerCache.getPlayerById(playerId);
                EquipmentBarDTO bar = playerInfoDTO.getEquipmentBarDTO();
                if (Objects.nonNull(bar.getWeapon())) {
                    EquipmentBelongToService.getInstance().updateEquipment(bar.getWeapon().getId(), false);
                }
                EquipmentBelongToService.getInstance().updateEquipment(equipmentBelongTo.getId(), true);
                bar.setWeapon(new EquipmentDTO(equipmentBelongTo.getId(), equipmentBelongTo.getBelongToId(),
                        equipmentBelongTo.getEquipmentLevel()));
            },
            playerId -> {
                PlayerInfoDTO playerInfoDTO = PlayerCache.getPlayerById(playerId);
                EquipmentBarDTO bar = playerInfoDTO.getEquipmentBarDTO();
                if (Objects.isNull(bar.getWeapon())) {
                    throw EquipmentExceptionEnum.NOT_EQUIP_WEAPON.getException();
                }
                EquipmentBelongToService.getInstance().updateEquipment(bar.getWeapon().getId(), false);
                bar.setWeapon(null);
            }),
    /**
     * 防具
     */
    ARMOR("armor", "防具",
            (playerId, equipmentBelongTo) -> {
                EquipmentUtil.validate(playerId, equipmentBelongTo);
                PlayerInfoDTO playerInfoDTO = PlayerCache.getPlayerById(playerId);
                EquipmentBarDTO bar = playerInfoDTO.getEquipmentBarDTO();
                if (Objects.nonNull(bar.getArmor())) {
                    EquipmentBelongToService.getInstance().updateEquipment(bar.getArmor().getId(), false);
                }
                bar.setArmor(new EquipmentDTO(equipmentBelongTo.getId(), equipmentBelongTo.getBelongToId(),
                        equipmentBelongTo.getEquipmentLevel()));
                EquipmentBelongToService.getInstance().updateEquipment(equipmentBelongTo.getId(), true);
            },
            playerId -> {
                PlayerInfoDTO playerInfoDTO = PlayerCache.getPlayerById(playerId);
                EquipmentBarDTO bar = playerInfoDTO.getEquipmentBarDTO();
                if (Objects.isNull(bar.getArmor())) {
                    throw EquipmentExceptionEnum.NOT_EQUIP_ARMOR.getException();
                }
                EquipmentBelongToService.getInstance().updateEquipment(bar.getArmor().getId(), false);
                bar.setArmor(null);
            }),
    /**
     * 饰品
     */
    JEWELRY("jewelry", "饰品",
            (playerId, equipmentBelongTo) -> {
                EquipmentUtil.validate(playerId, equipmentBelongTo);
                PlayerInfoDTO playerInfoDTO = PlayerCache.getPlayerById(playerId);
                EquipmentBarDTO bar = playerInfoDTO.getEquipmentBarDTO();
                if (Objects.nonNull(bar.getJewelry())) {
                    EquipmentBelongToService.getInstance().updateEquipment(bar.getJewelry().getId(), false);
                }
                bar.setJewelry(new EquipmentDTO(equipmentBelongTo.getId(), equipmentBelongTo.getBelongToId(),
                        equipmentBelongTo.getEquipmentLevel()));
                EquipmentBelongToService.getInstance().updateEquipment(equipmentBelongTo.getId(), true);
            },
            playerId -> {
                PlayerInfoDTO playerInfoDTO = PlayerCache.getPlayerById(playerId);
                EquipmentBarDTO bar = playerInfoDTO.getEquipmentBarDTO();
                if (Objects.isNull(bar.getJewelry())) {
                    throw EquipmentExceptionEnum.NOT_EQUIP_JEWELRY.getException();
                }
                EquipmentBelongToService.getInstance().updateEquipment(bar.getJewelry().getId(), false);
                bar.setJewelry(null);
            }),
    ;
    private final String type;
    private final String chnDesc;
    private final BiConsumer<Long, EquipmentBelongTo> equip;
    private final Consumer<Long> remove;
}
