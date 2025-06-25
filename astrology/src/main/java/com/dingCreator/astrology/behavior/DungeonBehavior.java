package com.dingCreator.astrology.behavior;

import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.dto.DungeonConfigSettingsDTO;
import com.dingCreator.astrology.dto.organism.player.PlayerDTO;
import com.dingCreator.astrology.entity.Dungeon;
import com.dingCreator.astrology.enums.RankEnum;
import com.dingCreator.astrology.enums.exception.DungeonExceptionEnum;
import com.dingCreator.astrology.response.PageResponse;
import com.dingCreator.astrology.service.DungeonService;
import com.dingCreator.astrology.util.LockUtil;
import com.dingCreator.astrology.util.MapUtil;
import com.dingCreator.astrology.util.PageUtil;
import com.dingCreator.astrology.vo.DungeonResultVO;
import com.dingCreator.astrology.vo.DungeonVO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author ding
 * @date 2024/2/20
 */
public class DungeonBehavior {

    private final DungeonService dungeonService = DungeonService.getInstance();

    /**
     * 获取副本列表
     *
     * @param playerId 玩家ID
     * @return 副本信息
     */
    public PageResponse<String> dungeonPage(Long playerId, int pageIndex, int pageSize) {
        PlayerCache.getPlayerById(playerId);
        List<Dungeon> dungeonList = dungeonService.list();
        return PageUtil.buildPage(dungeonList.stream()
                .map(d -> "副本名称：" + d.getName() + " 冷却时间：" + d.getFlushTime() + "s")
                .collect(Collectors.toList()), pageIndex, pageSize);
    }

    /**
     * 开始探索副本
     *
     * @param playerId 玩家ID
     * @return 探索结果
     */
    public DungeonResultVO startExploreDungeon(Long playerId) {
        return startExploreDungeon(playerId, 0);
    }

    /**
     * 开始探索副本
     *
     * @param playerId 玩家ID
     * @param index    副本编号
     * @return 探索结果
     */
    public DungeonResultVO startExploreDungeon(Long playerId, int index) {
        return LockUtil.execute(Constants.EXPLORE_DUNGEON_LOCK_PREFIX + playerId, () ->
                DungeonService.getInstance().startExploreDungeon(playerId, index)
        );
    }

    /**
     * 继续探索副本
     *
     * @param playerId 玩家ID
     * @return 探索结果
     */
    public DungeonResultVO continueExploreDungeon(Long playerId) {
        return LockUtil.execute(Constants.EXPLORE_DUNGEON_LOCK_PREFIX + playerId, () ->
                DungeonService.getInstance().continueExploreDungeon(playerId)
        );
    }

    /**
     * 停止探索副本
     *
     * @param playerId 玩家ID
     * @return 探索结果
     */
    public DungeonResultVO stopExploreDungeon(Long playerId) {
        return LockUtil.execute(Constants.EXPLORE_DUNGEON_LOCK_PREFIX + playerId, () ->
                DungeonService.getInstance().stopExploreDungeon(playerId)
        );
    }

    /**
     * 获取副本信息
     *
     * @param playerId 玩家ID
     * @param index    副本编号
     * @return 副本信息
     */
    public DungeonVO getDungeonInfoByIndex(Long playerId, int index) {
        PlayerDTO playerDTO = PlayerCache.getPlayerById(playerId).getPlayerDTO();
        List<Dungeon> dungeonList = dungeonService.list();
        if (index < 1 || index > dungeonList.size()) {
            throw DungeonExceptionEnum.DUNGEON_NOT_FOUND.getException();
        }
        Dungeon dungeon = dungeonList.get(index - 1);
        if (Objects.isNull(dungeon)) {
            throw DungeonExceptionEnum.DUNGEON_NOT_FOUND.getException();
        }
        DungeonVO vo = new DungeonVO();
        vo.setDungeonName(dungeon.getName());
        vo.setFlushTime(dungeon.getFlushTime());
        vo.setMaxRank(RankEnum.getEnum(playerDTO.getJob(), dungeon.getMaxRank()).getRankName());
        return vo;
    }

    /**
     * 创建副本
     *
     * @param dungeonName 副本名称
     * @param maxRank     最高阶级
     * @param flushTime   刷新时间
     */
    public void createDungeon(String dungeonName, Integer maxRank, Long flushTime, BigDecimal passRate,
                              DungeonConfigSettingsDTO settings) {
        DungeonService.getInstance().createDungeon(dungeonName, maxRank, flushTime, passRate, settings);
    }

    public void addLoot(String mapName, String dungeonName, Integer rate, String lootType) {

    }

    public void deleteLoot() {

    }

    private static class Holder {
        private static final DungeonBehavior BEHAVIOR = new DungeonBehavior();
    }

    private DungeonBehavior() {

    }

    public static DungeonBehavior getInstance() {
        return Holder.BEHAVIOR;
    }
}
