package com.dingCreator.astrology.dto.task;

import com.dingCreator.astrology.entity.TaskTemplateDetail;
import com.dingCreator.astrology.enums.task.TaskTargetTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ding
 * @date 2024/8/26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDetailDTO {
    /**
     * 目标类型
     */
    private TaskTargetTypeEnum target;
    /**
     * 目标ID
     */
    private Long targetId;
    /**
     * 数量
     */
    private Integer targetCnt;

    /**
     * 转化为dto
     *
     * @param detail 实体
     * @return dto
     */
    public static TaskDetailDTO convert(TaskTemplateDetail detail) {
        return TaskDetailDTO.builder()
                .target(TaskTargetTypeEnum.getByCode(detail.getTargetType()))
                .targetCnt(detail.getTargetCnt())
                .targetId(detail.getTargetId()).build();
    }
}
