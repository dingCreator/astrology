package com.dingCreator.astrology.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 拥有的技能
 *
 * @author ding
 * @date 2023/4/18
 */
@Data
@TableName("astrology_skill_belong_to")
public class SkillBelongTo {
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
     * 技能ID
     */
    @TableField("skill_id")
    private Long skillId;
}
