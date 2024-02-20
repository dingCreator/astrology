package com.dingCreator.astrology.entity;

import lombok.Data;

/**
 * 拥有的技能
 *
 * @author ding
 * @date 2023/4/18
 */
@Data
public class SkillBelongTo {
    /**
     * 主键
     */
    private Long id;
    /**
     * Player/DungeonBoss/RankUpBoss/AreaBoss
     */
    private String belongTo;
    /**
     * 对应的ID
     */
    private Long belongToId;
    /**
     * 技能ID
     */
    private Long skillId;
}
