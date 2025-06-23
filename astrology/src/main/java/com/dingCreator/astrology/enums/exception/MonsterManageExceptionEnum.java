package com.dingCreator.astrology.enums.exception;

import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author ding
 * @date 2024/3/29
 */
@Getter
@AllArgsConstructor
public enum MonsterManageExceptionEnum {

    /**
     * 怪物相关异常
     */
    MONSTER_NOT_FOUND(Constants.MONSTER_EXCEPTION_PREFIX + "000", "boss不存在"),
    MONSTER_PROP_NOT_FOUND(Constants.MONSTER_EXCEPTION_PREFIX + "001", "属性类型输入错误"),
    MONSTER_PROP_VAL_ERR(Constants.MONSTER_EXCEPTION_PREFIX + "002", "属性值输入错误"),
    MONSTER_SKILL_ID_NOT_FOUND(Constants.MONSTER_EXCEPTION_PREFIX + "003", "id对应的技能不存在"),
    MONSTER_SKILL_INACTIVE(Constants.MONSTER_EXCEPTION_PREFIX + "004", "配置主动技能时输入了被动技能，请检查"),
    MONSTER_SKILL_ACTIVE(Constants.MONSTER_EXCEPTION_PREFIX + "005", "配置被动技能时输入了主动技能，请检查"),
    LIST_MONSTER_PAGE_ERR(Constants.MONSTER_EXCEPTION_PREFIX + "006", "错误的页码"),
    WORLD_BOSS_TIME_INVALID(Constants.MONSTER_EXCEPTION_PREFIX + "007", "世界boss时间设置错误"),
    WORLD_BOSS_ID_INVALID(Constants.MONSTER_EXCEPTION_PREFIX + "008", "boss ID配置有误"),
    RANK_UP_BOSS_JOB_INVALID(Constants.MONSTER_EXCEPTION_PREFIX + "009", "职业配置有误"),
    RANK_UP_BOSS_RANK_INVALID(Constants.MONSTER_EXCEPTION_PREFIX + "010", "阶级配置有误"),
    MONSTER_PROP_FORMAT_ERR(Constants.MONSTER_EXCEPTION_PREFIX + "011", "格式输入错误"),
    ;

    private final String code;
    private final String chnDesc;

    public BusinessException getException() {
        return new BusinessException(this.code, this.chnDesc);
    }
}
