package com.dingCreator.astrology.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dingCreator.astrology.behavior.MailBoxBehavior;
import com.dingCreator.astrology.behavior.WorldBossBehavior;
import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.cache.WorldBossCache;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.database.DatabaseProvider;
import com.dingCreator.astrology.dto.MailBoxDTO;
import com.dingCreator.astrology.dto.WorldBossAwardDTO;
import com.dingCreator.astrology.dto.article.ArticleItemDTO;
import com.dingCreator.astrology.dto.organism.player.PlayerAssetDTO;
import com.dingCreator.astrology.entity.WorldBoss;
import com.dingCreator.astrology.entity.WorldBossRecord;
import com.dingCreator.astrology.enums.AssetTypeEnum;
import com.dingCreator.astrology.enums.exception.WorldBossExceptionEnum;
import com.dingCreator.astrology.mapper.WorldBossMapper;
import com.dingCreator.astrology.util.BattleUtil;
import com.dingCreator.astrology.vo.WorldBossResultVO;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ding
 * @date 2024/8/7
 */
public class WorldBossService {

    private static final Object MONITOR = new Object();

    private final MonsterService monsterService = MonsterService.getInstance();

    /**
     * 查询今天的boss
     *
     * @return boss
     */
    public WorldBoss getNowBoss() {
        return DatabaseProvider.getInstance().executeReturn(sqlSession ->
                sqlSession.getMapper(WorldBossMapper.class).selectOne(
                        new QueryWrapper<WorldBoss>()
                                .le(WorldBoss.START_TIME, LocalDateTime.now())
                                .ge(WorldBoss.END_TIME, LocalDateTime.now())
                )
        );
    }

    /**
     * 查询未发放奖励的boss
     *
     * @return boss
     */
    public List<WorldBoss> listNotDistributeWorldBoss() {
        return DatabaseProvider.getInstance().executeReturn(sqlSession ->
                sqlSession.getMapper(WorldBossMapper.class).selectList(
                        new QueryWrapper<WorldBoss>().eq(WorldBoss.DISTRIBUTE, false)
                )
        );
    }

    /**
     * 创建世界boss
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param monsterId 怪物ID
     * @param awardList 奖品
     */
    public void insertOrUpdateWorldBoss(LocalDateTime startTime, LocalDateTime endTime,
                                        String monsterId, List<WorldBossAwardDTO> awardList) {
        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("开始时间不能晚于结束时间");
        }
        WorldBoss worldBoss = WorldBoss.builder()
                .startTime(startTime)
                .endTime(endTime)
                .monsterId(monsterId)
                .award(JSONObject.toJSONString(awardList))
                .build();

