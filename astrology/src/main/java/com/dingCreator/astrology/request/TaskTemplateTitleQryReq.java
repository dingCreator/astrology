package com.dingCreator.astrology.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author ding
 * @date 2024/9/24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskTemplateTitleQryReq {
    /**
     * 标题ID
     */
    private Long titleId;
    /**
     * 详情ID
     */
    private List<Long> detailIds;
}
