package com.dingCreator.yuanshen.behavior;

import com.dingCreator.yuanshen.cache.PlayerCache;
import com.dingCreator.yuanshen.constants.Constants;
import com.dingCreator.yuanshen.entity.Player;
import com.dingCreator.yuanshen.enums.JobEnum;
import com.dingCreator.yuanshen.enums.PlayerStatusEnum;
import com.dingCreator.yuanshen.enums.exception.PlayerExceptionEnum;

import java.util.Objects;

/**
 * @author ding
 * @date 2024/2/3
 */
public class PlayerBehavior {

    private PlayerCache playerCache;

    /**
     * 创建角色
     *
     * @param id 玩家ID
     * @param name 玩家名称
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
        playerCache.createPlayer(player);
    }
}
