package com.dingCreator.astrology.behavior;

import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.cache.TeamCache;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.dto.PlayerDTO;
import com.dingCreator.astrology.dto.TeamDTO;
import com.dingCreator.astrology.enums.PlayerStatusEnum;
import com.dingCreator.astrology.enums.exception.TeamExceptionEnum;
import com.dingCreator.astrology.util.MapUtil;

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

    /**
     * 创建小队
     *
     * @param initiatorId 发起人ID
     * @return 小队编号
     */
    public Long buildTeam(Long initiatorId) {
        PlayerDTO playerDTO = PlayerCache.getPlayerById(initiatorId);
        playerDTO.setTeam(true);

        TeamCache.createTeam(initiatorId);
        return playerDTO.getPlayer().getId();
    }

    /**
     * 加入小队
     *
     * @param initiatorId 发起人ID
     * @param teamId      小队编号
     */
    public void joinTeam(Long initiatorId, Long teamId) {
        TeamDTO teamDTO = TeamCache.getTeamById(teamId);
        if (Objects.isNull(teamDTO)) {
            throw TeamExceptionEnum.TEAM_NOT_EXIST.getException();
        }
        if (!PlayerStatusEnum.FREE.getCode()
                .equals(PlayerCache.getPlayerById(initiatorId).getPlayer().getStatus())) {
            throw TeamExceptionEnum.PRE_JOIN_NOT_FREE.getException();
        }
        if (!PlayerStatusEnum.FREE.getCode()
                .equals(PlayerCache.getPlayerById(teamId).getPlayer().getStatus())) {
            throw TeamExceptionEnum.TEAM_NOT_FREE.getException();
        }
        if (!MapUtil.getNowLocation(PlayerCache.getPlayerById(initiatorId).getPlayer().getMapId())
                .equals(MapUtil.getNowLocation(PlayerCache.getPlayerById(teamId).getPlayer().getMapId()))) {
            throw TeamExceptionEnum.NOT_IN_SAME_MAP.getException();
        }
        if (teamDTO.getMembers().size() >= Constants.TEAM_MEMBER_LIMIT) {
            throw TeamExceptionEnum.TEAM_MEMBER_OVER_LIMIT.getException();
        }
        PlayerDTO playerDTO = PlayerCache.getPlayerById(initiatorId);
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
        TeamDTO teamDTO = TeamCache.getTeamById(initiatorId);
        if (Objects.isNull(teamDTO)) {
            throw TeamExceptionEnum.NOT_CAPTAIN.getException();
        }

        if (!teamDTO.getMembers().remove(removedId)) {
            throw TeamExceptionEnum.NOT_TEAM_MEMBER.getException();
        }

        PlayerDTO playerDTO = PlayerCache.getPlayerById(removedId);
        playerDTO.setTeam(false);
    }

    /**
     * 退出小队
     *
     * @param initiatorId 发起人ID
     */
    public void leaveTeam(Long initiatorId) {
        TeamDTO teamDTO = TeamCache.getTeamByPlayerId(initiatorId);
        if (Objects.isNull(teamDTO)) {
            throw TeamExceptionEnum.NOT_IN_TEAM.getException();
        }
        if (teamDTO.getCaptainId().equals(initiatorId)) {
            throw TeamExceptionEnum.CAPTAIN_NOT_ALLOW.getException();
        }

        PlayerDTO playerDTO = PlayerCache.getPlayerById(initiatorId);
        playerDTO.setTeam(false);
    }

    /**
     * 小队成员排序
     *
     * @param initiatorId 发起人ID
     * @param index       新的排序
     */
    public void sortMembers(Long initiatorId, List<Integer> index) {
        TeamDTO teamDTO = TeamCache.getTeamById(initiatorId);
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
        TeamDTO teamDTO = TeamCache.getTeamById(from);
        if (Objects.isNull(teamDTO)) {
            throw TeamExceptionEnum.NOT_CAPTAIN.getException();
        }
        if (!teamDTO.getMembers().contains(to)) {
            throw TeamExceptionEnum.NOT_TEAM_MEMBER.getException();
        }
        teamDTO.setCaptainId(to);
        TeamCache.deleteTeam(from);
        TeamCache.createTeam(to, teamDTO);
    }

    /**
     * 解散小队
     *
     * @param initiatorId 发起人ID
     */
    public void deleteTeam(Long initiatorId) {
        TeamDTO teamDTO = TeamCache.getTeamById(initiatorId);
        if (Objects.isNull(teamDTO)) {
            throw TeamExceptionEnum.NOT_CAPTAIN.getException();
        }
        teamDTO.getMembers().forEach(this::leaveTeam);
        TeamCache.deleteTeam(initiatorId);
    }

    private static class Holder {
        private static final TeamBehavior BEHAVIOR = new TeamBehavior();
    }

    private TeamBehavior() {

    }

    public static TeamBehavior getInstance() {
        return Holder.BEHAVIOR;
    }
}
