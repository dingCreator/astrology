package com.dingCreator.astrology.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author ding
 * @date 2024/4/9
 */
@Data
public class DungeonVO implements Serializable {
    /**
     * 名称
     */
    private String dungeonName;
    /**
     * 可挑战此副本的最高Rank
     */
    private String maxRank;
    /**
     * 冷却时间
     */
    private Long flushTime;
    /**
     * 掉落物
     */
    private List<String> loot;
}
