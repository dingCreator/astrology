package com.dingCreator.astrology.behavior;

import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.cache.TeamCache;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.dto.OrganismDTO;
import com.dingCreator.astrology.dto.PlayerDTO;
import com.dingCreator.astrology.dto.TeamDTO;
import com.dingCreator.astrology.entity.Player;
import com.dingCreator.astrology.entity.SkillBarItem;
import com.dingCreator.astrology.enums.BelongToEnum;
import com.dingCreator.astrology.enums.PlayerStatusEnum;
import com.dingCreator.astrology.enums.PropertiesTypeEnum;
import com.dingCreator.astrology.enums.RankEnum;
import com.dingCreator.astrology.enums.exception.PlayerExceptionEnum;
import com.dingCreator.astrology.enums.exception.TeamExceptionEnum;
import com.dingCreator.astrology.enums.job.JobEnum;
import com.dingCreator.astrology.enums.job.JobInitPropertiesEnum;
import com.dingCreator.astrology.enums.skill.SkillEnum;
import com.dingCreator.astrology.exception.BusinessException;
import com.dingCreator.astrology.response.BaseResponse;
import com.dingCreator.astrology.service.PlayerService;
import com.dingCreator.astrology.service.SkillBarItemService;
import com.dingCreator.astrology.service.SkillBelongToService;
import com.dingCreator.astrology.util.BattleUtil;
import com.dingCreator.astrology.util.EquipmentUtil;
import com.dingCreator.astrology.util.MapUtil;
import com.dingCreator.astrology.util.SkillUtil;
import com.dingCreator.astrology.vo.BattleResultVO;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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

        // 装备技能栏
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
        PlayerDTO playerDTO = PlayerCache.getPlayerById(playerId);
        if (playerDTO.getPlayer().getName().equals(name)) {
            return;
        }
        // todo 敏感词校验
        Player player = PlayerService.getPlayerByName(name);
        if (Objects.nonNull(player)) {
            throw PlayerExceptionEnum.NAME_EXIST.getException();
        }
        playerDTO.getPlayer().setName(name);
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
        PlayerDTO playerDTO = PlayerCache.getPlayerById(id);
        Player player = playerDTO.getPlayer();
        // 生物信息
        OrganismDTO organismDTO = new OrganismDTO();
        organismDTO.setOrganism(playerDTO.getPlayer());

        List<String> list = new ArrayList<>(16);
        list.add("昵称：" + player.getName() + " 职业：" + JobEnum.getByCode(player.getJob()).getJobName());
        list.add("阶级：" + RankEnum.getEnum(player.getJob(), player.getRank()).getRankName());
        list.add("等级：" + player.getLevel() + " 经验：" + player.getExp()
                + "/" + ExpBehavior.getInstance().getCurrentLevelMaxExp(player.getLevel()));
        list.add("血量：" + player.getHp() + "/" + player.getMaxHp());
        list.add("蓝量：" + player.getMp() + "/" + player.getMaxMp());
        list.add("物攻：" + player.getAtk());
        list.add("物防：" + player.getDef());
        list.add("魔攻：" + player.getMagicAtk());
        list.add("魔防：" + player.getMagicDef());
        list.add("穿甲：" + player.getPenetrate() * 100 + "%");
        list.add("命中：" + player.getHit());
        list.add("闪避：" + player.getDodge());
        list.add("速度：" + player.getBehaviorSpeed());
        list.add("暴击：" + player.getCriticalRate() * 100 + "%");
        list.add("暴击减免：" + player.getCriticalReductionRate() * 100 + "%");
        list.add("暴伤：" + player.getCriticalDamage() * 100 + "%");
        list.add("暴伤减免：" + player.getCriticalDamageReduction() * 100 + "%");
        list.add("状态：" + PlayerStatusEnum.getByCode(getStatus(player)).getName());
        list.add("所在地图：" + MapUtil.getMapById(MapUtil.getNowLocation(player.getId())).getName());
        BaseResponse<List<String>> baseResponse = new BaseResponse<>();
        baseResponse.setContent(list);
        return baseResponse;
    }

    public void updatePlayerStatus(Player player, PlayerStatusEnum newStatus,
                                                       Supplier<Boolean> supplier, BusinessException exception) {
        synchronized (LOCK) {
            if (!supplier.get()) {
                throw exception;
            }
            player.setStatus(newStatus.getCode());
            player.setStatusStartTime(new Date());
        }
    }

    /**
     * 获取当前状态
     *
     * @return 当前状态
     */
    public String getStatus(Player player) {
        synchronized (LOCK) {
            if (PlayerStatusEnum.MOVING.getCode().equals(player.getStatus())) {
                Long targetMapId = MapUtil.getTargetLocation(player.getId());
                if (targetMapId == 0L) {
                    player.setStatus(PlayerStatusEnum.FREE.getCode());
                    PlayerCache.flush(Collections.singletonList(player.getId()));
                } else {
                    long seconds = MapUtil.moveTime(player.getId(), MapUtil.getNowLocation(player.getId()), targetMapId);
                    if (System.currentTimeMillis() >= player.getStatusStartTime().getTime() + seconds * 1000) {
                        // 已经到了
                        player.setStatus(PlayerStatusEnum.FREE.getCode());
                        player.setStatusStartTime(new Date());
                        player.setMapId(MapUtil.getTargetLocation(player.getId()));
                        PlayerCache.flush(Collections.singletonList(player.getId()));
                    }
                }
            }
            return player.getStatus();
        }
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

    private static class Holder {
        private static final PlayerBehavior BEHAVIOR = new PlayerBehavior();
    }

    private PlayerBehavior() {

    }

    public static PlayerBehavior getInstance() {
        return Holder.BEHAVIOR;
    }
}
