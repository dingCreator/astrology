package com.dingCreator.astrology.enums.activity;

import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.dto.activity.ActivityStaticsDTO;
import com.dingCreator.astrology.enums.exception.ActivityExceptionEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * @author ding
 * @date 2024/11/28
 */
@Getter
@AllArgsConstructor
public enum ActivityLimitTypeEnum {
    /**
     * 参与限制类型
     */
    NONE(0, "无限制", () -> null),
    MINUTE(1, "每分钟",
            () -> LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern(Constants.DATE_FORMAT_5 + Constants.BLANK + Constants.MINUTE_FORMAT_1)
            )),
    HOUR(2, "每小时",
            () -> LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern(Constants.DATE_FORMAT_5 + Constants.BLANK + Constants.HOUR_FORMAT_1)
            )),
    DAY(3, "每天", () -> LocalDate.now().format(DateTimeFormatter.ofPattern(Constants.DATE_FORMAT_5))),
    MONTH(4, "每月", () -> LocalDate.now().format(DateTimeFormatter.ofPattern(Constants.MONTH_FORMAT_5))),
    YEAR(5, "每年", () -> LocalDate.now().format(DateTimeFormatter.ofPattern(Constants.YEAR_FORMAT_1))),
    ALL(6, "总", () -> null),
    ;

    private final Integer code;

    private final String chnDesc;

    private final Supplier<String> pointInTime;

    public static ActivityLimitTypeEnum getByCode(Integer code) {
        return Arrays.stream(values()).filter(e -> e.getCode().equals(code)).findFirst().orElse(null);
    }
}
