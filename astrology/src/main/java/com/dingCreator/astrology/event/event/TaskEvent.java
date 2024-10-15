package com.dingCreator.astrology.event.event;

import com.dingCreator.astrology.dto.task.TaskScheduleDetailDTO;
import com.dingCreator.astrology.dto.task.TaskScheduleTitleDTO;
import com.dingCreator.astrology.dto.task.TaskTemplateTitleDTO;
import com.dingCreator.astrology.enums.task.TaskScheduleEnum;
import lombok.*;

/**
 * @author ding
 * @date 2024/10/5
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskEvent extends BaseEvent {

    /**
     * 任务模板
     */
    private TaskTemplateTitleDTO taskTplTitle;

    /**
     * 发生变动的任务进度级别
     */
    private TaskScheduleLevel taskScheduleLevel;

    /**
     * 任务进度
     */
    private TaskScheduleDetailDTO taskScheduleDetail;

    /**
     * 旧状态
     */
    private TaskScheduleEnum oldSchedule;

    /**
     * 新状态
     */
    private TaskScheduleEnum newSchedule;

    @Getter
    @AllArgsConstructor
    public enum TaskScheduleLevel {
        /**
         * 任务项
         */
        DETAIL,
        /**
         * 子任务
         */
        SCHEDULE,
        /**
         * 总任务
         */
        TITLE,
        ;
    }
}
