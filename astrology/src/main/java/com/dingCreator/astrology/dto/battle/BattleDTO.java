package com.dingCreator.astrology.dto.battle;

import com.dingCreator.astrology.dto.organism.OrganismInfoDTO;
import com.dingCreator.astrology.enums.EffectTypeEnum;
import com.dingCreator.astrology.util.BuffUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 战斗包装类，用于存储一些中间过程
 * 血量/蓝量这一类要持久化的则直接修改生物属性内的值
 *
 * @author ding
 * @date 2024/2/2
 */
@Data
public class BattleDTO implements Serializable {
    /**
     * 生物属性
     */
    private OrganismInfoDTO organismInfoDTO;
    /**
     * 行动值
     */
    private Long behavior;
    /**
     * 获得的buff
     */
    private Map<EffectTypeEnum, List<BattleBuffDTO>> buffMap;
    /**
     * 标记
     */
    private Map<String, Integer> markMap;
    /**
     * 附加法则
     */
    private List<RuleDTO> ruleList;
    /**
     * 轮次
     */
    private Long round;
    /**
     * 伤害量
     */
    private Long damage;
    /**
     * 是否为召唤物
     */
    private Boolean summoned;

    public void giveBuff(BattleDTO target, List<GiveBuffDTO> giveBuffList, StringBuilder builder)  {
        BuffUtil.addBuff(this, target, giveBuffList, builder);
    }
}
