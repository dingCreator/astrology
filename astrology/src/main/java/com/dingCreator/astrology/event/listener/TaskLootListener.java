package com.dingCreator.astrology.event.listener;

import com.dingCreator.astrology.cache.TaskCache;
import com.dingCreator.astrology.dto.task.TaskScheduleDetailDTO;
import com.dingCreator.astrology.dto.task.TaskScheduleTitleDTO;
import com.dingCreator.astrology.dto.task.TaskTemplateDTO;
import com.dingCreator.astrology.dto.task.TaskTemplateTitleDTO;
import com.dingCreator.astrology.entity.TaskSchedule;
import com.dingCreator.astrology.entity.TaskScheduleDetail;
import com.dingCreator.astrology.entity.TaskTemplate;
import com.dingCreator.astrology.enums.task.TaskScheduleEnum;
import com.dingCreator.astrology.event.event.TaskEvent;
import com.dingCreator.astrology.util.LootUtil;

import java.util.Objects;

/**
 * @author ding
 * @date 2024/10/5
 */
public class TaskLootListener implements EventListener<TaskEvent> {

    @Override
    public void listen(TaskEvent event) {
        if (Objects.equals(event.getOldSchedule(), event.getNewSchedule())) {
            return;
        }
        if (!TaskScheduleEnum.COMPLETE.equals(event.getNewSchedule())) {
            return;
        }

        TaskScheduleDetailDTO scheduleDetail = event.getTaskScheduleDetail();
        TaskTemplateTitleDTO tplTitle = event.getTaskTplTitle();
        if (TaskEvent.TaskScheduleLevel.SCHEDULE.equals(event.getTaskScheduleLevel())) {
            TaskTemplateDTO tpl = tplTitle.getTemplateList().stream()
                    .filter(t -> scheduleDetail.getTaskTemplateId().equals(t.getId()))
                    .findFirst().orElse(null);
            if (Objects.nonNull(tpl)) {
                LootUtil.sendLoot(tpl.getLootDTO(), scheduleDetail.getPlayerId());
            }
        } else if (TaskEvent.TaskScheduleLevel.TITLE.equals(event.getTaskScheduleLevel())) {
            LootUtil.sendLoot(tplTitle.getLootDTO(), scheduleDetail.getPlayerId());
        }
    }
}
