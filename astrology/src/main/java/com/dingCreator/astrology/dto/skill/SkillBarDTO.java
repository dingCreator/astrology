package com.dingCreator.astrology.dto.skill;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ding
 * @date 2024/2/3
 */
@Data
public class SkillBarDTO implements Serializable {
    /**
     * 技能ID
     */
    private Long skillId;
    /**
     * 头
     */
    private SkillBarDTO head;
    /**
     * 技能组下一个技能
     */
    private SkillBarDTO next;
}
