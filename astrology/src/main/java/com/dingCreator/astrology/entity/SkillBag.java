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
 * @author ding
 * @date 2025/2/27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("astrology_skill_bag")
public class SkillBag {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 玩家ID
     */
    @TableField("player_id")
    private Long playerId;
    /**
     * 技能ID
     */
    @TableField("skill_id")
    private Long skillId;
    /**
     * 数量
     */
    @TableField("cnt")
    private Integer cnt;
}
