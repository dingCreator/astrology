package com.dingCreator.astrology.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author ding
 * @date 2024/2/5
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillEffectDTO implements Serializable {
    /**
     * 生效条件
     */
    private String skillCondition;
    /**
     * 效果
     */
    private String skillEffect;
    /**
     * 效果值
     */
    private Object val;
}
