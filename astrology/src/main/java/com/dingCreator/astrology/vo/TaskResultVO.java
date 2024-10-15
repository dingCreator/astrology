package com.dingCreator.astrology.vo;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ding
 * @date 2024/10/15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResultVO {
    /**
     * 是否成功
     */
    private Boolean success;
    /**
     * 提示信息
     */
    private String msg;

    public String getMsg() {
        if (StringUtils.isBlank(msg)) {
            return success ? "任务成功" : "任务失败";
        }
        return this.msg;
    }
}
