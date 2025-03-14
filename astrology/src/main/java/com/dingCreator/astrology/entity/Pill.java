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
     * 丹药类型
     */
    @TableField("pill_type")
    private String PillType;
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
    @TableField("quality_start")
    private Integer qualityStart;
    /**
     * 品质
     */
    @TableField("quality_end")
    private Integer qualityEnd;
    /**
     * 星辰之力
     */
    @TableField("star_start")
    private Integer starStart;
    /**
     * 星辰之力
     */
    @TableField("star_end")
    private Integer starEnd;
    /**
     * 效果
     */
    @TableField("effect_json")
    private String effectJson;

    public static final String ID = "id";

    public static final String PILL_NAME = "pill_name";

    public static final String LEVEL = "level";

    public static final String VIGOR = "vigor";

    public static final String WARN = "warn";

    public static final String COLD = "cold";

    public static final String TOXICITY = "toxicity";

    public static final String QUALITY_START = "quality_start";

    public static final String QUALITY_END = "quality_end";

    public static final String STAR_START = "star_start";

    public static final String STAR_END = "star_end";

    public static final String EFFECT_JSON = "effect_json";
}
