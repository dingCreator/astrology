package com.dingCreator.astrology.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author ding
 * @date 2024/10/28
 */
@TableName("astrology_loot_item")
@Data
public class LootItem {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 所属掉落
     */
    @TableField(value = "loot_id")
    private Long lootId;
    /**
     * 爆率
     */
    @TableField("rate")
    private Float rate;
    /**
     * 具体物品json
     */
    @TableField(value = "article_json")
    private String articleJson;

    public static final String LOOT_ID = "loot_id";
}
