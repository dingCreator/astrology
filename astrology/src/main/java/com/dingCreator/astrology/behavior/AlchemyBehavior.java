package com.dingCreator.astrology.behavior;

import cn.hutool.core.collection.CollectionUtil;
import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.dto.alchemy.PillDTO;
import com.dingCreator.astrology.dto.alchemy.PlayerHerbListDTO;
import com.dingCreator.astrology.dto.alchemy.PlayerPillListDTO;
import com.dingCreator.astrology.entity.Pill;
import com.dingCreator.astrology.entity.PlayerHerb;
import com.dingCreator.astrology.entity.PlayerPill;
import com.dingCreator.astrology.enums.HerbEnum;
import com.dingCreator.astrology.enums.exception.AlchemyExceptionEnum;
import com.dingCreator.astrology.response.PageResponse;
import com.dingCreator.astrology.service.PillService;
import com.dingCreator.astrology.service.PlayerHerbService;
import com.dingCreator.astrology.service.PlayerPillService;
import com.dingCreator.astrology.util.AlchemyUtil;
import com.dingCreator.astrology.util.PageUtil;
import com.dingCreator.astrology.vo.AlchemyResultVO;

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
            throw AlchemyExceptionEnum.INVALID_HERB_NAME.getException().fillArgs(herbName);
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
        List<PlayerHerb> playerHerbList = service.listPlayerHerb(playerId, pageIndex, pageSize);
        List<PlayerHerbListDTO> dtoList = playerHerbList.stream().map(AlchemyUtil::convertPlayerHerb).collect(Collectors.toList());
        int count = service.countPlayerHerb(playerId);
        return PageUtil.addPageDesc(dtoList, pageIndex, pageSize, count);
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
        List<PlayerPill> playerPillList = service.listPill(playerId, pageIndex, pageSize);
        List<Long> pillIds = playerPillList.stream().map(PlayerPill::getPillId).collect(Collectors.toList());
        Map<Long, Pill> pillMap = PillService.getInstance().listByIds(pillIds).stream().collect(Collectors.toMap(Pill::getId, Function.identity()));
        List<PlayerPillListDTO> list = playerPillList.stream()
                .map(p -> PlayerPillListDTO.builder().pillName(pillMap.get(p.getPillId()).getPillName()).pillCnt(p.getPillCnt()).build())
                .collect(Collectors.toList());
        return PageUtil.addPageDesc(list, pageIndex, pageSize, service.selectPlayerPillCount(playerId));
    }

    public String usePill(Long playerId, String pillName, int cnt) {
        if (cnt <= 0) {
            throw AlchemyExceptionEnum.INVALID_PILL_CNT.getException().fillArgs(cnt);
        }
        Pill pill = PillService.getInstance().getByName(pillName);
        if (Objects.isNull(pill)) {
            throw AlchemyExceptionEnum.PILL_NOT_FOUND.getException().fillArgs(pillName);
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
