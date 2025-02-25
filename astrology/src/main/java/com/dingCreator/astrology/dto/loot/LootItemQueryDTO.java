package com.dingCreator.astrology.dto.loot;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ding
 * @date 2024/10/28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LootItemQueryDTO {
    /**
     * id
     */
    private Long id;
    /**
     * 所属掉落
     */
    private Long lootId;
    /**
     * 爆率
     */
    private Float rate;
    /**
     * 具体物品json
     */
    private String articleJson;
}
