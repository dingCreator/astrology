package com.dingCreator.astrology.dto.alchemy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ding
 * @date 2025/3/12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerPillListDTO {

    private String pillName;

    private Integer pillCnt;
}
