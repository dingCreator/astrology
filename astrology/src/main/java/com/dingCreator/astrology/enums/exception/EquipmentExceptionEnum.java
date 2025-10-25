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
    EQUIPMENT_NOT_EXIST(Constants.EQUIPMENT_EXCEPTION_PREFIX + "000", "装备不存在"),
    NOT_YOUR_EQUIPMENT(Constants.EQUIPMENT_EXCEPTION_PREFIX + "001", "这件装备不是你的"),
    DONT_HAVE_EQUIPMENT(Constants.EQUIPMENT_EXCEPTION_PREFIX + "002", "你未持有此装备"),
    DATA_ERROR(Constants.EQUIPMENT_EXCEPTION_PREFIX + "003", "装备数据出错，请联系管理员"),
    INVALID_JOB(Constants.EQUIPMENT_EXCEPTION_PREFIX + "004", "你的职业无法使用该装备"),
    LEVEL_TOO_LOW(Constants.EQUIPMENT_EXCEPTION_PREFIX + "005", "等级不足，无法使用该装备"),
    NOT_EQUIP_WEAPON(Constants.EQUIPMENT_EXCEPTION_PREFIX + "006", "你未穿戴武器"),
    NOT_EQUIP_ARMOR(Constants.EQUIPMENT_EXCEPTION_PREFIX + "007", "你未穿戴防具"),
    NOT_EQUIP_JEWELRY(Constants.EQUIPMENT_EXCEPTION_PREFIX + "008", "你未穿戴饰品"),
    NOT_ENOUGH_EQUIPMENT(Constants.EQUIPMENT_EXCEPTION_PREFIX + "009", "装备数量不足"),
    NOT_EQUIP_RULE(Constants.EQUIPMENT_EXCEPTION_PREFIX + "010", "你未穿戴法则"),
    ;

    private final String code;
    private final String chnDesc;

    public BusinessException getException() {
        return new BusinessException(this.code, this.chnDesc);
    }
}
