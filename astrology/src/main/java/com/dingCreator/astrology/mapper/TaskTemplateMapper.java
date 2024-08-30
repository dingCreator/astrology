package com.dingCreator.astrology.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dingCreator.astrology.entity.TaskTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author ding
 * @date 2024/8/27
 */
@Mapper
public interface TaskTemplateMapper extends BaseMapper<TaskTemplate> {
    /**
     * 根据模板id获取任务及子任务
     *
     * @param templateId 模板Id
     * @return 任务列表
     */
    List<TaskTemplate> listTaskTemplateById(@Param("templateId") Long templateId);
}
