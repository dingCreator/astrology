package com.dingCreator.astrology.entity;

import lombok.Data;

import java.util.List;

/**
 * 击败怪物后的掉落物
 *
 * @author ding
 * @date 2024/2/19
 */
@Data
public class Loot {
    /**
     * 主键ID
     */
    private Long id;
    /**
     * 从何处掉落
     */
    private String belongTo;
    /**
     * 所属ID
     */
    private Long belongToId;
    /**
     * 货币
     */
    private Long money;
    /**
     * 经验值
     */
    private Long exp;
    /**
     * 掉落物集合json
     */
    private String lootItemList;
}
