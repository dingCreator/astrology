package com.dingCreator.astrology.dto.loot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 击败怪物后的掉落物
 *
 * @author ding
 * @date 2024/2/19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LootQueryDTO {
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
    private String asset;
    /**
     * 经验值
     */
    private Long exp;
    /**
     * 扩展信息
     */
    private String extInfo;
    /**
     * 实物奖品列表
     */
    private List<LootItemQueryDTO> itemList;
}
