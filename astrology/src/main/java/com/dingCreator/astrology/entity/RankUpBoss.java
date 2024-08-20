package com.dingCreator.astrology.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 突破boss
 *
 * @author ding
 * @date 2024/2/2
 */
@Data
@TableName("astrology_rank_up_boss")
public class RankUpBoss {
    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;
    /**
     * 职业
     */
    @TableField("job")
    private String job;
    /**
     * 突破前阶级
     */
    @TableField("rank")
    private Integer rank;
    /**
     * boss的ID
     */
    @TableField("monster_id")
    private Long monsterId;
}
