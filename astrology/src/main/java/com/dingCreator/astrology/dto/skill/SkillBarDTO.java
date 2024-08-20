package com.dingCreator.astrology.dto.skill;

import com.dingCreator.astrology.enums.skill.SkillEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

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

    /**
     * 获取技能循环描述
     *
     * @return 技能循环
     */
    public String chainToString() {
        SkillBarDTO skillBarDTO = this.head;
        if (Objects.isNull(skillBarDTO)) {
            return "";
        }
        StringBuilder builder = new StringBuilder(SkillEnum.getById(skillBarDTO.getSkillId()).getName());
        while (Objects.nonNull(skillBarDTO.getNext())) {
            skillBarDTO = skillBarDTO.getNext();
            builder.append(" >> ").append(SkillEnum.getById(skillBarDTO.getSkillId()).getName());
        }
        return builder.toString();
    }
}
