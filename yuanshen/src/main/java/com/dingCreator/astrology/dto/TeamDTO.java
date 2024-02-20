package com.dingCreator.yuanshen.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author ding
 * @date 2024/2/1
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamDTO implements Serializable {
    /**
     * 队长ID
     */
    private Long captainId;
    /**
     * 成员ID
     */
    private List<Long> members;
}
