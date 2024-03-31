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
    INVALID_SKILL_ID(new BusinessException(Constants.SKILL_EXCEPTION_PREFIX + "000", "输入的技能ID含未拥有的技能")),
    INACTIVE_SKILL_NOT_ALLOW(new BusinessException(Constants.SKILL_EXCEPTION_PREFIX + "001", "被动技能不能装备到技能栏")),
    JOB_SKILL_NOT_ALLOW(new BusinessException(Constants.SKILL_EXCEPTION_PREFIX + "002", "你的职业不允许装备该技能")),
    SKILL_ID_NOT_EXIST(new BusinessException(Constants.SKILL_EXCEPTION_PREFIX + "003", "技能ID对应的技能不存在")),
    SKILL_NAME_NOT_EXIST(new BusinessException(Constants.SKILL_EXCEPTION_PREFIX + "004", "技能名称对应的技能不存在")),
    ;
    private final BusinessException exception;
}
