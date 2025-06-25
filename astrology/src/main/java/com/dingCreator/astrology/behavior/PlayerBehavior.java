package com.dingCreator.astrology.behavior;

import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.dto.battle.BattleRoundDTO;
import com.dingCreator.astrology.dto.battle.BattleRoundRecordDTO;
import com.dingCreator.astrology.dto.equipment.EquipmentBarDTO;
import com.dingCreator.astrology.dto.organism.player.PlayerAssetDTO;
import com.dingCreator.astrology.dto.organism.player.PlayerDTO;
import com.dingCreator.astrology.dto.organism.player.PlayerInfoDTO;
import com.dingCreator.astrology.entity.Player;
import com.dingCreator.astrology.enums.AssetTypeEnum;
import com.dingCreator.astrology.enums.PlayerStatusEnum;
import com.dingCreator.astrology.enums.equipment.EquipmentPropertiesTypeEnum;
import com.dingCreator.astrology.enums.exception.PlayerExceptionEnum;
import com.dingCreator.astrology.enums.job.JobEnum;
import com.dingCreator.astrology.enums.job.JobInitPropertiesEnum;
import com.dingCreator.astrology.response.BaseResponse;
import com.dingCreator.astrology.response.PageResponse;
import com.dingCreator.astrology.service.PlayerService;
import com.dingCreator.astrology.service.SkillBarItemService;
import com.dingCreator.astrology.service.SkillBelongToService;
import com.dingCreator.astrology.util.BattleUtil;
import com.dingCreator.astrology.util.EquipmentUtil;
import com.dingCreator.astrology.util.PageUtil;
import com.dingCreator.astrology.vo.SimplePlayerInfoVO;
import com.dingCreator.astrology.vo.BattleResultVO;
import com.dingCreator.astrology.vo.PlayerInfoVO;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author ding
 * @date 2024/2/3
 */
public class PlayerBehavior {

    /**
     * 状态读写锁
     */
    private static final Object LOCK = new Object();

    /**
     * 创建角色
     *
     * @param id      玩家ID
     * @param name    玩家名称
     * @param jobName 职业名称
     */
    public void createPlayer(Long id, String name, String jobName) {
        // 获取职业
        JobEnum job = JobEnum.getByName(jobName);
        if (Objects.isNull(job)) {
            throw PlayerExceptionEnum.JOB_NOT_EXIST.getException();
        }
        // 设置初始属性
        Player player = new Player();
        player.setId(id);
        player.setName(name);
        player.setStatus(PlayerStatusEnum.FREE.getCode());
        player.setMapId(job.getMapId());
        player.setJob(job.getJobCode());
        player.setExp(0L);
        player.setLevel(Constants.MIN_LEVEL);
        player.setRank(Constants.MIN_RANK);
        player.setEnabled(true);

        JobInitPropertiesEnum prop = JobInitPropertiesEnum.getByCode(job.getJobCode());
        player.setHp(prop.getInitHp());
        player.setMaxHp(prop.getInitHp());
        player.setMp(prop.getInitMp());
        player.setMaxMp(prop.getInitMp());

        player.setAtk(prop.getInitAtk());
        player.setDef(prop.getInitDef());
        player.setMagicAtk(prop.getInitMagicAtk());
        player.setMagicDef(prop.getInitMagicDef());

        player.setBehaviorSpeed(prop.getInitBehaviorSpeed());
        player.setPenetrate(prop.getInitPenetrate());
        player.setMagicPenetrate(prop.getInitMagicPenetrate());
        player.setCriticalRate(prop.getInitCriticalRate());
        player.setCriticalReductionRate(prop.getInitCriticalReductionRate());
        player.setCriticalDamage(prop.getInitCriticalDamage());
        player.setCriticalDamageReduction(prop.getInitCriticalDamageReduction());

        player.setHit(prop.getInitHit());
        player.setDodge(prop.getInitDodge());
        player.setLifeStealing(prop.getInitLifeStealing());
        PlayerCache.createPlayer(player);
    }

    /**
     * 改名
     *
     * @param playerId 玩家ID
     * @param name     新名字
     */
    public void rename(Long playerId, String name) {
        PlayerInfoDTO playerInfoDTO = PlayerCache.getPlayerById(playerId);
        if (playerInfoDTO.getPlayerDTO().getName().equals(name)) {
            return;
        }
        Player player = PlayerService.getInstance().getPlayerByName(name);
        if (Objects.nonNull(player)) {
            throw PlayerExceptionEnum.NAME_EXIST.getException();
        }
        playerInfoDTO.getPlayerDTO().setName(name);
        PlayerCache.save(Collections.singletonList(playerId));
    }

