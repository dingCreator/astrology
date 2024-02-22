package com.dingCreator.astrology.behavior;

import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.dto.PlayerDTO;
import com.dingCreator.astrology.entity.Player;
import com.dingCreator.astrology.enums.JobEnum;
import com.dingCreator.astrology.enums.PlayerStatusEnum;
import com.dingCreator.astrology.enums.exception.PlayerExceptionEnum;
import com.dingCreator.astrology.vo.BaseVO;

import java.util.Collections;
import java.util.Objects;

/**
 * @author ding
 * @date 2024/2/3
 */
public class PlayerBehavior {

    /**
     * 创建角色
     *
     * @param id      玩家ID
     * @param name    玩家名称
     * @param jobName 职业名称
     */
    public void createPlayer(Long id, String name, String jobName) {
        JobEnum job = JobEnum.getByName(jobName);
        if (Objects.isNull(job)) {
            throw PlayerExceptionEnum.JOB_NOT_EXIST.getException();
        }
        Player player = new Player();
        player.setId(id);
        player.setName(name);
        player.setStatus(PlayerStatusEnum.FREE.getCode());
        player.setMapId(1L);
        player.setJob(jobName);
        player.setExp(0L);
        player.setLevel(Constants.MIN_LEVEL);
        player.setRank(Constants.MIN_RANK);
        player.setEnabled(true);

        player.setHp(job.getInitHp());
        player.setMaxHp(job.getInitHp());
        player.setMp(job.getInitMp());
        player.setMaxMp(job.getInitMp());

        player.setAtk(job.getInitAtk());
        player.setDef(job.getInitDef());
        player.setMagicAtk(job.getInitMagicAtk());
        player.setMagicDef(job.getInitMagicDef());

        player.setBehaviorSpeed(job.getInitBehaviorSpeed());
        player.setPenetrate(job.getInitPenetrate());
        player.setCritical(job.getInitCritical());

        player.setHit(job.getInitHit());
        player.setDodge(job.getInitDodge());
        PlayerCache.createPlayer(player);
    }

    /**
     * 根据玩家ID获取玩家信息
     *
     * @param id 玩家ID
     * @return 玩家信息
     */
    public BaseVO<String> getPlayerInfoById(Long id) {
        PlayerDTO playerDTO = PlayerCache.getPlayerById(id);
        String msg = "";
        BaseVO<String> baseVO = new BaseVO<>();
        baseVO.setMsg(Collections.singletonList(msg));
        return baseVO;
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
