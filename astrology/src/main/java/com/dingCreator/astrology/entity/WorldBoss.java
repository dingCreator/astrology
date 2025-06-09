package com.dingCreator.astrology.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author ding
 * @date 2024/2/21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("astrology_world_boss")
public class WorldBoss {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * boss的ID
     */
    @TableField("monster_id")
    private String monsterId;
    /**
     * 开始时间
     */
    @TableField("start_time")
    private LocalDateTime startTime;
    /**
     * 结束时间
     */
    @TableField("end_time")
    private LocalDateTime endTime;
    /**
     * 奖励
     */
    @TableField("award")
    private String award;
    /**
     * 是否已发放奖励
     */
    @TableField("distribute")
    private Boolean distribute;

    public static final String START_TIME = "start_time";

    public static final String END_TIME = "end_time";

    public static final String DISTRIBUTE = "distribute";
}
