package com.dingCreator.astrology.behavior;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.cache.TeamCache;
import com.dingCreator.astrology.cache.WorldBossCache;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.dto.MailBoxDTO;
import com.dingCreator.astrology.dto.WorldBossAwardDTO;
import com.dingCreator.astrology.dto.WorldBossRankRecordDTO;
import com.dingCreator.astrology.entity.WorldBoss;
import com.dingCreator.astrology.enums.exception.WorldBossExceptionEnum;
import com.dingCreator.astrology.response.PageResponse;
import com.dingCreator.astrology.service.MailBoxService;
import com.dingCreator.astrology.service.WorldBossRecordService;
import com.dingCreator.astrology.service.WorldBossService;
import com.dingCreator.astrology.util.ArticleUtil;
import com.dingCreator.astrology.util.CdUtil;
import com.dingCreator.astrology.util.ScheduleUtil;
import com.dingCreator.astrology.vo.WorldBossChartVO;
import com.dingCreator.astrology.vo.WorldBossResultVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ding
 * @date 2024/2/21
 */
public class WorldBossBehavior {

    private final WorldBossRecordService worldBossRecordService = WorldBossRecordService.getInstance();

    Logger logger = LoggerFactory.getLogger(WorldBossBehavior.class);

    /**
     * 讨伐世界boss
     *
     * @param playerId 玩家ID
     */
    public WorldBossResultVO attackWorldBoss(Long playerId) {
        long seconds = CdUtil.getAndSetCd(Constants.CD_WORLD_BOSS_PREFIX + playerId, Constants.ATTACK_WORLD_BOSS_CD);
        if (seconds > 0) {
            throw WorldBossExceptionEnum.IN_CD.getException(seconds);
        }
        // 状态校验
        WorldBossCache.BossPropDTO prop = WorldBossCache.getBossProp();
        if (Objects.isNull(prop)) {
            throw WorldBossExceptionEnum.NOT_EXIST.getException();
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(prop.getStartTime())) {
            throw WorldBossExceptionEnum.TOO_EARLY.getException();
        } else if (now.isAfter(prop.getEndTime())) {
            throw WorldBossExceptionEnum.TOO_LATE.getException();
        }
        List<Long> playerIdList;
        if (PlayerCache.getPlayerById(playerId).getTeam()) {
            TeamBehavior.getInstance().captainOnlyValidate(playerId);
            playerIdList = TeamCache.getTeamByPlayerId(playerId).getMembers();
        } else {
            playerIdList = Collections.singletonList(playerId);
        }
        return WorldBossService.getInstance().attackWorldBoss(playerId, playerIdList, prop);
    }

    /**
     * 查询我的排名
     *
     * @return 当前排名
     */
    public WorldBossChartVO queryPlayerCharts(Long playerId) {
        WorldBossRankRecordDTO record = worldBossRecordService.getPlayerRank(playerId, getWorldBossDetail().getWorldBossId());
        if (Objects.isNull(record)) {
            return null;
        }
        WorldBossChartVO vo = new WorldBossChartVO();
        vo.setRank(record.getDamageRank());
        vo.setPlayerName(PlayerCache.getPlayerById(record.getPlayerId()).getPlayerDTO().getName());
        vo.setDamage(record.getDamage());
        return vo;
    }

    /**
     * 查询排行榜
     *
     * @return 排行榜
     */
    public PageResponse<WorldBossChartVO> queryWorldBossCharts(int pageIndex, int pageSize) {
        PageResponse<WorldBossRankRecordDTO> pageResponse
                = worldBossRecordService.queryRankPage(getWorldBossDetail().getWorldBossId(), pageIndex, pageSize);
        PageResponse<WorldBossChartVO> result = new PageResponse<>();
        result.setPageIndex(pageResponse.getPageIndex());
        result.setPageSize(pageResponse.getPageSize());
        result.setTotal(pageResponse.getTotal());
        List<WorldBossChartVO> voList = new ArrayList<>(pageSize);
        if (CollectionUtils.isNotEmpty(pageResponse.getData())) {
            voList = pageResponse.getData().stream().map(record ->
                    WorldBossChartVO.builder().rank(record.getDamageRank())
                            .playerName(PlayerCache.getPlayerById(record.getPlayerId()).getPlayerDTO().getName())
                            .damage(record.getDamage()).build()
            ).collect(Collectors.toList());
        }
        result.setData(voList);
        return result;
    }

    /**
     * 世界boss详情
     *
     * @return 世界boss详情
     */
    public WorldBossCache.BossPropDTO getWorldBossDetail() {
        WorldBossCache.BossPropDTO prop = WorldBossCache.getBossProp();
        if (Objects.isNull(prop)) {
            throw WorldBossExceptionEnum.NOT_EXIST.getException();
        }
        return prop;
    }

    /**
     * 清除所有世界boss缓存
     */
    public void clearCache() {
        WorldBossCache.clearCache();
    }

    private static class Holder {
        private static final WorldBossBehavior BEHAVIOR = new WorldBossBehavior();
    }

    private WorldBossBehavior() {

    }

    public static WorldBossBehavior getInstance() {
        return WorldBossBehavior.Holder.BEHAVIOR;
    }

