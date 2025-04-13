package com.dingCreator.astrology.enums.exception;

import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author ding
 * @date 2024/3/31
 */
@Getter
@AllArgsConstructor
public enum SkillExceptionEnum {

    /**
     * 技能异常
     */
    INVALID_SKILL_ID(Constants.SKILL_EXCEPTION_PREFIX + "000", "输入的技能ID含未拥有的技能"),
    INACTIVE_SKILL_NOT_ALLOW(Constants.SKILL_EXCEPTION_PREFIX + "001", "被动技能不能装备到技能栏"),
    JOB_SKILL_NOT_ALLOW(Constants.SKILL_EXCEPTION_PREFIX + "002", "你的职业不允许装备该技能"),
    SKILL_ID_NOT_EXIST(Constants.SKILL_EXCEPTION_PREFIX + "003", "技能ID对应的技能不存在"),
    SKILL_NAME_NOT_EXIST(Constants.SKILL_EXCEPTION_PREFIX + "004", "技能名称对应的技能不存在"),
    JOB_SKILL_NOT_ALLOW_2(Constants.SKILL_EXCEPTION_PREFIX + "005", "你的职业无法学习该技能"),
    SKILL_ALREADY_EXIST(Constants.SKILL_EXCEPTION_PREFIX + "006", "你已学会此技能，无需重复学习"),
    DONT_HAVE_SKILL(Constants.SKILL_EXCEPTION_PREFIX + "007", "你并未拥有此技能"),
    CANT_ANALYSE_SKILL(Constants.SKILL_EXCEPTION_PREFIX + "008", "无法识别的技能ID或名称：【%s】")
    ;

    private final String code;
    private final String cheDesc;

    public BusinessException getException() {
        return new BusinessException(this.code, this.cheDesc);
    }

    public BusinessException getException(String... param) {
        return new BusinessException(this.code, String.format(this.cheDesc, param));
    }
}
