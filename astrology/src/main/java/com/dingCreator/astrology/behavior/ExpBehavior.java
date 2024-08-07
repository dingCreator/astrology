package com.dingCreator.astrology.behavior;


import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.dto.player.PlayerDTO;
import com.dingCreator.astrology.dto.player.PlayerInfoDTO;
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
        PlayerDTO playerDTO = PlayerCache.getPlayerById(id).getPlayerDTO();
        long currentExp = playerDTO.getExp() + exp;
        int oldLevel = playerDTO.getLevel();
        long realExp = exp;

        while (currentExp >= getCurrentLevelMaxExp(playerDTO.getLevel())) {
            RankEnum rankEnum = RankEnum.getEnum(playerDTO.getJob(), playerDTO.getRank());
            if (playerDTO.getLevel() >= Constants.MAX_LEVEL) {
                long maxExp = getCurrentLevelMaxExp(Constants.MAX_LEVEL) - 1;
                // 减掉溢出经验值
                realExp -= (currentExp - maxExp);
                currentExp = maxExp;
                break;
            } else if (playerDTO.getLevel() >= rankEnum.getMaxLevel()) {
                // 上限了，不突破不允许升级
                long expLimit = getExpLimit(playerDTO.getLevel());
                if (currentExp >= expLimit) {
                    // 减掉溢出经验值
                    realExp -= (currentExp - expLimit);
                    currentExp = expLimit;
                }
                break;
            } else {
                long currentLevelMaxExp = getCurrentLevelMaxExp(playerDTO.getLevel());
                currentExp -= currentLevelMaxExp;
                playerDTO.setLevel(playerDTO.getLevel() + 1);
                // 属性提升
                JobInitPropertiesEnum base = JobInitPropertiesEnum.getByCode(playerDTO.getJob());
                playerDTO.setHp(playerDTO.getMaxHp() + getLevelUpIncrease(base.getInitHp(), playerDTO.getLevel()));
                playerDTO.setMaxHp(playerDTO.getMaxHp() + getLevelUpIncrease(base.getInitHp(), playerDTO.getLevel()));
                playerDTO.setMp(playerDTO.getMaxMp() + getLevelUpIncrease(playerDTO.getMaxMp(), 0.03F));
                playerDTO.setMaxMp(playerDTO.getMaxMp() + getLevelUpIncrease(playerDTO.getMaxMp(), 0.03F));
                playerDTO.setAtk(playerDTO.getAtk() + getLevelUpIncrease(base.getInitAtk(), playerDTO.getLevel()));
                playerDTO.setMagicAtk(playerDTO.getMagicAtk() + getLevelUpIncrease(base.getInitMagicAtk(), playerDTO.getLevel()));
                playerDTO.setDef(playerDTO.getDef() + getLevelUpIncrease(base.getInitDef(), playerDTO.getLevel()));
                playerDTO.setMagicDef(playerDTO.getMagicDef() + getLevelUpIncrease(base.getInitMagicDef(), playerDTO.getLevel()));
                playerDTO.setBehaviorSpeed(playerDTO.getBehaviorSpeed() + getLevelUpIncrease(playerDTO.getBehaviorSpeed(), 0.02F));
                playerDTO.setHit(playerDTO.getHit() + getLevelUpIncrease(base.getInitHit(), playerDTO.getLevel()));
                playerDTO.setDodge(playerDTO.getDodge() + getLevelUpIncrease(base.getInitDodge(), playerDTO.getLevel()));
            }
        }
        playerDTO.setExp(currentExp);
        PlayerCache.flush(id);
        return new LevelChange(oldLevel, playerDTO.getLevel(), realExp);
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
     * 获取升级提升
     *
     * @param base  基础数值
     * @param level 等级
     * @return 提升数值
     */
    private long getLevelUpIncrease(long base, int level) {
        return Math.round((1 + 0.2F * level) * base);
    }

    /**
     * 获取升级提升
     *
     * @param base 基础数值
     * @param rate 比例
     * @return 提升数值
     */
    private long getLevelUpIncrease(long base, float rate) {
        return Math.round(base * rate);
    }

    /**
     * 挂机
     *
     * @param id 玩家ID
     */
    public void hangUp(Long id) {
        PlayerInfoDTO playerInfoDTO = PlayerCache.getPlayerById(id);
        PlayerDTO playerDTO = playerInfoDTO.getPlayerDTO();
        if (!PlayerStatusEnum.FREE.getCode().equals(PlayerBehavior.getInstance().getStatus(playerDTO))) {
            throw ExpExceptionEnum.CANT_HANG_UP.getException();
        }
        PlayerBehavior.getInstance().updatePlayerStatus(playerDTO, PlayerStatusEnum.HANG_UP,
                () -> PlayerStatusEnum.FREE.getCode().equals(PlayerBehavior.getInstance().getStatus(playerDTO)),
                ExpExceptionEnum.CANT_HANG_UP.getException());
        PlayerCache.flush(Collections.singletonList(id));
    }

    /**
     * 停止挂机
     *
     * @param id 玩家ID
     * @return 挂机信息
     */
    public BaseResponse<HangUpVO> stopHangUp(Long id) {
        PlayerInfoDTO playerInfoDTO = PlayerCache.getPlayerById(id);
        PlayerDTO playerDTO = playerInfoDTO.getPlayerDTO();
        if (!PlayerStatusEnum.HANG_UP.getCode().equals(PlayerBehavior.getInstance().getStatus(playerDTO))) {
            throw ExpExceptionEnum.NOT_HANG_UP.getException();
        }

        HangUpVO hangUpVO = new HangUpVO();
        long between = DateUtil.between(playerDTO.getStatusStartTime(), new Date(), DateUnit.MINUTE);
        PlayerBehavior.getInstance().updatePlayerStatus(playerDTO, PlayerStatusEnum.FREE,
                () -> PlayerStatusEnum.HANG_UP.getCode().equals(PlayerBehavior.getInstance().getStatus(playerDTO)),
                ExpExceptionEnum.NOT_HANG_UP.getException());
        PlayerCache.flush(Collections.singletonList(id));

        // 回显真正的挂机时间
        hangUpVO.setHangUpTime(between);
        // 最长有收益挂机时间24h
        between = Math.min(between, Constants.MAX_HANG_UP_TIME);
        // 回复状态
        long maxHp = playerDTO.getMaxHpWithAddition();
        long oldHp = playerDTO.getHpWithAddition();
        long maxMp = playerDTO.getMaxMpWithAddition();
        long oldMp = playerDTO.getMpWithAddition();
        float recharge = RankEnum.getEnum(playerDTO.getJob(), playerDTO.getRank()).getRechargeRatePerMin();
        long newHp = Math.min(maxHp, oldHp + Math.round(maxHp * recharge * between));
        long newMp = Math.min(maxMp, oldMp + Math.round(maxMp * recharge * between));
        playerDTO.setHp(newHp);
        playerDTO.setMp(newMp);
        // 计算经验值
        long exp = (10 + (long) Math.pow(playerDTO.getLevel() - 1, 3) / 30) * between;
        LevelChange levelChange = getExp(id, exp);
        // 回显实际获得的经验值以及状态变化
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
