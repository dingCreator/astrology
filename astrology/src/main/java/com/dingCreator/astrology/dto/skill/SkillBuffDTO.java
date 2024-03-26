package com.dingCreator.astrology.dto.skill;

import com.dingCreator.astrology.dto.BuffDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 技能附加buff
 *
 * @author ding
 * @date 2024/2/26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillBuffDTO implements Serializable {
    /**
     * buff
     */
    private BuffDTO buffDTO;
    /**
     * 生效概率
     */
    private Float effectRate;
    /**
     * 持续回合数
     */
    private Integer round;
}