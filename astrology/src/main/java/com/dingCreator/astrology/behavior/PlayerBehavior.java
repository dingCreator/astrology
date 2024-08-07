package com.dingCreator.astrology.behavior;

import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.dto.OrganismDTO;
import com.dingCreator.astrology.dto.player.PlayerDTO;
import com.dingCreator.astrology.dto.player.PlayerInfoDTO;
import com.dingCreator.astrology.entity.Player;
import com.dingCreator.astrology.entity.SkillBarItem;
import com.dingCreator.astrology.enums.BelongToEnum;
import com.dingCreator.astrology.enums.PlayerStatusEnum;
import com.dingCreator.astrology.enums.RankEnum;
import com.dingCreator.astrology.enums.exception.PlayerExceptionEnum;
import com.dingCreator.astrology.enums.job.JobEnum;
import com.dingCreator.astrology.enums.job.JobInitPropertiesEnum;
import com.dingCreator.astrology.enums.skill.SkillEnum;
import com.dingCreator.astrology.exception.BusinessException;
import com.dingCreator.astrology.response.BaseResponse;
import com.dingCreator.astrology.service.PlayerService;
import com.dingCreator.astrology.service.SkillBarItemService;
import com.dingCreator.astrology.service.SkillBelongToService;
import com.dingCreator.astrology.util.BattleUtil;
import com.dingCreator.astrology.util.MapUtil;
import com.dingCreator.astrology.util.SkillUtil;
import com.dingCreator.astrology.vo.BattleResultVO;

