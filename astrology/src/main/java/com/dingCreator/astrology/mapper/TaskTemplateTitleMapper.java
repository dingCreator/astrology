package com.dingCreator.astrology.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dingCreator.astrology.entity.TaskTemplateTitle;
import com.dingCreator.astrology.request.TaskTemplateTitleQryReq;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author ding
 * @date 2024/8/27
 */
@Mapper
public interface TaskTemplateTitleMapper extends BaseMapper<TaskTemplateTitle> {

    /**
     * 查询
     *
     * @param req 查询请求
     * @return 任务列表
     */
    List<TaskTemplateTitle> listTaskTemplate(@Param("req") TaskTemplateTitleQryReq req);
}
