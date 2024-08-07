package com.dingCreator.astrology.dto.player;

import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.entity.base.Organism;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Objects;

/**
 * @author ding
 * @date 2024/8/2
 */
@Data
@Builder
@NoArgsConstructor
public class PlayerDTO extends Organism {
    /**
     * 玩家ID
     */
    private Long id;
    /**
     * 经验值
     */
    private Long exp;
    /**
     * 职业
     */
    private String job;
    /**
     * 所处位置
     */
    private Long mapId;
    /**
     * 状态
     */
    private String status;
    /**
     * 进入此状态时间
     */
    private Date statusStartTime;
    /**
     * 是否可用
     */
    private Boolean enabled;
    /**
     * 计算加成后的血量
     */
    private Long hpWithAddition;
    /**
     * 计算加成后的血量上限
     */
    private Long maxHpWithAddition;
    /**
     * 计算加成后的蓝
     */
    private Long mpWithAddition;
    /**
     * 计算加成后的蓝上限
     */
    private Long maxMpWithAddition;

    /*
        血量和蓝比较特殊，因为存在当前值/最大值，与攻击防御等属性不同，计算起来较为复杂
        所以另外引入新的字段标识装备或其他加成
        withAddition后缀的为实际值，不带后缀的为基础值
        当maxWithAddition属性发生变动或初始化时，计算一次withAddition值
        例如 hpWithAddition = (hp/maxHp) * maxHpWithAddition
        将血量与蓝量的get set方法重写，以兼容多态
        另外增加方法来修改基础数值
     */


    /**
     * 设置当前血量
     *
     * @param hp 当前血量
     */
    @Override
    public void setHp(Long hp) {
        this.hpWithAddition = hp;
        if (hp < 0) {
            this.hpWithAddition = 0L;
        } else if (this.hpWithAddition > this.maxHpWithAddition) {
            this.hpWithAddition = this.maxHpWithAddition;
        }
        super.setHp(super.getMaxHp() * (this.hpWithAddition / this.maxHpWithAddition));
    }

    /**
     * 设置最大血量
     *
     * @param maxHp 当前血量
     */
    @Override
    public void setMaxHp(Long maxHp) {
        this.setMaxHpWithAddition(maxHp);
    }

    /**
     * 设置当前蓝
     *
     * @param mp 当前蓝
     */
    @Override
    public void setMp(Long mp) {
        this.mpWithAddition = mp;
        if (mp < 0) {
            this.mpWithAddition = 0L;
        } else if (this.mpWithAddition > this.maxMpWithAddition) {
            this.mpWithAddition = this.maxMpWithAddition;
        }
        super.setMp(super.getMaxMp() * (this.mpWithAddition / this.maxMpWithAddition));
    }

    /**
     * 设置当前蓝
     *
     * @param maxMp 当前蓝
     */
    @Override
    public void setMaxMp(Long maxMp) {
        this.setMaxHpWithAddition(maxMp);
    }

    /**
     * 获取当前血量
     *
     * @return 当前血量
     */
    @Override
    public Long getHp() {
        return this.getHpWithAddition();
    }

    /**
     * 获取最大血量
     *
     * @return 当前血量
     */
    @Override
    public Long getMaxHp() {
        return this.getMaxHpWithAddition();
    }

    /**
     * 获取当前蓝
     *
     * @return 当前蓝
     */
    @Override
    public Long getMp() {
        return this.getMpWithAddition();
    }

    /**
     * 获取最大蓝
     *
     * @return 当前蓝
     */
    @Override
    public Long getMaxMp() {
        return this.getMaxMpWithAddition();
    }

    public Long getHpWithAddition() {
        if (Objects.isNull(this.hpWithAddition)) {
            initPropWithAddition();
        }
        return hpWithAddition;
    }

    public Long getMaxHpWithAddition() {
        if (Objects.isNull(this.maxHpWithAddition)) {
            initPropWithAddition();
        }
        return maxHpWithAddition;
    }

    public Long getMpWithAddition() {
        if (Objects.isNull(this.mpWithAddition)) {
            initPropWithAddition();
        }
        return mpWithAddition;
    }

    public Long getMaxMpWithAddition() {
        if (Objects.isNull(this.maxMpWithAddition)) {
            initPropWithAddition();
        }
        return maxMpWithAddition;
    }

    private synchronized void initPropWithAddition() {
        PlayerCache.
    }
}
