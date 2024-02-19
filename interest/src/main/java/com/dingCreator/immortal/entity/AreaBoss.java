package com.dingCreator.immortal.entity;

import com.dingCreator.immortal.entity.base.Boss;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 区域Boss
 *
 * @author ding
 * @date 2024/2/2
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AreaBoss extends Boss {
    /**
     * 主键
     */
    private Long id;
}
