package com.dingCreator.astrology.enums;

import com.alibaba.fastjson.JSONObject;
import com.dingCreator.astrology.behavior.ExpBehavior;
import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.dto.article.*;
import com.dingCreator.astrology.dto.organism.player.PlayerAssetDTO;
import com.dingCreator.astrology.entity.EquipmentBelongTo;
import com.dingCreator.astrology.entity.PlayerAsset;
import com.dingCreator.astrology.entity.PlayerHerb;
import com.dingCreator.astrology.entity.SkillBag;
import com.dingCreator.astrology.service.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author ding
 * @date 2024/10/28
 */
@Getter
@AllArgsConstructor
public enum ArticleTypeEnum {
    /**
     * 装备
     */
    EQUIPMENT("equipment",
            str -> JSONObject.parseObject(str, ArticleEquipmentItem.class),
            (playerId, equipmentList) -> {
                List<EquipmentBelongTo> equipmentBelongToList = equipmentList.stream()
                        .collect(Collectors.toMap(
                                item -> ((ArticleEquipmentItem) item).getEquipmentId(),
                                item -> (ArticleEquipmentItem) item,
                                (item1, item2) -> {
                                    item2.setCnt(item1.getCnt() + item2.getCnt());
                                    return item2;
                                }
                        )).values().stream()
                        .map(item -> EquipmentBelongTo.builder().equipmentId(item.getEquipmentId())
                                .belongTo(BelongToEnum.PLAYER.getBelongTo()).belongToId(playerId)
                                .totalCnt(item.getCnt().intValue()).equipmentLevel(1).equip(false).build())
                        .collect(Collectors.toList());
                EquipmentBelongToService.getInstance().batchAddBelongTo(equipmentBelongToList);
            }
    ),
    /**
     * 称号
     */
    TITLE("title", str -> JSONObject.parseObject(str, ArticleTitleItem.class),
            (playerId, titleList) -> {
                // TODO 称号
            }
    ),
    /**
     * 技能
     */
    SKILL("skill", str -> JSONObject.parseObject(str, ArticleSkillItem.class),
            (playerId, skillList) -> {
                List<SkillBag> skillBagList = skillList.stream()
                        .collect(Collectors.toMap(
                                item -> ((ArticleSkillItem) item).getSkillId(),
                                item -> (ArticleSkillItem) item,
                                (item1, item2) -> {
                                    item2.setCnt(item1.getCnt() + item2.getCnt());
                                    return item2;
                                }
                        )).values().stream()
                        .map(item -> SkillBag.builder().skillId(item.getSkillId())
                                .playerId(playerId).skillCnt(item.getCnt().intValue()).build())
                        .collect(Collectors.toList());
                SkillBagService.getInstance().batchSendSkill(skillBagList);
            }
    ),
    /**
     * 药材
     */
    HERB("herb", str -> JSONObject.parseObject(str, ArticleHerbItem.class),
            (playerId, herbList) -> {
                List<PlayerHerb> skillBagList = herbList.stream()
                        .collect(Collectors.toMap(
                                item -> ((ArticleHerbItem) item).getHerbId(),
                                item -> (ArticleHerbItem) item,
                                (item1, item2) -> {
                                    item2.setCnt(item1.getCnt() + item2.getCnt());
                                    return item2;
                                }
                        )).values().stream()
                        .map(item -> PlayerHerb.builder().herbId(item.getHerbId())
                                .playerId(playerId).herbCnt(item.getCnt().intValue()).build())
                        .collect(Collectors.toList());
                PlayerHerbService.getInstance().batchSendHerb(skillBagList);
            }
    ),
    /**
     * 资产
     */
    ASSET("asset", str -> JSONObject.parseObject(str, ArticleAssetItem.class),
            (playerId, assetList) -> {
                List<PlayerAssetDTO> assetDTOList = assetList.stream()
                        .collect(Collectors.toMap(
                                item -> ((ArticleAssetItem) item).getAssetType(),
                                item -> (ArticleAssetItem) item,
                                (item1, item2) -> {
                                    item2.setCnt(item1.getCnt() + item2.getCnt());
                                    return item2;
                                }
                        )).values().stream()
                        .map(item -> PlayerAssetDTO.builder().assetType(item.getAssetType())
                                .playerId(playerId).assetCnt(item.getCnt()).build())
                        .collect(Collectors.toList());
                PlayerService.getInstance().changeAsset(PlayerCache.getPlayerById(playerId), assetDTOList);
            }
    ),
    /**
     * 经验
     */
    EXP("exp", str -> JSONObject.parseObject(str, ArticleExpItem.class),
            (playerId, expList) -> {
                long exp = expList.stream().map(e -> (ArticleExpItem) e).map(e -> {
                    long tmp = 0;
                    if (Objects.nonNull(e.getRate())) {
                        int level = PlayerCache.getPlayerById(playerId).getPlayerDTO().getLevel();
                        tmp += Math.round(ExpBehavior.getInstance().getCurrentLevelMaxExp(level) * e.getRate());
                    }
                    if (Objects.nonNull(e.getVal())) {
                        tmp += e.getVal();
                    }
                    return tmp;
                }).reduce(0L, Long::sum);
                ExpBehavior.getInstance().getExp(playerId, exp);
            }
    ),
    ;

    private final String type;

    private final Function<String, ? extends ArticleItemDTO> convertJsonFunc;

    private final BiConsumer<Long, List<ArticleItemDTO>> batchSendFunc;

    public static ArticleTypeEnum getByType(String type) {
        return Arrays.stream(ArticleTypeEnum.values()).filter(e -> e.getType().equals(type)).findFirst().orElse(null);
    }
}
