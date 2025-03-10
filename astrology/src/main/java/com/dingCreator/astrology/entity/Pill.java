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
@TableName("astrology_pill")
public class Pill {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 丹药名称
     */
    @TableField("pill_name")
    private String pillName;
    /**
     * 丹药等级
     */
    @TableField("level")
    private Integer level;
    /**
     * 生机
     */
    @TableField("vigor")
    private Integer vigor;
    /**
     * 温热
     */
    @TableField("warn")
    private Integer warn;
    /**
     * 寒韵
     */
    @TableField("cold")
    private Integer cold;
    /**
     * 毒性
     */
    @TableField("toxicity")
    private Integer toxicity;
    /**
     * 品质
     */
    @TableField("quality")
    private Integer quality;
    /**
     * 星辰之力
     */
    @TableField("star")
    private Integer star;
    /**
     * 效果
     */
    @TableField("effect_json")
    private String effectJson;
}