    public PlayerInfoDTO getPlayerInfoDTOById(Long id) {
        return PlayerCache.getPlayerById(id);
    }

    /**
     * 查询玩家基础信息
     *
     * @param id 玩家ID
     * @return 玩家基础信息
     */
    public SimplePlayerInfoVO getSimplePlayerInfoById(Long id) {
        // 玩家信息
        PlayerInfoDTO playerInfoDTO = PlayerCache.getPlayerById(id);
        PlayerDTO playerDTO = playerInfoDTO.getPlayerDTO();
        EquipmentBarDTO bar = playerInfoDTO.getEquipmentBarDTO();
        return SimplePlayerInfoVO.builder()
                .id(playerDTO.getId())
                .name(playerDTO.getName())
                .job(playerDTO.getJob())
                .rank(playerDTO.getRank())
                .level(playerDTO.getLevel())
                .exp(playerDTO.getExp())
                .currentLevelMaxExp(ExpBehavior.getInstance().getCurrentLevelMaxExp(playerDTO.getLevel()))
                .hp(playerDTO.getHpWithAddition())
                .maxHp(playerDTO.getMaxHpWithAddition())
                .mp(playerDTO.getMpWithAddition())
                .maxMp(playerDTO.getMaxMpWithAddition())
                .atk(EquipmentUtil.getLongVal(playerDTO.getAtk(), EquipmentPropertiesTypeEnum.ATK, bar))
                .magicAtk(EquipmentUtil.getLongVal(playerDTO.getMagicAtk(), EquipmentPropertiesTypeEnum.MAGIC_ATK, bar))
                .status(playerDTO.getStatus()).build();
    }

    /**
     * 根据玩家ID获取玩家信息
     *
     * @param id 玩家ID
     * @return 玩家信息
     */
    public PlayerInfoVO getPlayerInfoById(Long id) {
        // 玩家信息
        PlayerInfoDTO playerInfoDTO = PlayerCache.getPlayerById(id);
        PlayerDTO playerDTO = playerInfoDTO.getPlayerDTO();
        EquipmentBarDTO bar = playerInfoDTO.getEquipmentBarDTO();
        return PlayerInfoVO.builder().id(playerDTO.getId())
                .name(playerDTO.getName())
                .job(playerDTO.getJob())
                .rank(playerDTO.getRank())
                .level(playerDTO.getLevel())
                .exp(playerDTO.getExp())
                .currentLevelMaxExp(ExpBehavior.getInstance().getCurrentLevelMaxExp(playerDTO.getLevel()))
                .hp(playerDTO.getHpWithAddition())
                .maxHp(playerDTO.getMaxHpWithAddition())
                .mp(playerDTO.getMpWithAddition())
                .maxMp(playerDTO.getMaxMpWithAddition())
                .atk(EquipmentUtil.getLongVal(playerDTO.getAtk(), EquipmentPropertiesTypeEnum.ATK, bar))
                .magicAtk(EquipmentUtil.getLongVal(playerDTO.getMagicAtk(), EquipmentPropertiesTypeEnum.MAGIC_ATK, bar))
                .def(EquipmentUtil.getLongVal(playerDTO.getDef(), EquipmentPropertiesTypeEnum.DEF, bar))
                .magicDef(EquipmentUtil.getLongVal(playerDTO.getMagicDef(), EquipmentPropertiesTypeEnum.MAGIC_DEF, bar))
                .penetrate(EquipmentUtil.getFloatVal(playerDTO.getPenetrate(), EquipmentPropertiesTypeEnum.PENETRATE, bar))
                .magicPenetrate(EquipmentUtil.getFloatVal(playerDTO.getMagicPenetrate(),
                        EquipmentPropertiesTypeEnum.MAGIC_PENETRATE, bar))
                .hit(EquipmentUtil.getLongVal(playerDTO.getHit(), EquipmentPropertiesTypeEnum.HIT, bar))
                .dodge(EquipmentUtil.getLongVal(playerDTO.getDodge(), EquipmentPropertiesTypeEnum.DODGE, bar))
                .behaviorSpeed(EquipmentUtil.getLongVal(playerDTO.getBehaviorSpeed(),
                        EquipmentPropertiesTypeEnum.BEHAVIOR_SPEED, bar))
                .criticalRate(EquipmentUtil.getFloatVal(playerDTO.getCriticalRate(),
                        EquipmentPropertiesTypeEnum.CRITICAL_RATE, bar))
                .criticalReductionRate(EquipmentUtil.getFloatVal(playerDTO.getCriticalReductionRate(),
                        EquipmentPropertiesTypeEnum.CRITICAL_REDUCTION_RATE, bar))
                .criticalDamage(EquipmentUtil.getFloatVal(playerDTO.getCriticalDamage(),
                        EquipmentPropertiesTypeEnum.CRITICAL_DAMAGE, bar))
                .criticalDamageReduction(EquipmentUtil.getFloatVal(playerDTO.getCriticalDamageReduction(),
                        EquipmentPropertiesTypeEnum.CRITICAL_DAMAGE_REDUCTION, bar))
                .status(playerDTO.getStatus()).build();
    }

