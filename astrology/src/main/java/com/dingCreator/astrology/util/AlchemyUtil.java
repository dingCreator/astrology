package com.dingCreator.astrology.util;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.dto.alchemy.PillDTO;
import com.dingCreator.astrology.dto.alchemy.PlayerHerbListDTO;
import com.dingCreator.astrology.dto.organism.player.PlayerDTO;
import com.dingCreator.astrology.entity.Pill;
import com.dingCreator.astrology.entity.PlayerHerb;
import com.dingCreator.astrology.enums.alchemy.HerbEnum;
import com.dingCreator.astrology.enums.OrganismPropertiesEnum;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author ding
 * @date 2025/3/12
 */
public class AlchemyUtil {

    /**
     * 丹药效果
     */
    @FunctionalInterface
    public interface PillEffect {
        String doEffect(Long playerId, PillDTO.PillEffectDTO effect);
    }

    private static final List<PillEffect> PILL_EFFECTS = Arrays.asList(
            new AtkPillEffect(),
            new DefPillEffect()
    );

    private static class AtkPillEffect implements PillEffect {
        @Override
        public String doEffect(Long playerId, PillDTO.PillEffectDTO effect) {
            if (Objects.isNull(effect.getAtk()) || effect.getAtk() == 0) {
                return null;
            }
            PlayerDTO playerDTO = PlayerCache.getPlayerById(playerId).getPlayerDTO();
            playerDTO.setAtk(playerDTO.getAtk() + effect.getAtk());
            PlayerCache.save(playerId);
            return OrganismPropertiesEnum.ATK.getChnDesc() + "+" + effect.getAtk();
        }
    }
    private static class DefPillEffect implements PillEffect {
        @Override
        public String doEffect(Long playerId, PillDTO.PillEffectDTO effect) {
            if (Objects.isNull(effect.getDef()) || effect.getDef() == 0) {
                return null;
            }
            PlayerDTO playerDTO = PlayerCache.getPlayerById(playerId).getPlayerDTO();
            playerDTO.setDef(playerDTO.getDef() + effect.getDef());
            PlayerCache.save(playerId);
            return OrganismPropertiesEnum.DEF.getChnDesc() + "防御+" + effect.getAtk();
        }
    }


    /**
     * 转化丹药
     *
     * @param pill 丹药
     * @return 转化结果
     */
    public static PillDTO convertPill(Pill pill) {
        PillDTO pillDTO = PillDTO.builder()
                .pillName(pill.getPillName()).pillRank(pill.getPillRank()).qualityRank(pill.getQualityRank())
                .vigor(pill.getVigor()).warn(pill.getWarn()).cold(pill.getCold()).toxicity(pill.getToxicity())
                .qualityStart(pill.getQualityStart()).qualityEnd(pill.getQualityEnd())
                .starStart(pill.getStarStart()).starEnd(pill.getStarEnd()).build();
        pillDTO.setPillEffectDTO(JSONObject.parseObject(pill.getEffectJson(), PillDTO.PillEffectDTO.class));
        return pillDTO;
    }

    /**
     * 转化丹药
     *
     * @param pillDTO 丹药
     * @return 转化结果
     */
    public static Pill convertPillDTO(PillDTO pillDTO) {
        Pill pill = Pill.builder()
                .pillName(pillDTO.getPillName()).pillRank(pillDTO.getPillRank()).qualityRank(pillDTO.getQualityRank())
                .vigor(pillDTO.getVigor()).warn(pillDTO.getWarn()).cold(pillDTO.getCold()).toxicity(pillDTO.getToxicity())
                .qualityStart(pillDTO.getQualityStart()).qualityEnd(pillDTO.getQualityEnd())
                .starStart(pillDTO.getStarStart()).starEnd(pillDTO.getStarEnd()).build();
        pill.setEffectJson(JSONObject.toJSONString(pillDTO.getPillEffectDTO()));
        return pill;
    }

    public static PlayerHerbListDTO convertPlayerHerb(PlayerHerb playerHerb) {
        return PlayerHerbListDTO.builder().id(playerHerb.getId()).herbId(playerHerb.getHerbId())
                .herbName(HerbEnum.getById(playerHerb.getHerbId()).getName())
                .herbCnt(playerHerb.getHerbCnt()).build();
    }

    public static String doPillEffect(Long playerId, Pill pill) {
        return doPillEffect(playerId, convertPill(pill));
    }

    public static String doPillEffect(Long playerId, PillDTO pillDTO) {
        PillDTO.PillEffectDTO effect = pillDTO.getPillEffectDTO();
        return PILL_EFFECTS.stream().map(pillEffect -> pillEffect.doEffect(playerId, effect))
                .filter(StringUtils::isNotBlank)
                .reduce((s1, s2) -> s1 + "," + s2).orElse("无任何效果");
    }
}
