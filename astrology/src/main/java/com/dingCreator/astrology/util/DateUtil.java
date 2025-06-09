package com.dingCreator.astrology.util;

import com.dingCreator.astrology.constants.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author ding
 * @date 2024/8/24
 */
public class DateUtil {

    private static final List<DateTimeFormatter> DATE_TIME_FORMATTER = Arrays.asList(
            DateTimeFormatter.ofPattern(Constants.DATE_FORMAT_1 + Constants.BLANK + Constants.TIME_FORMAT_1),
            DateTimeFormatter.ofPattern(Constants.DATE_FORMAT_2 + Constants.BLANK + Constants.TIME_FORMAT_1),
            DateTimeFormatter.ofPattern(Constants.DATE_FORMAT_3 + Constants.BLANK + Constants.TIME_FORMAT_1),
            DateTimeFormatter.ofPattern(Constants.DATE_FORMAT_4 + Constants.BLANK + Constants.TIME_FORMAT_1),
            DateTimeFormatter.ofPattern(Constants.DATE_FORMAT_1 + Constants.BLANK + Constants.TIME_FORMAT_2),
            DateTimeFormatter.ofPattern(Constants.DATE_FORMAT_2 + Constants.BLANK + Constants.TIME_FORMAT_2),
            DateTimeFormatter.ofPattern(Constants.DATE_FORMAT_3 + Constants.BLANK + Constants.TIME_FORMAT_2),
            DateTimeFormatter.ofPattern(Constants.DATE_FORMAT_4 + Constants.BLANK + Constants.TIME_FORMAT_2)
    );

    private static final List<DateTimeFormatter> DATE_FORMATTER = Arrays.asList(
            DateTimeFormatter.ofPattern(Constants.DATE_FORMAT_1),
            DateTimeFormatter.ofPattern(Constants.DATE_FORMAT_2),
            DateTimeFormatter.ofPattern(Constants.DATE_FORMAT_3),
            DateTimeFormatter.ofPattern(Constants.DATE_FORMAT_4)
    );

    public static LocalDate parseDate(String str) {
        for (DateTimeFormatter formatter : DATE_FORMATTER) {
            try {
                LocalDate localDate = LocalDate.parse(str, formatter);
                if (Objects.nonNull(localDate)) {
                    return localDate;
                }
            } catch (Exception e) {

            }
        }
        return null;
    }

    public static LocalDateTime parseDateTime(String str) {
        for (DateTimeFormatter formatter : DATE_TIME_FORMATTER) {
            try {
                LocalDateTime localDateTime = LocalDateTime.parse(str, formatter);
                if (Objects.nonNull(localDateTime)) {
                    return localDateTime;
                }
            } catch (Exception e) {

            }
        }
        return null;
    }
}
