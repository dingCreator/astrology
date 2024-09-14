package com.dingCreator.astrology.enums.exception;

import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author ding
 * @date 2024/4/24
 */
@Getter
@AllArgsConstructor
public enum EquipmentExceptionEnum {

    /**
     * 装备错误
     */
    EQUIPMENT_NOT_EXIST(new BusinessException(Constants.EQUIPMENT_EXCEPTION_PREFIX + "000", "装备不存在")),
    NOT_YOUR_EQUIPMENT(new BusinessException(Constants.EQUIPMENT_EXCEPTION_PREFIX + "001", "这件装备不是你的")),
    DONT_HAVE_EQUIPMENT(new BusinessException(Constants.EQUIPMENT_EXCEPTION_PREFIX + "002", "你未持有此装备")),
    DATA_ERROR(new BusinessException(Constants.EQUIPMENT_EXCEPTION_PREFIX + "003", "装备数据出错，请联系管理员")),
    INVALID_JOB(new BusinessException(Constants.EQUIPMENT_EXCEPTION_PREFIX + "004", "你的职业无法使用该装备")),
    LEVEL_TOO_LOW(new BusinessException(Constants.EQUIPMENT_EXCEPTION_PREFIX + "005", "等级不足，无法使用该装备")),
    ;

    private final BusinessException exception;
}