    /**
     * 初始化世界boss定时任务
     */
    public void initWorldBossTimer() {
        logger.info("world boss timer init...");
        // 每周五20点00分30秒定时发送奖励
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.setWeekDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.WEEK_OF_YEAR), 6);
        calendar.set(Calendar.HOUR, 20);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 30);
        ScheduleUtil.startTimer(() -> {
            try {
                distributeTimer();
            } catch (Exception e) {
                logger.error("发放世界boss奖励发生错误", e);
            }
        }, calendar.getTime(), 7 * 24 * 60 * 60 * 1000);
    }

    public void distributeTimer() {
        logger.info("start to distribute world boss award, start time:{}", LocalDateTime.now());
        WorldBossService worldBossService = WorldBossService.getInstance();
        WorldBossRecordService worldBossRecordService = WorldBossRecordService.getInstance();

        List<WorldBoss> worldBossList = worldBossService.listNotDistributeWorldBoss();
        if (CollectionUtils.isEmpty(worldBossList)) {
            return;
        }
        worldBossList.forEach(worldBoss -> {
            if (worldBoss.getDistribute()) {
                logger.info("{}-{}奖励已发放", worldBoss.getStartTime(), worldBoss.getEndTime());
                return;
            }

            String awardJson = worldBoss.getAward();
            if (Objects.isNull(awardJson) || awardJson.isEmpty()) {
                logger.info("{}-{}没有配置奖励", worldBoss.getStartTime(), worldBoss.getEndTime());
                return;
            }

            int i = 1;
            int total;
            do {
                PageResponse<WorldBossRankRecordDTO> response
                        = worldBossRecordService.queryRankPage(worldBoss.getId(), i, 500);
                if (CollectionUtils.isEmpty(response.getData())) {
                    logger.info("{}-{}没有挑战记录", worldBoss.getStartTime(), worldBoss.getEndTime());
                    break;
                }
                total = response.getTotal();
                distributeAward(convertAwardList(awardJson), response.getData());
                i++;
            } while (i * 500 < total);
            WorldBossService.getInstance().setDistributeFlag(worldBoss);
            WorldBossService.getInstance().recoverWorldBoss(worldBoss);
        });
    }

    public void distributeAward(List<WorldBossAwardDTO> awardList, List<WorldBossRankRecordDTO> recordList) {
        logger.info("开始发放排名奖励");
        // 只处理排名奖励
        awardList = awardList.stream()
                .filter(award -> WorldBossAwardDTO.WorldBossAwardTypeEnum.RANK_AWARD.getCode().equals(award.getAwardType()))
                .collect(Collectors.toList());
        // 排名
        Map<Integer, List<WorldBossRankRecordDTO>> rankPlayerMap = recordList.stream()
                .collect(Collectors.groupingBy(WorldBossRankRecordDTO::getDamageRank));
        // 发送奖励到邮箱
        List<MailBoxDTO> mailList = new ArrayList<>();
        awardList.forEach(award -> {
            String subject = "世界boss排名奖励：第%d名";
            if (Objects.isNull(award.getRankRangeEnd())) {
                for (int i = award.getRankRangeStart(); ; i++) {
                    List<Long> playerIdList = rankPlayerMap.getOrDefault(i, new ArrayList<>()).stream()
                            .map(WorldBossRankRecordDTO::getPlayerId).collect(Collectors.toList());
                    if (CollectionUtils.isEmpty(playerIdList)) {
                        break;
                    }
                    final int rank = i;
                    playerIdList.forEach(playerId -> award.getItemList().forEach(item ->
                            mailList.add(MailBoxDTO.builder().subject(String.format(subject, rank)).item(item)
                                    .playerId(playerId).createTime(LocalDateTime.now())
                                    .expireIn(LocalDateTime.now().plusDays(7L))
                                    .itemCnt(1).build())
                    ));
                }
            } else {
                for (int i = award.getRankRangeStart(); i <= award.getRankRangeEnd(); i++) {
                    List<Long> playerIdList = rankPlayerMap.getOrDefault(i, new ArrayList<>()).stream()
                            .map(WorldBossRankRecordDTO::getPlayerId).collect(Collectors.toList());
                    if (CollectionUtils.isEmpty(playerIdList)) {
                        continue;
                    }
                    final int rank = i;
                    playerIdList.forEach(playerId -> award.getItemList().forEach(item ->
                            mailList.add(MailBoxDTO.builder().subject(String.format(subject, rank)).item(item)
                                    .playerId(playerId).createTime(LocalDateTime.now())
                                    .expireIn(LocalDateTime.now().plusDays(7L))
                                    .itemCnt(1).build())
                    ));
                }
            }
        });
        MailBoxService.getInstance().sendMail(mailList);
    }

    /**
     * 获取今天奖励
     *
     * @return 奖励
     */
    public List<WorldBossAwardDTO> getNowAward() {
        WorldBossCache.BossPropDTO prop = WorldBossCache.getBossProp();
        if (Objects.isNull(prop)) {
            throw WorldBossExceptionEnum.NOT_EXIST.getException();
        }
        return prop.getAwardList();
    }

    @SuppressWarnings("unchecked")
    public List<WorldBossAwardDTO> convertAwardList(String awardJson) {
        List<JSONObject> list = JSONArray.parseArray(awardJson, JSONObject.class);
        return list.stream().map(jsonObj -> {
            WorldBossAwardDTO dto = new WorldBossAwardDTO();
            dto.setAwardType(jsonObj.getString("awardType"));
            dto.setRankRangeStart(jsonObj.getInteger("rankRangeStart"));
            dto.setRankRangeEnd(jsonObj.getInteger("rankRangeEnd"));
            dto.setDamageRangeStart(jsonObj.getLong("damageRangeStart"));
            dto.setDamageRangeEnd(jsonObj.getLong("damageRangeEnd"));
            List<JSONObject> itemJsonList = (List<JSONObject>) jsonObj.get("itemList");
            dto.setItemList(itemJsonList.stream()
                    .map(itemJson -> ArticleUtil.convert(itemJson.toJSONString()))
                    .collect(Collectors.toList()));
            return dto;
        }).collect(Collectors.toList());
    }
}
