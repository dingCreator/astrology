package com.dingCreator.astrology.dto.activity;

import lombok.Data;

/**
 * @author ding
 * @date 2024/12/4
 */
@Data
public class SignActivityAwardRuleDTO extends BaseActivityAwardRuleDTO {

    public static final String FIELD_SIGN_AWARD_TYPE = "signAwardType";

    /**
     * 签到奖励类型
     */
    private String signAwardType;
}
