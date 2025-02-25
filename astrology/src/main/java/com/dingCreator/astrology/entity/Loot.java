package com.dingCreator.astrology.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 击败怪物后的掉落物
 *
 * @author ding
 * @date 2024/2/19
 */
@Data
@TableName("astrology_loot")
public class Loot {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 从何处掉落
     */
    @TableField("belong_to")
    private String belongTo;
    /**
     * 所属ID
     */
    @TableField("belong_to_id")
    private Long belongToId;
    /**
     * 货币
     */
    @TableField("asset")
    private String asset;
    /**
     * 经验值
     */
    @TableField("exp")
    private Long exp;
    /**
     * 扩展信息
     */
    @TableField("ext_info")
    private String extInfo;

    public static final String BELONG_TO = "belong_to";

    public static final String BELONG_TO_ID = "belong_to_id";
}
