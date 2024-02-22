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
    public synchronized void getExp(Long id, Long exp) {
        Player player = PlayerCache.getPlayerById(id).getPlayer();
        long currentExp = player.getExp() + exp;

        while (currentExp >= getCurrentLevelMaxExp(player.getLevel())) {
            RankEnum rankEnum = RankEnum.getEnum(player.getJob(), player.getRank());
            if (player.getLevel() >= Constants.MAX_LEVEL) {
                currentExp = getCurrentLevelMaxExp(Constants.MAX_LEVEL);
            } else if (player.getLevel() >= rankEnum.getMaxLevel()) {
                // 上限了，不突破不允许升级
                long expLimit = getExpLimit(player.getLevel());
                if (currentExp >= expLimit) {
                    currentExp = expLimit;
                }
                break;
            } else {
                currentExp -= getCurrentLevelMaxExp(player.getLevel());
                player.setLevel(player.getLevel() + 1);
                // todo 属性提升
            }
        }
        player.setExp(currentExp);
        PlayerCache.flush(Collections.singletonList(id));
    }

    /**
     * 获取当前等级最大经验值
     *
     * @param level 等级
     * @return 最大经验值
     */
    private long getCurrentLevelMaxExp(int level) {
        return level * 100;
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
        if (!PlayerStatusEnum.FREE.getCode().equals(player.getStatus())) {
            throw ExpExceptionEnum.CANT_HANG_UP.getException();
        }
        player.setStatus(PlayerStatusEnum.HANG_UP.getCode());
        player.setHangUpTime(new Date());
        PlayerCache.flush(Collections.singletonList(id));
    }

    /**
     * 停止挂机
     *
     * @param id 玩家ID
     */
    public long stopHangUp(Long id) {
        PlayerDTO playerDTO = PlayerCache.getPlayerById(id);
        Player player = playerDTO.getPlayer();
        if (!PlayerStatusEnum.HANG_UP.getCode().equals(player.getStatus())) {
            throw ExpExceptionEnum.NOT_HANG_UP.getException();
        }
        player.setStatus(PlayerStatusEnum.FREE.getCode());
        PlayerCache.flush(Collections.singletonList(id));

        long between = DateUtil.between(player.getHangUpTime(), new Date(), DateUnit.SECOND);
        // todo EXP计算
        long exp = between;
        getExp(id, exp);
        return between;
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
