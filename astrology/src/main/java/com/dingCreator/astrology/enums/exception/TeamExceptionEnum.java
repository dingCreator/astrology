package com.dingCreator.astrology.enums.exception;

import com.dingCreator.astrology.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.dingCreator.astrology.constants.Constants.TEAM_EXCEPTION_PREFIX;

/**
 * @author ding
 * @date 2024/2/1
 */
@Getter
@AllArgsConstructor
public enum TeamExceptionEnum {
    /**
     * 业务异常
     */
    ALREADY_IN_TEAM(TEAM_EXCEPTION_PREFIX + "000", "已经处于组队状态"),
    NOT_IN_TEAM(TEAM_EXCEPTION_PREFIX + "001", "你没有加入小队"),
    TEAM_NOT_EXIST(TEAM_EXCEPTION_PREFIX + "002", "小队不存在"),
    NOT_CAPTAIN(TEAM_EXCEPTION_PREFIX + "003", "非小队队长，无权操作"),
    NOT_TEAM_MEMBER(TEAM_EXCEPTION_PREFIX + "004", "该成员不在小队中"),
    CAPTAIN_NOT_ALLOW(TEAM_EXCEPTION_PREFIX + "005", "队长不允许退出小队，可选择解散小队或队长交接"),
    TEAM_MEMBER_OVER_LIMIT(TEAM_EXCEPTION_PREFIX + "006", "小队成员数量已达上限"),
    SORT_INDEX_NOT_MATCH(TEAM_EXCEPTION_PREFIX + "007", "排序有误"),
    NOT_ALLOW_OWN_2_OWN(TEAM_EXCEPTION_PREFIX + "008", "不能对自己操作"),
    PRE_JOIN_NOT_FREE(TEAM_EXCEPTION_PREFIX + "009", "仅空闲状态可申请加入小队"),
    TEAM_NOT_FREE(TEAM_EXCEPTION_PREFIX + "010", "只能加入空闲状态的队伍"),
    NOT_IN_SAME_MAP(TEAM_EXCEPTION_PREFIX + "011", "只能加入处于同一区域的队伍"),
    INDEX_OUT_OF_RANGE(TEAM_EXCEPTION_PREFIX + "012", "输入的序号有误"),
    ;

    private final String code;
    private final String cheDesc;

    public BusinessException getException() {
        return new BusinessException(this.code, this.cheDesc);
    }
}
