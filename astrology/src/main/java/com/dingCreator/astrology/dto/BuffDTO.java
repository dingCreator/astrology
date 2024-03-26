package com.dingCreator.astrology.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author ding
 * @date 2024/2/2
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BuffDTO implements Serializable {
    /**
     * buff类型
     */
    private String buffType;
    /**
     * 数值变化
     */
    private Long value;
    /**
     * 比例变化
     */
    private Float rate;
}