        DatabaseProvider.getInstance().execute(sqlSession -> {
            WorldBossMapper mapper = sqlSession.getMapper(WorldBossMapper.class);
            // 与现有配置存在交集，则覆盖
            WorldBoss exist = mapper.selectOne(new QueryWrapper<WorldBoss>()
                    .ge(WorldBoss.START_TIME, endTime)
                    .le(WorldBoss.END_TIME, startTime)
            );
            if (Objects.nonNull(exist)) {
                worldBoss.setId(exist.getId());
                mapper.updateById(worldBoss);
            } else {
                worldBoss.setDistribute(false);
                mapper.insert(worldBoss);
            }
        });
    }

    public void setDistributeFlag(WorldBoss worldBoss) {
        worldBoss.setDistribute(true);
        DatabaseProvider.getInstance().execute(sqlSession -> sqlSession.getMapper(WorldBossMapper.class).updateById(worldBoss));
    }

    public void recoverWorldBoss(WorldBoss worldBoss) {
        Arrays.stream(worldBoss.getMonsterId().split(Constants.COMMA)).map(Long::parseLong)
                .forEach(monsterService::recoverById);
    }

    public WorldBossResultVO attackWorldBoss(Long captainId, List<Long> playerIdList, WorldBossCache.BossPropDTO prop) {
        WorldBossResultVO vo = new WorldBossResultVO();
        DatabaseProvider.getInstance().batchTransactionExecute(sqlSession -> {
            List<WorldBossRecord> recordList = playerIdList.stream().map(playerId -> {
                WorldBossRecord worldBossRecord = new WorldBossRecord();
                worldBossRecord.setWorldBossId(prop.getWorldBossId());
                worldBossRecord.setCreateTime(LocalDateTime.now());
                worldBossRecord.setPlayerId(playerId);
                return worldBossRecord;
            }).collect(Collectors.toList());
            synchronized (MONITOR) {
                if (WorldBossCache.getDefeated()) {
                    throw WorldBossExceptionEnum.DEFEATED.getException();
                }
                // 战斗前boss血量总值
                long hpBeforeBattle = prop.getMonsterMap().values().stream().mapToLong(o -> o.getOrganismDTO()
                        .getHpWithAddition()).sum();
                // 开打
                BattleUtil.battlePVE(captainId, new ArrayList<>(prop.getMonsterMap().values()), true, false,
                        () -> prop.getMonsterMap().forEach((key, value) ->
                                monsterService.updateHpById(key, value.getOrganismDTO().getHp()))
                );
                // 消耗令牌
                useToken(playerIdList);
                // 战斗后boss血量总值
                long hpAfterBattle = prop.getMonsterMap().values().stream().mapToLong(o -> o.getOrganismDTO()
                        .getHpWithAddition()).sum();
                long damage = hpBeforeBattle - hpAfterBattle;
                recordList.forEach(record -> record.setDamage(damage));
                vo.setDamage(damage);

                WorldBossAwardDTO worldBossAward = prop.getAwardList().stream()
                        .filter(award -> WorldBossAwardDTO.WorldBossAwardTypeEnum.DAMAGE_AWARD.getCode().equals(award.getAwardType()))
                        .filter(award -> damage >= award.getDamageRangeStart())
                        .filter(award -> Objects.isNull(award.getDamageRangeEnd()) || damage <= award.getDamageRangeEnd())
                        .findFirst().orElse(null);
                if (Objects.nonNull(worldBossAward)) {
                    List<ArticleItemDTO> itemList = worldBossAward.getItemList();
                    List<MailBoxDTO> mailList = playerIdList.stream().map(playerId ->
                            itemList.stream().map(item -> MailBoxDTO.builder()
                                    .subject("世界boss伤害奖励").playerId(playerId).item(item).itemCnt(1)
                                    .createTime(LocalDateTime.now()).expireIn(LocalDateTime.now().plusDays(7)).build()
                            ).collect(Collectors.toList())
                    ).flatMap(Collection::stream).collect(Collectors.toList());
                    MailBoxBehavior.getInstance().sendMail(mailList);
                    vo.setArticleItemVOList(itemList.stream().map(ArticleItemDTO::view).collect(Collectors.toList()));
                }
            }
            recordList.stream().filter(record -> record.getDamage() > 0).forEach(record -> {
                WorldBossRecordService worldBossRecordService = WorldBossRecordService.getInstance();
                WorldBossRecord bf = worldBossRecordService.getByPlayerId(record.getPlayerId(), record.getWorldBossId());
                if (Objects.isNull(bf)) {
                    worldBossRecordService.insert(record);
                } else if (record.getDamage() > bf.getDamage()){
                    bf.setDamage(record.getDamage());
                    worldBossRecordService.updateById(bf);
                }
            });
        });
        return vo;
    }

    public static void useToken(List<Long> playerIdList) {
        playerIdList.forEach(playerId -> {
            List<PlayerAssetDTO> changeList = Collections.singletonList(
                    PlayerAssetDTO.builder().playerId(playerId).assetType(AssetTypeEnum.WORLD_BOSS_TOKEN.getCode())
                            .assetCnt(-1L).build()
            );
            PlayerService.getInstance().changeAsset(PlayerCache.getPlayerById(playerId), changeList);
        });
    }


    private static class Holder {
        private static final WorldBossService SERVICE = new WorldBossService();
    }

    private WorldBossService() {

    }

    public static WorldBossService getInstance() {
        return WorldBossService.Holder.SERVICE;
    }
}
