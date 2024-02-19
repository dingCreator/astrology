package com.dingCreator.immortal.cache;

import com.dingCreator.immortal.dto.TeamDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 组队缓存
 *
 * @author ding
 * @date 2024/2/1
 */
public class TeamCache {

    private static final Map<Long, TeamDTO> TEAM = new ConcurrentHashMap<>();

    /**
     * 根据小队ID查询小队
     *
     * @param teamId 小队ID
     * @return 小队
     */
    public TeamDTO getTeamById(Long teamId) {
        return TEAM.get(teamId);
    }

    /**
     * 根据玩家ID查询小队
     *
     * @param playerId 玩家ID
     * @return 小队
     */
    public TeamDTO getTeamByPlayerId(Long playerId) {
        return TEAM.values().stream().filter(t -> t.getMembers().contains(playerId)).findFirst().orElse(null);
    }

    /**
     * 创建小队
     *
     * @param playerId 创建者ID
     */
    public void createTeam(Long playerId) {
        List<Long> members = new ArrayList<>(3);
        members.add(playerId);
        createTeam(playerId, new TeamDTO(playerId, members));
    }

    /**
     * 创建小队
     *
     * @param playerId 创建者ID
     */
    public void createTeam(Long playerId, TeamDTO teamDTO) {
        TEAM.put(playerId, teamDTO);
    }

    /**
     * 删除小队
     *
     * @param teamId 小队ID
     */
    public void deleteTeam(Long teamId) {
        TEAM.remove(teamId);
    }
}
