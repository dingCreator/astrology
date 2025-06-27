package com.dingCreator.astrology.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 技能栏
 *
 * @author ding
 * @date 2024/2/3
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("astrology_skill_bar_item")
public class SkillBarItem {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * Player/DungeonBoss/RankUpBoss/AreaBoss
     */
    @TableField("belong_to")
    private String belongTo;
    /**
     * 对应的ID
     */
    @TableField("belong_to_id")
    private Long belongToId;
    /**
     * 技能ID，用逗号区隔
     */
    @TableField("skill_id")
    private String skillId;
    /**
     * 是否正在使用此配置
     */
    @TableField("using")
    private Boolean using;
}
