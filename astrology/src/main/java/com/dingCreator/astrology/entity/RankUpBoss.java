package com.dingCreator.astrology.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 突破boss
 *
 * @author ding
 * @date 2024/2/2
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("astrology_rank_up_boss")
public class RankUpBoss {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 职业
     */
    @TableField("job")
    private String job;
    /**
     * 突破前阶级
     */
    @TableField("`rank`")
    private Integer rank;
    /**
     * boss的ID
     */
    @TableField("monster_id")
    private Long monsterId;

    public static final String ID = "id";
    public static final String JOB = "job";
    public static final String RANK = "`rank`";
    public static final String MONSTER_ID = "monster_id";
}
