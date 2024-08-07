package com.dingCreator.astrology.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author ding
 * @date 2024/4/29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamVO implements Serializable {
    /**
     * 队伍ID
     */
    private Long teamId;
    /**
     * 队长昵称
     */
    private String captainNick;
    /**
     * 队伍成员昵称
     */
    private List<String> membersNick;
}