    /**
     * 发起决斗
     *
     * @param initiatorId 发起者ID
     * @param recipientId 接受者ID
     */
    public long createBattle(Long initiatorId, Long recipientId) {
        String initiatorStatus = PlayerCache.getPlayerById(initiatorId).getPlayerDTO().getStatus();
        String recipientStatus = PlayerCache.getPlayerById(recipientId).getPlayerDTO().getStatus();
        if (PlayerStatusEnum.EXPLORE.getCode().equals(initiatorStatus)) {
            throw PlayerExceptionEnum.INITIATOR_EXPLORING.getException();
        }
        if (PlayerStatusEnum.EXPLORE.getCode().equals(recipientStatus)) {
            throw PlayerExceptionEnum.RECIPIENT_EXPLORING.getException();
        }
        return BattleUtil.createBattle(initiatorId, recipientId);
    }

    /**
     * 接受决斗
     *
     * @param recipientId 接受玩家ID
     * @return 战斗结果
     */
    public BaseResponse<BattleResultVO> acceptBattle(Long recipientId) {
        BattleResultVO vo = BattleUtil.acceptBattle(recipientId);
        BaseResponse<BattleResultVO> response = new BaseResponse<>();
        response.setContent(vo);
        return response;
    }

    /**
     * 拒绝决斗
     *
     * @param recipientId 被发起者ID
     * @return 发起者ID
     */
    public long refuseBattle(Long recipientId) {
        return BattleUtil.refuseBattle(recipientId);
    }

    /**
     * 读取玩家信息
     *
     * @param playerId 玩家ID
     * @return 玩家信息
     */
    public PlayerInfoDTO getById(Long playerId) {
        return PlayerCache.getPlayerById(playerId);
    }

    /**
     * 清除玩家信息缓存
     */
    public void clearCache() {
        PlayerCache.clearCache();
    }

    /**
     * 清除对战锁
     */
    public void clearBattleLock() {
        BattleUtil.clearLock();
    }

    /**
     * 获取最近一次对战详情
     *
     * @param playerId  玩家ID
     * @param pageIndex 页码
     * @param pageSize  页面大小
     * @return 对战详情
     */
    public PageResponse<String> getLastBattleProcess(long playerId, int pageIndex, int pageSize) {
        List<String> battleMsg = BattleUtil.getBattleProcess(playerId);
        return PageUtil.buildPage(battleMsg, pageIndex, pageSize);
    }

    /**
     * 获取玩家资产信息
     *
     * @param playerId 玩家ID
     * @return 玩家资产
     */
    public List<PlayerAssetDTO> getPlayerAssetById(long playerId) {
        return PlayerCache.getPlayerById(playerId).getAssetList();
    }

    /**
     * 变更玩家资产
     *
     * @param playerId  玩家ID
     * @param assetType 资产类型
     * @param val       变更量
     */
    public void changePlayerAsset(long playerId, String assetType, long val) {
        AssetTypeEnum assetTypeEnum = AssetTypeEnum.getByChnName(assetType);
        if (Objects.isNull(assetTypeEnum)) {
            throw PlayerExceptionEnum.ASSET_TYPE_ERR.getException();
        }
        PlayerService.getInstance().changeAsset(PlayerCache.getPlayerById(playerId),
                Collections.singletonList(PlayerAssetDTO.builder().playerId(playerId)
                        .assetType(assetTypeEnum.getCode()).assetCnt(val).build()));
    }

    public BattleResultVO PVP(Long playerId1, Long playerId2) {
        return BattleUtil.battlePVP(playerId1, playerId2);
    }

    public BattleRoundRecordDTO getBattleRound(Long playerId, int roundIdx) {
        return BattleUtil.getByPlayerIdAndRound(playerId, roundIdx);
    }

    public void recover(Long playerId) {
        PlayerInfoDTO info = PlayerCache.getPlayerById(playerId);
        PlayerDTO player = info.getPlayerDTO();
        player.clearAdditionVal();
        player.setHp(player.getMaxHp());
        player.setMp(player.getMaxMp());
        PlayerCache.save(playerId);
    }

    private static class Holder {
        private static final PlayerBehavior BEHAVIOR = new PlayerBehavior();
    }

    private PlayerBehavior() {

    }

    public static PlayerBehavior getInstance() {
        return Holder.BEHAVIOR;
    }
}
