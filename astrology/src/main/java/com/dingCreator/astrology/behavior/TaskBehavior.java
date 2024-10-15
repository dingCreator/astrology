package com.dingCreator.astrology.behavior;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.dingCreator.astrology.cache.TaskCache;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.dto.task.TaskScheduleTitleDTO;
import com.dingCreator.astrology.dto.task.TaskTemplateTitleDTO;
import com.dingCreator.astrology.entity.PeakTaskTemplate;
import com.dingCreator.astrology.entity.TaskTemplateTitle;
import com.dingCreator.astrology.enums.exception.TaskExceptionEnum;
import com.dingCreator.astrology.enums.job.JobEnum;
import com.dingCreator.astrology.enums.task.TaskPropertiesEnum;
import com.dingCreator.astrology.enums.task.TaskTargetTypeEnum;
import com.dingCreator.astrology.response.PageResponse;
import com.dingCreator.astrology.service.MapService;
import com.dingCreator.astrology.service.PeakTaskTemplateService;
import com.dingCreator.astrology.service.TaskTitleService;
import com.dingCreator.astrology.util.PageUtil;
import com.dingCreator.astrology.util.TaskUtil;
import com.dingCreator.astrology.vo.TaskResultVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author ding
 * @date 2024/7/26
 */
public class TaskBehavior {

    static Logger logger = LoggerFactory.getLogger(TaskBehavior.class);

    /**
     * 接取巅峰任务
     *
     * @param playerId 玩家ID
     */
    public void startPeakTask(Long playerId) {
        TaskUtil.receivePeakTask(playerId);
    }

    /**
     * 完成任务
     *
     * @param playerId         玩家ID
     * @param scheduleDetailId 任务进度详情ID
     */
    public TaskResultVO completeTask(Long playerId, Long scheduleDetailId) {
        return TaskUtil.completeTask(playerId, scheduleDetailId);
    }

    /**
     * 查询任务模板列表
     *
     * @param pageIndex 页码
     * @param pageSize  页面大小
     * @return 任务模板
     */
    public PageResponse<TaskTemplateTitleDTO> queryTaskTplTitlePage(int pageIndex, int pageSize) {
        List<TaskTemplateTitleDTO> tplTitleList = TaskCache.listTaskTplTitle();
        return PageUtil.buildPage(tplTitleList, pageIndex, pageSize);
    }

    /**
     * 查询任务模板列表
     *
     * @param playerId  玩家ID
     * @param pageIndex 页码
     * @param pageSize  页面大小
     * @return 任务模板
     */
    public PageResponse<TaskTemplateTitleDTO> queryTaskTplTitlePage(Long playerId, int pageIndex, int pageSize) {
        List<Long> tplTitleIds = TaskCache.listTaskScheduleByPlayerId(playerId).stream()
                .map(TaskScheduleTitleDTO::getTaskTemplateTitleId).collect(Collectors.toList());
        List<TaskTemplateTitleDTO> tplTitleList = TaskCache.listTaskTplTitle().stream()
                .filter(tplTitle -> tplTitleIds.contains(tplTitle.getId()))
                .collect(Collectors.toList());
        return PageUtil.buildPage(tplTitleList, pageIndex, pageSize);
    }

    /**
     * 获取模板
     *
     * @param titleId 标题ID
     * @return 模板
     */
    public TaskTemplateTitleDTO getTaskTplTitle(Long titleId) {
        return TaskCache.getTaskTplById(titleId);
    }

    /**
     * 获取任务进度
     *
     * @param playerId  玩家ID
     * @param pageIndex 页码
     * @param pageSize  页面大小
     * @return 任务进度
     */
    public PageResponse<TaskScheduleTitleDTO> queryTaskScheduleTitlePage(Long playerId, int pageIndex, int pageSize) {
        List<TaskScheduleTitleDTO> scheduleTitleList = TaskCache.listTaskScheduleByPlayerId(playerId);
        return PageUtil.buildPage(scheduleTitleList, pageIndex, pageSize);
    }

