package com.dingCreator.astrology.behavior;

import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.cache.TeamCache;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.dto.organism.player.PlayerDTO;
import com.dingCreator.astrology.dto.organism.player.PlayerInfoDTO;
import com.dingCreator.astrology.dto.TeamDTO;
import com.dingCreator.astrology.enums.PlayerStatusEnum;
import com.dingCreator.astrology.enums.exception.TeamExceptionEnum;
import com.dingCreator.astrology.util.MapUtil;
import com.dingCreator.astrology.vo.TeamVO;

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
        PlayerInfoDTO playerInfoDTO = PlayerCache.getPlayerById(initiatorId);
        playerInfoDTO.setTeam(true);

        TeamCache.createTeam(initiatorId);
        return playerInfoDTO.getPlayerDTO().getId();
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
        PlayerDTO initiator = PlayerCache.getPlayerById(initiatorId).getPlayerDTO();
        PlayerDTO captain = PlayerCache.getPlayerById(teamId).getPlayerDTO();

        if (!PlayerStatusEnum.FREE.getCode().equals(initiator.getStatus())) {
            throw TeamExceptionEnum.PRE_JOIN_NOT_FREE.getException();
        }
        if (!PlayerStatusEnum.FREE.getCode().equals(captain.getStatus())) {
            throw TeamExceptionEnum.TEAM_NOT_FREE.getException();
        }
        if (!MapUtil.getNowLocation(initiator.getMapId()).equals(MapUtil.getNowLocation(captain.getMapId()))) {
            throw TeamExceptionEnum.NOT_IN_SAME_MAP.getException();
        }
        if (teamDTO.getMembers().size() >= Constants.TEAM_MEMBER_LIMIT) {
            throw TeamExceptionEnum.TEAM_MEMBER_OVER_LIMIT.getException();
        }
        PlayerInfoDTO playerInfoDTO = PlayerCache.getPlayerById(initiatorId);
        playerInfoDTO.setTeam(true);

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

        PlayerInfoDTO playerInfoDTO = PlayerCache.getPlayerById(removedId);
        playerInfoDTO.setTeam(false);
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

        PlayerInfoDTO playerInfoDTO = PlayerCache.getPlayerById(initiatorId);
        playerInfoDTO.setTeam(false);
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
        PlayerCache.getPlayerById(initiatorId).setTeam(false);
        teamDTO.getMembers().stream().filter(member -> !member.equals(teamDTO.getCaptainId()))
                .forEach(this::leaveTeam);
        TeamCache.deleteTeam(initiatorId);
    }

    /**
     * 获取队伍信息
     *
     * @param playerId 玩家ID
     * @return 队伍信息
     */
    public TeamVO getTeamInfo(Long playerId) {
        PlayerCache.getPlayerById(playerId);
        TeamDTO teamDTO = TeamCache.getTeamByPlayerId(playerId);
        if (Objects.isNull(teamDTO)) {
            throw TeamExceptionEnum.NOT_IN_TEAM.getException();
        }

        List<String> membersNick = teamDTO.getMembers().stream()
                .map(id -> PlayerCache.getPlayerById(id).getPlayerDTO().getName()).collect(Collectors.toList());
        return new TeamVO(teamDTO.getCaptainId(),
                PlayerCache.getPlayerById(teamDTO.getCaptainId()).getPlayerDTO().getName(), membersNick);
    }

    public void captainOnlyValidate(long playerId) {
        PlayerInfoDTO playerInfoDTO = PlayerCache.getPlayerById(playerId);
        if (playerInfoDTO.getTeam() && Objects.isNull(TeamCache.getTeamById(playerId))) {
            throw TeamExceptionEnum.NOT_CAPTAIN.getException();
        }
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
