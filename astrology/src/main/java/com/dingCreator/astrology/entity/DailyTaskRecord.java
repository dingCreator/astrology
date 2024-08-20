package com.dingCreator.astrology.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 每日任务
 *
 * @author ding
 * @date 2024/2/3
 */
@Data
@TableName("astrology_daily_task_record")
public class DailyTaskRecord {
    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 玩家ID
     */
    @TableField("player_id")
    private Long playerId;
    /**
     * 周期月份
     */
    @TableField("period_month")
    private String periodMonth;
    /**
     * 完成情况统计
     * 以,分隔三个数字
     * 三个数字采用bitmap储存，每个玩家一个月一条数据
     */
    @TableField("complete_statics")
    private String completeStatics;
}