import java.util.*;
import java.util.function.Supplier;

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

        // 赠送默认技能
        SkillBelongToService.createSkillBelongTo(BelongToEnum.Player.getBelongTo(), id,
                SkillEnum.getDefaultSkillByJob(job.getJobCode()).getId());

        // 将默认技能装备到技能栏
        List<Long> skillIds = new ArrayList<>();
        skillIds.add(SkillEnum.getDefaultSkillByJob(job.getJobCode()).getId());
        SkillBarItem skillBarItem = SkillUtil.buildSkillBarItemChain(skillIds, BelongToEnum.Player, id);
        SkillBarItemService.addSkillBarItem(skillBarItem);
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
        // todo 敏感词校验
        Player player = PlayerService.getPlayerByName(name);
        if (Objects.nonNull(player)) {
            throw PlayerExceptionEnum.NAME_EXIST.getException();
        }
        playerInfoDTO.getPlayerDTO().setName(name);
        PlayerCache.flush(Collections.singletonList(playerId));
    }

    /**
     * 根据玩家ID获取玩家信息
     *
     * @param id 玩家ID
     * @return 玩家信息
     */
    public BaseResponse<List<String>> getPlayerInfoById(Long id) {
        // 玩家信息
        PlayerInfoDTO playerInfoDTO = PlayerCache.getPlayerById(id);
        PlayerDTO PlayerDTO = playerInfoDTO.getPlayerDTO();
        // 生物信息
        OrganismDTO organismDTO = new OrganismDTO();
        organismDTO.setOrganism(playerInfoDTO.getPlayerDTO());

        List<String> list = new ArrayList<>(16);
        list.add("昵称：" + PlayerDTO.getName() + " 职业：" + JobEnum.getByCode(PlayerDTO.getJob()).getJobName());
        list.add("阶级：" + RankEnum.getEnum(PlayerDTO.getJob(), PlayerDTO.getRank()).getRankName());
        list.add("等级：" + PlayerDTO.getLevel() + " 经验：" + PlayerDTO.getExp()
                + "/" + ExpBehavior.getInstance().getCurrentLevelMaxExp(PlayerDTO.getLevel()));
        list.add("血量：" + PlayerDTO.getHp() + "/" + PlayerDTO.getMaxHp());
        list.add("蓝量：" + PlayerDTO.getMp() + "/" + PlayerDTO.getMaxMp());
        list.add("攻击：" + PlayerDTO.getAtk());
        list.add("防御：" + PlayerDTO.getDef());
        list.add("法强：" + PlayerDTO.getMagicAtk());
        list.add("法抗：" + PlayerDTO.getMagicDef());
        list.add("穿甲：" + PlayerDTO.getPenetrate() * 100 + "%");
        list.add("法穿：" + PlayerDTO.getMagicPenetrate() * 100 + "%");
        list.add("命中：" + PlayerDTO.getHit());
        list.add("闪避：" + PlayerDTO.getDodge());
        list.add("速度：" + PlayerDTO.getBehaviorSpeed());
        list.add("暴击：" + PlayerDTO.getCriticalRate() * 100 + "%");
        list.add("抗暴：" + PlayerDTO.getCriticalReductionRate() * 100 + "%");
        list.add("暴伤：" + PlayerDTO.getCriticalDamage() * 100 + "%");
        list.add("暴免：" + PlayerDTO.getCriticalDamageReduction() * 100 + "%");
        list.add("状态：" + PlayerStatusEnum.getByCode(getStatus(PlayerDTO)).getName());
        list.add("所在地图：" + MapUtil.getMapById(MapUtil.getNowLocation(PlayerDTO.getId())).getName());
        BaseResponse<List<String>> baseResponse = new BaseResponse<>();
        baseResponse.setContent(list);
        return baseResponse;
    }

    /**
     * 无需校验前置状态带锁更新玩家状态
     *
     * @param playerDTO 玩家
     * @param newStatus 新状态
     */
    public void updatePlayerStatus(PlayerDTO playerDTO, PlayerStatusEnum newStatus) {
        updatePlayerStatus(playerDTO, newStatus, () -> true, null);
    }

    /**
     * 自旋
     *
     * @param playerDTO 玩家
     * @param newStatus 新状态
     */
    public void casPlayerStatus(PlayerDTO playerDTO, PlayerStatusEnum oldStatus, PlayerStatusEnum newStatus,
                                BusinessException exception) {
        synchronized (LOCK) {
            flushStatus(playerDTO);
            if (!playerDTO.getStatus().equals(oldStatus.getCode())) {
                throw exception;
            }
            playerDTO.setStatus(newStatus.getCode());
            playerDTO.setStatusStartTime(new Date());
            PlayerCache.flush(Collections.singletonList(playerDTO.getId()));
        }
    }

    /**
     * 带锁更新玩家状态
     * 所有更新玩家状态的操作都建议使用此方法
     *
     * @param playerDTO 玩家
     * @param newStatus 新状态
     * @param supplier  更新前校验
     * @param exception 更新前校验出错时的报错
     */
    public void updatePlayerStatus(PlayerDTO playerDTO, PlayerStatusEnum newStatus,
                                   Supplier<Boolean> supplier, BusinessException exception) {
        synchronized (LOCK) {
            flushStatus(playerDTO);
            if (!supplier.get()) {
                throw exception;
            }
            playerDTO.setStatus(newStatus.getCode());
            playerDTO.setStatusStartTime(new Date());
            PlayerCache.flush(Collections.singletonList(playerDTO.getId()));
        }
    }

    /**
     * 获取当前状态
     *
     * @return 当前状态
     */
    public String getStatus(PlayerDTO playerDTO) {
        if (PlayerStatusEnum.MOVING.getCode().equals(playerDTO.getStatus())) {
            synchronized (LOCK) {
                flushStatus(playerDTO);
            }
        }
        return playerDTO.getStatus();
    }

    /**
     * 发起决斗
     *
     * @param initiatorId 发起者ID
     * @param recipientId 接受者ID
     */
    public long createBattle(Long initiatorId, Long recipientId) {
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
     * 刷新状态
     */
    public void flushStatus(PlayerDTO playerDTO) {
        synchronized (LOCK) {
            if (PlayerStatusEnum.MOVING.getCode().equals(playerDTO.getStatus())) {
                long targetMapId = MapUtil.getTargetLocation(playerDTO.getId());
                if (targetMapId == 0L) {
                    playerDTO.setStatus(PlayerStatusEnum.FREE.getCode());
                    PlayerCache.flush(Collections.singletonList(playerDTO.getId()));
                } else {
                    long seconds = MapUtil.moveTime(playerDTO.getId(), MapUtil.getNowLocation(playerDTO.getId()), targetMapId);
                    long statusEndTime = Objects.isNull(playerDTO.getStatusStartTime()) ?
                            0 : playerDTO.getStatusStartTime().getTime() + seconds * 1000;
                    if (System.currentTimeMillis() >= statusEndTime) {
                        // 已经到了
                        playerDTO.setStatus(PlayerStatusEnum.FREE.getCode());
                        playerDTO.setStatusStartTime(new Date());
                        playerDTO.setMapId(MapUtil.getTargetLocation(playerDTO.getId()));
                        PlayerCache.flush(Collections.singletonList(playerDTO.getId()));
                    }
                }
            }
        }
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
     * @param playerId 玩家ID
     * @return 对战详情
     */
    public List<String> getLastBattleProcess(long playerId) {
        return BattleUtil.getBattleProcess(playerId);
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
