package com.dingCreator.astrology.entity.base;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * @author ding
 * @date 2024/2/1
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("astrology_monster")
public class Monster extends Organism {
    /**
     * ID
     */
    @TableId("id")
    protected Long id;
    /**
     * 怪物描述
     */
    @TableField("description")
    protected String description;
}
