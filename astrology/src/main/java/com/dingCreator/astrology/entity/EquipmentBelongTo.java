package com.dingCreator.astrology.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 装备
 *
 * @author ding
 * @date 2024/1/29
 */
@Data
@TableName("astrology_equipment_belong_to")
public class EquipmentBelongTo {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 装备归属
     */
    @TableField("belong_to")
    private String belongTo;
    /**
     * 装备所属ID
     */
    @TableField("belong_to_id")
    private Long belongToId;
    /**
     * 装备ID
     */
    @TableField("equipment_id")
    private Long equipmentId;
    /**
     * 强化等级
     */
    @TableField("level")
    private Integer level;
    /**
     * 是否装备中
     */
    @TableField("equip")
    private Boolean equip;
}
