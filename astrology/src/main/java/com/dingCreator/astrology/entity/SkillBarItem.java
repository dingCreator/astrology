package com.dingCreator.astrology.entity;

import lombok.Data;

/**
 * 技能栏
 *
 * @author ding
 * @date 2024/2/3
 */
@Data
public class SkillBarItem {
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
    /**
     * 头
     */
    private Long headId;
    /**
     * 技能组下一个技能
     */
    private Long nextId;
}
