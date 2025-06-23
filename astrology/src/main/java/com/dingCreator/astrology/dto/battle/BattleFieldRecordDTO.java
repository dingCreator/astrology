package com.dingCreator.astrology.dto.battle;

import com.dingCreator.astrology.enums.FieldEffectEnum;
import com.dingCreator.astrology.util.template.ExtraBattleProcessTemplate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author ding
 * @date 2025/4/21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BattleFieldRecordDTO {
    /**
     * 场地效果
     */
    private FieldEffectEnum fieldEffectEnum;
    /**
     * 轮次
     */
    private Integer round;
    /**
     * 最大轮次
     */
    private Integer maxRound;
    /**
     * 行动值总计
     */
    private Long totalBehavior;
}
