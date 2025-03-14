package com.dingCreator.astrology.vo;

import com.dingCreator.astrology.dto.alchemy.PillDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ding
 * @date 2025/3/11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlchemyResultVO {
    /**
     * 是否成功
     */
    private Boolean success;
    /**
     * 炼制信息
     */
    private String alchemyMsg;
}
