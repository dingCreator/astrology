package com.dingCreator.astrology.entity;

import lombok.Data;

/**
 * 击败怪物后的掉落物
 *
 * @author ding
 * @date 2024/2/19
 */
@Data
public class MonsterLoot {
    /**
     * 主键ID
     */
    private Long id;
    /**
     * 所属怪物类型
     */
    private String belongTo;
    /**
     * 所属怪物ID
     */
    private Long belongToId;
    /**
     * 爆率
     */
    private Float rate;
    /**
     * 掉落物类型
     */
    private String type;
    /**
     * 掉落物ID
     */
    private Long lootId;
}
