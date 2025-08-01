package com.dingCreator.astrology.behavior;


import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.dto.organism.player.PlayerDTO;
import com.dingCreator.astrology.dto.organism.player.PlayerInfoDTO;
import com.dingCreator.astrology.enums.PlayerStatusEnum;
import com.dingCreator.astrology.enums.RankEnum;
import com.dingCreator.astrology.enums.exception.ExpExceptionEnum;
import com.dingCreator.astrology.enums.job.JobEnum;
import com.dingCreator.astrology.enums.job.JobInitPropertiesEnum;
import com.dingCreator.astrology.enums.job.JobLevelAwardEnum;
import com.dingCreator.astrology.response.BaseResponse;
import com.dingCreator.astrology.util.LockUtil;
import com.dingCreator.astrology.vo.HangUpVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
    public LevelChange getExp(Long id, Long exp) {
        return LockUtil.execute(Constants.PLAYER_LOCK_PREFIX + id, () -> {
            PlayerDTO playerDTO = PlayerCache.getPlayerById(id).getPlayerDTO();
            long currentExp = playerDTO.getExp() + exp;
            int oldLevel = playerDTO.getLevel();
            long realExp = exp;

            List<String> awardDescList = new ArrayList<>();
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
                    int newLevel = playerDTO.getLevel() + 1;
                    playerDTO.setLevel(newLevel);
                    JobLevelAwardEnum award = JobLevelAwardEnum.getByJobAndLv(JobEnum.getByCode(playerDTO.getJob()), newLevel);
                    if (Objects.nonNull(award)) {
                        awardDescList.add(award.getSendAward().apply(id));
                    }
                    // 属性提升
                    JobInitPropertiesEnum base = JobInitPropertiesEnum.getByCode(playerDTO.getJob());
                    // 先更新上限，否则当前值加不上去
                    playerDTO.setMaxHp(playerDTO.getMaxHp() + getLevelUpIncrease(base.getInitHp(), newLevel));
                    playerDTO.setMaxMp(playerDTO.getMaxMp() + getLevelUpIncrease(base.getInitMp(), BigDecimal.valueOf(0.03)));
                    playerDTO.setHp(playerDTO.getMaxHp());
                    playerDTO.setMp(playerDTO.getMaxMp());
                    // 四种特殊属性赋空值，下次获取时重新计算
                    playerDTO.clearAdditionVal();

                    playerDTO.setAtk(playerDTO.getAtk() + getLevelUpIncrease(base.getInitAtk(), newLevel));
                    playerDTO.setMagicAtk(playerDTO.getMagicAtk() + getLevelUpIncrease(base.getInitMagicAtk(), newLevel));
                    playerDTO.setDef(playerDTO.getDef() + getLevelUpIncrease(base.getInitDef(), newLevel));
                    playerDTO.setMagicDef(playerDTO.getMagicDef() + getLevelUpIncrease(base.getInitMagicDef(), newLevel));
                    playerDTO.setBehaviorSpeed(playerDTO.getBehaviorSpeed()
                            + getLevelUpIncrease(playerDTO.getBehaviorSpeed(), BigDecimal.valueOf(0.03)));
                    playerDTO.setHit(playerDTO.getHit() + getLevelUpIncrease(base.getInitHit(), newLevel));
                    playerDTO.setDodge(playerDTO.getDodge() + getLevelUpIncrease(base.getInitDodge(), newLevel));
                }
            }
            playerDTO.setExp(currentExp);
            PlayerCache.save(id);
            return new LevelChange(oldLevel, playerDTO.getLevel(), realExp, awardDescList);
        });
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
    private long getLevelUpIncrease(long base, BigDecimal rate) {
        return BigDecimal.valueOf(base).multiply(rate).longValue();
    }

    /**
     * 挂机
     *
     * @param id 玩家ID
     */
    public void hangUp(Long id) {
        PlayerInfoDTO playerInfoDTO = PlayerCache.getPlayerById(id);
        PlayerDTO playerDTO = playerInfoDTO.getPlayerDTO();
        if (PlayerStatusEnum.HANG_UP.getCode().equals(playerDTO.getStatus())) {
            throw ExpExceptionEnum.ALREADY_HANG_UP.getException();
        }
        if (PlayerStatusEnum.EXPLORE.getCode().equals(playerDTO.getStatus())) {
            throw ExpExceptionEnum.EXPLORING.getException();
        }
        playerDTO.setStatus(PlayerStatusEnum.HANG_UP.getCode());
        playerDTO.setStatusStartTime(LocalDateTime.now());
        PlayerCache.save(id);
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
        if (!PlayerStatusEnum.HANG_UP.getCode().equals(playerDTO.getStatus())) {
            throw ExpExceptionEnum.NOT_HANG_UP.getException();
        }
        HangUpVO hangUpVO = new HangUpVO();
        long between = Duration.between(playerDTO.getStatusStartTime(), LocalDateTime.now()).toMinutes();
        playerDTO.setStatus(PlayerStatusEnum.FREE.getCode());
        playerDTO.setStatusStartTime(LocalDateTime.now());
        // 回显真正的挂机时间
        hangUpVO.setHangUpTime(between);
        // 最长有收益挂机时间24h
        between = Math.min(between, Constants.MAX_HANG_UP_TIME);
        if (between == 0) {
            hangUpVO.setExp(0L);
            hangUpVO.setOldLevel(playerDTO.getLevel());
            hangUpVO.setNewLevel(playerDTO.getLevel());
            hangUpVO.setHp(0L);
            hangUpVO.setMp(0L);
        } else {
            // 记录值
            long oldHpWithAddition = playerDTO.getHpWithAddition();
            long oldMpWithAddition = playerDTO.getMpWithAddition();
            // 回复状态
            long oldHp = playerDTO.getHp();
            long maxHp = playerDTO.getMaxHp();
            long oldMp = playerDTO.getMp();
            long maxMp = playerDTO.getMaxMp();
            float recharge = RankEnum.getEnum(playerDTO.getJob(), playerDTO.getRank()).getRechargeRatePerMin();
            long newHp = Math.min(maxHp, oldHp + Math.round(maxHp * recharge * between));
            long newMp = Math.min(maxMp, oldMp + Math.round(maxMp * recharge * between));
            playerDTO.setHp(newHp);
            playerDTO.setMp(newMp);
            playerDTO.clearAdditionVal();
            // 计算经验值
            long exp = (10 + (long) Math.pow(playerDTO.getLevel() - 1, 3) / 30) * between;
            LevelChange levelChange = getExp(id, exp);
            // 回显实际获得的经验值以及状态变化
            hangUpVO.setExp(levelChange.getExp());
            hangUpVO.setOldLevel(levelChange.getOldLevel());
            hangUpVO.setNewLevel(levelChange.getNewLevel());
            hangUpVO.setHp(playerDTO.getHpWithAddition() - oldHpWithAddition);
            hangUpVO.setMp(playerDTO.getMpWithAddition() - oldMpWithAddition);
            if (CollectionUtil.isNotEmpty(levelChange.getAwardDesc())) {
                String msg = levelChange.getAwardDesc().stream().filter(StringUtils::isNotBlank)
                        .reduce((s1, s2) -> s1 + "\n" + s2).orElse(null);
                if (Objects.nonNull(msg)) {
                    hangUpVO.setMessage(msg);
                }
            }
        }
        PlayerCache.save(id);
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
    public static class LevelChange {
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
        /**
         * 奖励说明
         */
        private List<String> awardDesc;
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
