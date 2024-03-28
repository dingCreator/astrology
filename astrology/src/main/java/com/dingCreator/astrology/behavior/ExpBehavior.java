package com.dingCreator.astrology.behavior;


import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.dto.PlayerDTO;
import com.dingCreator.astrology.entity.Player;
import com.dingCreator.astrology.enums.PlayerStatusEnum;
import com.dingCreator.astrology.enums.RankEnum;
import com.dingCreator.astrology.enums.exception.ExpExceptionEnum;
import com.dingCreator.astrology.enums.job.JobInitPropertiesEnum;
import com.dingCreator.astrology.response.BaseResponse;
import com.dingCreator.astrology.vo.HangUpVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.Date;

/**
 * @author ding
 * @date 2024/2/1
 */
public class ExpBehavior {

    /**
     * 经验值发生改变
     *
     * @param id  玩家ID
     * @param exp 改变的经验值
     */
    public synchronized LevelChange getExp(Long id, Long exp) {
        Player player = PlayerCache.getPlayerById(id).getPlayer();
        long currentExp = player.getExp() + exp;
        int oldLevel = player.getLevel();
        long realExp = 0L;

        while (currentExp >= getCurrentLevelMaxExp(player.getLevel())) {
            RankEnum rankEnum = RankEnum.getEnum(player.getJob(), player.getRank());
            if (player.getLevel() >= Constants.MAX_LEVEL) {
                currentExp = getCurrentLevelMaxExp(Constants.MAX_LEVEL) - 1;
                realExp += (currentExp - player.getExp());
                break;
            } else if (player.getLevel() >= rankEnum.getMaxLevel()) {
                // 上限了，不突破不允许升级
                long expLimit = getExpLimit(player.getLevel());
                if (currentExp >= expLimit) {
                    currentExp = expLimit;
                }
                realExp += (currentExp - player.getExp());
                break;
            } else {
                long currentLevelMaxExp = getCurrentLevelMaxExp(player.getLevel());
                currentExp -= currentLevelMaxExp;
                realExp += currentLevelMaxExp;
                player.setLevel(player.getLevel() + 1);
                // 此处为了统计实际获得的经验值
                player.setExp(0L);
                // 属性提升
                player.setHp(player.getMaxHp() + JobInitPropertiesEnum.getByCode(player.getJob()).getInitHp());
                player.setMaxHp(player.getMaxHp() + JobInitPropertiesEnum.getByCode(player.getJob()).getInitHp());
                player.setMp(player.getMaxMp() + JobInitPropertiesEnum.getByCode(player.getJob()).getInitMp());
                player.setMaxMp(player.getMaxMp() + JobInitPropertiesEnum.getByCode(player.getJob()).getInitMp());
                player.setAtk(player.getAtk() + JobInitPropertiesEnum.getByCode(player.getJob()).getInitAtk());
                player.setMagicAtk(player.getMagicAtk() + JobInitPropertiesEnum.getByCode(player.getJob()).getInitMagicAtk());
                player.setDef(player.getDef() + JobInitPropertiesEnum.getByCode(player.getJob()).getInitDef());
                player.setMagicDef(player.getMagicDef() + JobInitPropertiesEnum.getByCode(player.getJob()).getInitMagicDef());
                player.setBehaviorSpeed(player.getBehaviorSpeed() + JobInitPropertiesEnum.getByCode(player.getJob()).getInitBehaviorSpeed());
                player.setHit(player.getHit() + JobInitPropertiesEnum.getByCode(player.getJob()).getInitHit());
                player.setDodge(player.getDodge() + JobInitPropertiesEnum.getByCode(player.getJob()).getInitDodge());
            }
        }
        player.setExp(currentExp);
        PlayerCache.flush(Collections.singletonList(id));
        return new LevelChange(oldLevel, player.getLevel(), realExp + currentExp);
    }

    /**
     * 获取当前等级最大经验值
     *
     * @param level 等级
     * @return 最大经验值
     */
    public long getCurrentLevelMaxExp(int level) {
        return 100 + (long) Math.pow(level - 1, 4);
    }

    /**
     * 若不突破，最多可储存的经验
     *
     * @param level 等级
     * @return 最多可储存的经验
     */
    private long getExpLimit(int level) {
        long exp = 0L;
        for (int i = 0; i < Constants.MAX_TMP_LEVEL; i++) {
            exp += getCurrentLevelMaxExp(level + i);
        }
        return exp;
    }

    /**
     * 挂机
     *
     * @param id 玩家ID
     */
    public void hangUp(Long id) {
        PlayerDTO playerDTO = PlayerCache.getPlayerById(id);
        Player player = playerDTO.getPlayer();
        if (!PlayerStatusEnum.FREE.getCode().equals(PlayerBehavior.getInstance().getStatus(id))) {
            throw ExpExceptionEnum.CANT_HANG_UP.getException();
        }
        player.setStatus(PlayerStatusEnum.HANG_UP.getCode());
        player.setStatusStartTime(new Date());
        PlayerCache.flush(Collections.singletonList(id));
    }

    /**
     * 停止挂机
     *
     * @param id 玩家ID
     * @return 挂机信息
     */
    public BaseResponse<HangUpVO> stopHangUp(Long id) {
        PlayerDTO playerDTO = PlayerCache.getPlayerById(id);
        Player player = playerDTO.getPlayer();
        if (!PlayerStatusEnum.HANG_UP.getCode().equals(player.getStatus())) {
            throw ExpExceptionEnum.NOT_HANG_UP.getException();
        }
        player.setStatus(PlayerStatusEnum.FREE.getCode());
        PlayerCache.flush(Collections.singletonList(id));

        HangUpVO hangUpVO = new HangUpVO();
        long between = DateUtil.between(player.getStatusStartTime(), new Date(), DateUnit.MINUTE);
        hangUpVO.setHangUpTime(between);

        between = Math.min(between, Constants.MAX_HANG_UP_TIME);

        long maxHp = player.getMaxHp();
        long oldHp = player.getHp();
        long maxMp = player.getMaxMp();
        long oldMp = player.getMp();
        float recharge = RankEnum.getEnum(player.getJob(), player.getRank()).getRechargeRatePerMin();
        long newHp = Math.min(maxHp, oldHp + Math.round(maxHp * recharge * between));
        long newMp = Math.min(maxMp, oldMp + Math.round(maxMp * recharge * between));
        player.setHp(newHp);
        player.setMp(newMp);

        long exp = (10 + (long) Math.pow(player.getLevel() - 1, 3)) * between;
        LevelChange levelChange = getExp(id, exp);
        hangUpVO.setExp(levelChange.getExp());
        hangUpVO.setOldLevel(levelChange.getOldLevel());
        hangUpVO.setNewLevel(levelChange.getNewLevel());
        hangUpVO.setHp(newHp - oldHp);
        hangUpVO.setMp(newMp - oldMp);
        BaseResponse<HangUpVO> response = new BaseResponse<>();
        response.setContent(hangUpVO);
        return response;
    }

    /**
     * 等级变化
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class LevelChange {
        /**
         * 旧等级
         */
        private Integer oldLevel;
        /**
         * 新等级
         */
        private Integer newLevel;
        /**
         * 实际获得的经验
         */
        private Long exp;
    }

    private static class Holder {
        private static final ExpBehavior BEHAVIOR = new ExpBehavior();
    }

    private ExpBehavior() {

    }

    public static ExpBehavior getInstance() {
        return ExpBehavior.Holder.BEHAVIOR;
    }
}
