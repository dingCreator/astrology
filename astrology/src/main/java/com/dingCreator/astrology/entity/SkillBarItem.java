package com.dingCreator.astrology.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 技能栏
 *
 * @author ding
 * @date 2024/2/3
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillBarItem {
    /**
     * 主键
     */
    private String id;
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
    private String headId;
    /**
     * 技能组下一个技能
     */
    private String nextId;
}