    /**
     * 根据编号获取任务详情
     *
     * @param no 编号
     * @return 任务详情
     */
    public TaskScheduleTitleDTO getTaskScheduleTitleByNo(Long playerId, int no) {
        List<TaskScheduleTitleDTO> scheduleTitleList = TaskCache.listTaskScheduleByPlayerId(playerId);
        if (no <= 0 || no > scheduleTitleList.size()) {
            throw TaskExceptionEnum.INVALID_TASK_SCHEDULE_TITLE_NO.getException();
        }
        return scheduleTitleList.get(no - 1);
    }

    public void createTaskTplTitle(Map<String, String> paramMap) {
        String name = paramMap.get(TaskPropertiesEnum.NAME.getField());
        String desc = paramMap.get(TaskPropertiesEnum.DESC.getField());
        String type = paramMap.getOrDefault(TaskPropertiesEnum.TYPE.getField(), TaskTargetTypeEnum.BATTLE.getCode());
        if (Objects.isNull(TaskTargetTypeEnum.getByCode(type))) {
            throw TaskExceptionEnum.TASK_TYPE_ERR.getException();
        }
        String mapIdStr = paramMap.get(TaskPropertiesEnum.MAP.getField());
        if (StringUtils.isNotBlank(mapIdStr)) {
            String[] mapIdStrArr = mapIdStr.replace(Constants.CHN_COMMA, Constants.COMMA).split(Constants.COMMA);
            List<com.dingCreator.astrology.entity.Map> mapList = MapService.listMap();
            List<Long> mapIds = mapList.stream().map(com.dingCreator.astrology.entity.Map::getId).collect(Collectors.toList());
            List<String> inputMapIdStrList = Arrays.stream(mapIdStrArr).distinct().collect(Collectors.toList());

            boolean valid;
            try {
                valid = inputMapIdStrList.stream().allMatch(str -> mapIds.contains(Long.parseLong(str)));
            } catch (Exception e) {
                logger.error("地图id转化异常", e);
                throw TaskExceptionEnum.TASK_MAP_ERR.getException();
            }
            if (!valid) {
                throw TaskExceptionEnum.TASK_MAP_ERR.getException();
            }
        }
        boolean allowRepeatableReceive = Constants.getWhether(paramMap.getOrDefault(
                TaskPropertiesEnum.REPEATABLE.getField(), "Y"));
        boolean allowTeam = Constants.getWhether(paramMap.getOrDefault(TaskPropertiesEnum.TEAM.getField(), "Y"));
        boolean childrenSort = Constants.getWhether(paramMap.getOrDefault(TaskPropertiesEnum.SORT.getField(), "Y"));
        boolean mutualExclusion = Constants.getWhether(paramMap.getOrDefault(
                TaskPropertiesEnum.EXCLUSION.getField(), "Y"));

        TaskTemplateTitle tplTitle = TaskTemplateTitle.builder()
                .name(name)
                .description(desc)
                .taskType(type)
                .limitMapId(mapIdStr)
                .allowRepeatableReceive(allowRepeatableReceive)
                .allowTeam(allowTeam)
                .childrenSort(childrenSort)
                .mutualExclusion(mutualExclusion)
                .build();
        TaskTitleService.constructTaskTitleDTO(tplTitle);
    }

    public void createPeakTask(String jobName, Integer rank, Long taskTplTitleId) {
        JobEnum job = JobEnum.getByName(jobName);
        if (Objects.isNull(job)) {
            throw TaskExceptionEnum.INVALID_JOB_NAME.getException();
        }
        if (rank < Constants.MIN_RANK || rank > Constants.MAX_RANK) {
            throw TaskExceptionEnum.INVALID_RANK.getException();
        }
        TaskTemplateTitleDTO title = TaskCache.getTaskTplById(taskTplTitleId);
        if (Objects.isNull(title)) {
            throw TaskExceptionEnum.INVALID_TASK_TPL_TITLE_ID.getException();
        }
        PeakTaskTemplate peakTaskTemplate = PeakTaskTemplate.builder()
                .job(job.getJobCode())
                .rank(rank)
                .taskTemplateTitleId(taskTplTitleId).build();
        PeakTaskTemplateService.createPeakTaskTemplate(peakTaskTemplate);
    }

    private static class Holder {
        private static final TaskBehavior BEHAVIOR = new TaskBehavior();
    }

    private TaskBehavior() {

    }

    public static TaskBehavior getInstance() {
        return TaskBehavior.Holder.BEHAVIOR;
    }
}
