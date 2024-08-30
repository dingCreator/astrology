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
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author ding
 * @date 2024/8/24
 */
public class DateUtil {

    private static final Logger LOG = LoggerFactory.getLogger(DateUtil.class);
    private static final List<DateTimeFormatter> DATE_TIME_FORMATTER = Arrays.asList(
            DateTimeFormatter.ofPattern(Constants.DATE_FORMAT_1 + Constants.BLANK + Constants.TIME_FORMAT),
            DateTimeFormatter.ofPattern(Constants.DATE_FORMAT_2 + Constants.BLANK + Constants.TIME_FORMAT),
            DateTimeFormatter.ofPattern(Constants.DATE_FORMAT_3 + Constants.BLANK + Constants.TIME_FORMAT),
            DateTimeFormatter.ofPattern(Constants.DATE_FORMAT_4 + Constants.BLANK + Constants.TIME_FORMAT)
    );

    public static LocalDate parseDate(String str) {
        return doParse(formatter -> LocalDate.parse(str, formatter));
    }

    public static LocalDateTime parseDateTime(String str) {
        return doParse(formatter -> LocalDateTime.parse(str, formatter));
    }

    private static <T> T doParse(Function<DateTimeFormatter, T> function) {
        for (DateTimeFormatter formatter : DATE_TIME_FORMATTER) {
            try {
                return function.apply(formatter);
            } catch (Exception e) {

            }
        }
        return null;
    }
}
