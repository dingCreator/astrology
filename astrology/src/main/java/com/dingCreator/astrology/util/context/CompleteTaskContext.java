package com.dingCreator.astrology.util.context;

import com.dingCreator.astrology.dto.task.TaskScheduleDetailDTO;
import com.dingCreator.astrology.dto.task.TaskScheduleTitleDTO;
import com.dingCreator.astrology.dto.task.TaskTemplateDetailDTO;
import com.dingCreator.astrology.dto.task.TaskTemplateTitleDTO;
import com.dingCreator.astrology.vo.TaskResultVO;
import lombok.Data;

/**
 * @author ding
 * @date 2024/9/27
 */
@Data
public class CompleteTaskContext {
    /**
     * 总进度
     */
    private TaskScheduleTitleDTO scheduleTitleDTO;
    /**
     * 进度详情
     */
    private TaskScheduleDetailDTO scheduleDetail;
    /**
     * 模板标题
     */
    private TaskTemplateTitleDTO tplTitle;
    /**
     * 模板详情
     */
    private TaskTemplateDetailDTO tplDetail;
    /**
     * 任务结果
     */
    private TaskResultVO taskResultVO;
}
