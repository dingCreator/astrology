package com.dingCreator.astrology.entity.base;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author ding
 * @date 2024/2/1
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class Monster extends Organism {
    /**
     * ID
     */
    private Long id;
}
