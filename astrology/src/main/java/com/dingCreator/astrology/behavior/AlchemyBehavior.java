package com.dingCreator.astrology.behavior;

import cn.hutool.core.collection.CollectionUtil;
import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.dto.alchemy.PillDTO;
import com.dingCreator.astrology.dto.alchemy.PlayerHerbDTO;
import com.dingCreator.astrology.dto.alchemy.PlayerHerbListDTO;
import com.dingCreator.astrology.dto.alchemy.PlayerPillListDTO;
import com.dingCreator.astrology.entity.Pill;
import com.dingCreator.astrology.entity.PlayerHerb;
import com.dingCreator.astrology.entity.PlayerPill;
import com.dingCreator.astrology.enums.alchemy.HerbEnum;
import com.dingCreator.astrology.enums.exception.AlchemyExceptionEnum;
import com.dingCreator.astrology.response.PageResponse;
import com.dingCreator.astrology.service.PillService;
import com.dingCreator.astrology.service.PlayerHerbService;
import com.dingCreator.astrology.service.PlayerPillService;
import com.dingCreator.astrology.util.AlchemyUtil;
import com.dingCreator.astrology.util.PageUtil;
import com.dingCreator.astrology.vo.AlchemyResultVO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author ding
 * @date 2025/3/11
 */
public class AlchemyBehavior {

    /**
     * 炼丹
     *
     * @param playerId 玩家ID
     * @param herbMap  投入的药材
     * @return 炼丹结果
     */
    public AlchemyResultVO alchemy(long playerId, Map<String, Integer> herbMap) {
        if (CollectionUtil.isEmpty(herbMap)) {
            throw AlchemyExceptionEnum.INVALID_HERB_INPUT.getException();
        }
        return PillService.getInstance().alchemy(playerId, herbMap);
    }

    /**
     * 增加药材
     *
     * @param playerId 玩家ID
     * @param herbName 药材名称
     * @param cnt      数量
     */
    public void addHerb(long playerId, String herbName, int cnt) {
        PlayerCache.getPlayerById(playerId);
        HerbEnum herbEnum = HerbEnum.getByName(herbName);
        if (Objects.isNull(herbEnum)) {
            throw AlchemyExceptionEnum.INVALID_HERB_NAME.getException(herbName);
        }
        PlayerHerbService.getInstance().addHerb(playerId, herbEnum.getId(), cnt);
    }

    /**
     * 新建丹药
     *
     * @param pillDTO 丹药
     */
    public void createPill(PillDTO pillDTO) {
        PillService.getInstance().createPill(pillDTO);
    }

    /**
     * 药材背包
     *
     * @param playerId  玩家ID
     * @param pageIndex 页码
     * @param pageSize  页面大小
     * @return 药材
     */
    public PageResponse<PlayerHerbListDTO> pageHerb(Long playerId, int pageIndex, int pageSize) {
        if (pageIndex < 1) {
            pageIndex = 1;
        }
        PlayerHerbService service = PlayerHerbService.getInstance();
        int count = service.countPlayerHerb(playerId);
        List<PlayerHerb> playerHerbList = service.listPlayerHerb(playerId, pageIndex, pageSize);
        if (CollectionUtil.isEmpty(playerHerbList)) {
            return PageUtil.addPageDesc(new ArrayList<>(), pageIndex, pageSize, count);
        }
        List<PlayerHerbListDTO> dtoList = playerHerbList.stream().map(AlchemyUtil::convertPlayerHerb).collect(Collectors.toList());
        return PageUtil.addPageDesc(dtoList, pageIndex, pageSize, count);
    }

    /**
     * 药材详情
     *
     * @param playerId 玩家ID
     * @param herbName 药材名称
     * @return 药材详情
     */
    public PlayerHerbDTO herbDetail(Long playerId, String herbName) {
        HerbEnum herbEnum = HerbEnum.getByName(herbName);
        if (Objects.isNull(herbEnum)) {
            throw AlchemyExceptionEnum.INVALID_HERB_NAME.getException(herbName);
        }
        PlayerHerbService service = PlayerHerbService.getInstance();
        PlayerHerb playerHerb = service.getHerbById(playerId, herbEnum.getId());
        if (Objects.isNull(playerHerb)) {
            throw AlchemyExceptionEnum.HERB_NOT_HAVE.getException();
        }
        return PlayerHerbDTO.builder()
                .herbName(herbEnum.getName())
                .herbRank(herbEnum.getRank())
                .herbDesc("缘神你觉不觉得这里需要一段药材说明")
                .vigor(herbEnum.getVigor())
                .warn(herbEnum.getWarn())
                .cold(herbEnum.getCold())
                .toxicity(herbEnum.getToxicity())
                .quality(herbEnum.getQuality())
                .star(herbEnum.getStar())
                .herbCnt(playerHerb.getHerbCnt()).build();
    }

    /**
     * 丹药背包
     *
     * @param playerId  玩家ID
     * @param pageIndex 页码
     * @param pageSize  页面大小
     * @return 丹药
     */
    public PageResponse<PlayerPillListDTO> pagePill(Long playerId, int pageIndex, int pageSize) {
        if (pageIndex < 1) {
            pageIndex = 1;
        }
        PlayerPillService service = PlayerPillService.getInstance();
        int size = service.selectPlayerPillCount(playerId);
        List<PlayerPill> playerPillList = service.listPill(playerId, pageIndex, pageSize);
        if (CollectionUtil.isEmpty(playerPillList)) {
            return PageUtil.addPageDesc(new ArrayList<>(), pageIndex, pageSize, size);
        }
        List<Long> pillIds = playerPillList.stream().map(PlayerPill::getPillId).collect(Collectors.toList());
        Map<Long, Pill> pillMap = PillService.getInstance().listByIds(pillIds).stream().collect(Collectors.toMap(Pill::getId, Function.identity()));
        List<PlayerPillListDTO> list = playerPillList.stream()
                .map(p -> PlayerPillListDTO.builder().pillName(pillMap.get(p.getPillId()).getPillName()).pillCnt(p.getPillCnt()).build())
                .collect(Collectors.toList());
        return PageUtil.addPageDesc(list, pageIndex, pageSize, size);
    }

    /**
     * 使用丹药
     *
     * @param playerId    玩家ID
     * @param qualityRank 丹药品质等级
     * @param pillName    丹药名称
     * @param cnt         数量
     * @return 使用结果
     */
    public String usePill(Long playerId, int qualityRank, String pillName, int cnt) {
        if (cnt <= 0) {
            throw AlchemyExceptionEnum.INVALID_PILL_CNT.getException(cnt);
        }
        Pill pill = PillService.getInstance().getByName(pillName);
        if (Objects.isNull(pill)) {
            throw AlchemyExceptionEnum.PILL_NOT_FOUND.getException(pillName);
        }
        return PlayerPillService.getInstance().usePill(playerId, pill, cnt);
    }

    private AlchemyBehavior() {

    }

    public static AlchemyBehavior getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static final AlchemyBehavior INSTANCE = new AlchemyBehavior();
    }
}
