package com.dingCreator.immortal.entity;

import com.dingCreator.immortal.entity.base.Boss;
import com.dingCreator.immortal.entity.base.Monster;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 小Boss
 * @author ding
 * @date 2024/2/1
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DungeonBoss extends Boss {
    /**
     * 主键
     */
    private Long id;
    /**
     * 刷新时间，单位：秒
     */
    private Integer flushTime;
}
