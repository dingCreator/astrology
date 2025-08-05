package com.dingCreator.astrology.dto.organism;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author ding
 * @date 2024/8/17
 */
@Data
public class OrganismDTO implements Serializable {
    /**
     * 名称
     */
    protected String name;
    /**
     * 血量
     */
    protected volatile Long hp;
    /**
     * 血量上限
     */
    protected volatile Long maxHp;
    /**
     * 蓝
     */
    protected volatile Long mp;
    /**
     * 蓝上限
     */
    protected volatile Long maxMp;
    /**
     * 攻击力
     */
    protected Long atk;
    /**
     * 法强
     */
    protected Long magicAtk;
    /**
     * 防御
     */
    protected Long def;
    /**
     * 法抗
     */
    protected Long magicDef;
    /**
     * 穿透
     */
    protected Float penetrate;
    /**
     * 法穿
     */
    protected Float magicPenetrate;
    /**
     * 暴击率
     */
    protected Float criticalRate;
    /**
     * 抗暴率
     */
    protected Float criticalReductionRate;
    /**
     * 爆伤倍率
     */
    protected Float criticalDamage;
    /**
     * 爆伤减免
     */
    protected Float criticalDamageReduction;
    /**
     * 行动速度
     */
    protected Long behaviorSpeed;
    /**
     * 命中率
     */
    protected Long hit;
    /**
     * 闪避率
     */
    protected Long dodge;
    /**
     * 吸血
     */
    protected Float lifeStealing;
    /**
     * 阶段
     */
    protected Integer rank;
    /**
     * 等级
     */
    protected Integer level;

    // 特殊属性
    /**
     * 计算加成后的血量
     */
    protected Long hpWithAddition;
    /**
     * 计算加成后的血量上限
     */
    protected Long maxHpWithAddition;
    /**
     * 计算加成后的蓝
     */
    protected Long mpWithAddition;
    /**
     * 计算加成后的蓝上限
     */
    protected Long maxMpWithAddition;

    /*
        血量和蓝比较特殊，因为存在当前值/最大值，与攻击防御等属性不同，计算起来较为复杂
        所以另外引入新的字段标识装备或其他加成
        withAddition后缀的为实际值，不带后缀的为基础值
        当maxWithAddition属性发生变动或初始化时，计算一次withAddition值
        例如 hpWithAddition = (hp/maxHp) * maxHpWithAddition
     */

    public void setHp(Long hp) {
        this.hp = hp;
        if (hp < 0) {
            this.hp = 0L;
        } else if (this.hp > this.maxHp) {
            this.hp = this.maxHp;
        }
        this.hpWithAddition = null;
    }

    public void setMaxHp(Long maxHp) {
        this.maxHp = maxHp;
        this.maxHpWithAddition = null;
    }

    public void setHpWithAddition(Long hp) {
        this.hpWithAddition = hp;
        if (hp < 0) {
            this.hpWithAddition = 0L;
        } else if (this.hpWithAddition > this.maxHpWithAddition) {
            this.hpWithAddition = this.maxHpWithAddition;
        }
        this.hp = Math.round(this.maxHp * (double) this.hpWithAddition / (double) this.maxHpWithAddition);
    }

    public void setMp(Long mp) {
        this.mp = mp;
        if (mp < 0) {
            this.mp = 0L;
        } else if (this.mp > this.maxMp) {
            this.mp = this.maxMp;
        }
        this.mpWithAddition = null;
    }

    public void setMaxMp(Long maxMp) {
        this.maxMp = maxMp;
        this.maxMpWithAddition = null;
    }

    public void setMpWithAddition(Long mp) {
        this.mpWithAddition = mp;
        if (mp < 0) {
            this.mpWithAddition = 0L;
        } else if (this.mpWithAddition > this.maxMpWithAddition) {
            this.mpWithAddition = this.maxMpWithAddition;
        }
        this.mp = Math.round(this.maxMp * (double) this.mpWithAddition / (double) this.maxMpWithAddition);
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

    /**
     * 初始化计算加成后数值
     */
    protected synchronized void initPropWithAddition() {
        this.maxHpWithAddition = this.maxHp;
        this.hpWithAddition = this.hp;
        this.maxMpWithAddition = this.maxMp;
        this.mpWithAddition = this.mp;
    }

    /**
     * 清除加成后数值
     */
    public void clearAdditionVal() {
        this.hpWithAddition = null;
        this.maxHpWithAddition = null;
        this.mpWithAddition = null;
        this.maxMpWithAddition = null;
    }
}
