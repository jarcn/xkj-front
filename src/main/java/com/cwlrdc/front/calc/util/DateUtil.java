package com.cwlrdc.front.calc.util;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

/**
 * Created by chenjia on 2017/8/22.
 */
@Slf4j
public class DateUtil {

    public static Date parseDate(String strDay, String pattern) {
        try {
            return DateUtils.parseDate(strDay, pattern);
        } catch (ParseException e) {
            log.warn("时间转换异常", e);
           return null;
        }
    }
    //格式化
    public static String formatDate(final Date date, String pattern) {
        return DateFormatUtils.format(date, pattern);
    }

    //本年第几个月
    public static int day2MonthInYear(final Date date) {
        LocalDate localDate = date2LocalDate(date);
        DateTimeFormatter dTF = DateTimeFormatter.ofPattern("M");
        return Integer.parseInt(dTF.format(localDate));
    }
    public static LocalDate date2LocalDate(final Date date) {
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        return localDateTime.toLocalDate();
    }


}
