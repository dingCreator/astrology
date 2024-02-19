package com.dingCreator.immortal.behavior;

import com.dingCreator.immortal.cache.PlayerCache;
import com.dingCreator.immortal.cache.TeamCache;
import com.dingCreator.immortal.dto.PlayerDTO;
import com.dingCreator.immortal.dto.TeamDTO;
import com.dingCreator.immortal.enums.exception.TeamExceptionEnum;
import com.dingCreator.immortal.constants.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 组队行为
 *
 * @author ding
 * @date 2024/2/1
 */
public class TeamBehavior {

    private PlayerCache playerCache;

    private TeamCache teamCache;

    /**
     * 创建小队
     *
     * @param initiatorId 发起人ID
     * @return 小队编号
     */
    public Long buildTeam(Long initiatorId) {
        PlayerDTO playerDTO = playerCache.getPlayerById(initiatorId);
        playerDTO.setTeam(true);

        teamCache.createTeam(initiatorId);
        return playerDTO.getPlayer().getId();
    }

    /**
     * 加入小队
     *
     * @param initiatorId 发起人ID
     * @param teamId      小队编号
     */
    public void joinTeam(Long initiatorId, Long teamId) {
        TeamDTO teamDTO = teamCache.getTeamById(teamId);
        if (Objects.isNull(teamDTO)) {
            throw TeamExceptionEnum.TEAM_NOT_EXIST.getException();
        }
        if (teamDTO.getMembers().size() >= Constants.TEAM_MEMBER_LIMIT) {
            throw TeamExceptionEnum.TEAM_MEMBER_OVER_LIMIT.getException();
        }
        PlayerDTO playerDTO = playerCache.getPlayerById(initiatorId);
        playerDTO.setTeam(true);

        teamDTO.getMembers().add(initiatorId);
    }

    /**
     * 移出小队（仅限队长）
     *
     * @param initiatorId 发起人ID
     * @param removedId   被移出人ID
     */
    public void removeMember(Long initiatorId, Long removedId) {
        if (Objects.equals(initiatorId, removedId)) {
            throw TeamExceptionEnum.NOT_ALLOW_OWN_2_OWN.getException();
        }
        TeamDTO teamDTO = teamCache.getTeamById(initiatorId);
        if (Objects.isNull(teamDTO)) {
            throw TeamExceptionEnum.NOT_CAPTAIN.getException();
        }

        if (!teamDTO.getMembers().remove(removedId)) {
            throw TeamExceptionEnum.NOT_TEAM_MEMBER.getException();
        }

        PlayerDTO playerDTO = playerCache.getPlayerById(removedId);
        playerDTO.setTeam(false);
    }

    /**
     * 退出小队
     *
     * @param initiatorId 发起人ID
     */
    public void leaveTeam(Long initiatorId) {
        TeamDTO teamDTO = teamCache.getTeamByPlayerId(initiatorId);
        if (Objects.isNull(teamDTO)) {
            throw TeamExceptionEnum.NOT_IN_TEAM.getException();
        }
        if (teamDTO.getCaptainId().equals(initiatorId)) {
            throw TeamExceptionEnum.CAPTAIN_NOT_ALLOW.getException();
        }

        PlayerDTO playerDTO = playerCache.getPlayerById(initiatorId);
        playerDTO.setTeam(false);
    }

    /**
     * 小队成员排序
     *
     * @param initiatorId 发起人ID
     * @param index       新的排序
     */
    public void sortMembers(Long initiatorId, List<Integer> index) {
        TeamDTO teamDTO = teamCache.getTeamById(initiatorId);
        if (Objects.isNull(teamDTO)) {
            throw TeamExceptionEnum.NOT_CAPTAIN.getException();
        }
        // 去重后才是需要的排序，此时若数量对不上，则输入有误
        index = index.stream().distinct().collect(Collectors.toList());
        List<Long> oldMembers = teamDTO.getMembers();
        if (oldMembers.size() != index.size()) {
            throw TeamExceptionEnum.SORT_INDEX_NOT_MATCH.getException();
        }
        // 新顺序
        List<Long> newMembers = new ArrayList<>(3);
        for (int idx : index) {
            if (idx < 1 || idx > oldMembers.size()) {
                throw TeamExceptionEnum.SORT_INDEX_NOT_MATCH.getException();
            }
            newMembers.add(oldMembers.get(idx - 1));
        }
        teamDTO.setMembers(newMembers);
    }

    /**
     * 更换队长
     *
     * @param from 原队长ID
     * @param to   新队长ID
     */
    public void changeCaptain(Long from, Long to) {
        if (Objects.equals(from, to)) {
            throw TeamExceptionEnum.NOT_ALLOW_OWN_2_OWN.getException();
        }
        TeamDTO teamDTO = teamCache.getTeamById(from);
        if (Objects.isNull(teamDTO)) {
            throw TeamExceptionEnum.NOT_CAPTAIN.getException();
        }
        if (!teamDTO.getMembers().contains(to)) {
            throw TeamExceptionEnum.NOT_TEAM_MEMBER.getException();
        }
        teamDTO.setCaptainId(to);
        teamCache.deleteTeam(from);
        teamCache.createTeam(to, teamDTO);
    }

    /**
     * 解散小队
     *
     * @param initiatorId 发起人ID
     */
    public void deleteTeam(Long initiatorId) {
        TeamDTO teamDTO = teamCache.getTeamById(initiatorId);
        if (Objects.isNull(teamDTO)) {
            throw TeamExceptionEnum.NOT_CAPTAIN.getException();
        }
        teamDTO.getMembers().forEach(this::leaveTeam);
        teamCache.deleteTeam(initiatorId);
    }
}
