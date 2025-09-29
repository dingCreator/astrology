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
    @TableField("equipment_level")
    private Integer equipmentLevel;
    /**
     * 数量
     */
    @TableField("total_cnt")
    private Integer totalCnt;
    /**
     * 是否装备中
     */
    @TableField("equip")
    private Boolean equip;
//    /**
//     * 是否绑定
//     */
//    @TableField("bind")
//    private Boolean bind;


    public static final String BELONG_TO = "belong_to";

    public static final String BELONG_TO_ID = "belong_to_id";

    public static final String EQUIPMENT_ID = "equipment_id";

    public static final String EQUIPMENT_LEVEL = "equipment_level";

    public static final String TOTAL_CNT = "total_cnt";

    public static final String EQUIP = "equip";

    public static final String BIND = "bind";
}
