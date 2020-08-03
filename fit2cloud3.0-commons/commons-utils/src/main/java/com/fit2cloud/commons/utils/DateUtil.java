package com.fit2cloud.commons.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 日期工具
 *
 * @author kun.mo
 */
public class DateUtil extends DateUtils {

    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
    public static final String YYYY_MM_DD_HH = "yyyy-MM-dd HH";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    public static final String YYYYMMDDHHMM = "yyyyMMddHHmm";
    public static final String YYYYMMDDHH = "yyyyMMddHH";
    public static final String YYYYMMDD = "yyyyMMdd";

    /**
     * 获取当前的日期
     *
     * @return 当前日期
     */
    public static Date getCurrentDate() {
        return new Date();
    }

    /**
     * 获取昨天的日期
     *
     * @return 昨天日期
     */
    public static Date getYesterdayDate() {
        return new Date(getCurrentTimeMillis() - 0x5265c00L);
    }

    /**
     * 获取当前时间戳
     *
     * @return 当前时间戳
     */
    public static long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }

    /**
     * 获取格式化后的当前日期
     *
     * @param format 格式化样式，例如：DateUtils.YYYY_MM_DD_HH_MM_SS
     * @return 当前日期的字符串
     */
    public static String getCurrentFormatDate(String format) {
        Date date = getCurrentDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    /**
     * 格式化日期转字符串
     *
     * @param format 格式化样式
     * @param date   日期
     * @return 日期字符串
     */
    public static String getDate2String(String format, Date date) {
        if (date != null) {
            if (StringUtils.isEmpty(format)) {
                format = YYYY_MM_DD_HH_MM_SS;
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);

            return simpleDateFormat.format(date);
        } else {
            return "";
        }
    }

    /**
     * 日期转字符串
     *
     * @param date 日期
     * @return 日期字符串
     */
    public static String getDate2String(Date date) {
        return getDate2String(null, date);
    }

    /**
     * 时间戳转日期
     *
     * @param l 时间戳
     * @return 日期
     */
    public static Date getLong2Date(long l) {
        return new Date(l);
    }

    /**
     * 时间戳转格式化日期字符串
     *
     * @param l      时间戳
     * @param format 格式化样式
     * @return 日期字符串
     */
    public static String getLong2ShortString(long l, String format) {
        Date date = getLong2Date(l);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    /**
     * 字符串转格式化日期
     *
     * @param format 格式化样式
     * @param str    字符串
     * @return 日期
     */
    public static Date getString2Date(String format, String str) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        ParsePosition parseposition = new ParsePosition(0);
        return simpleDateFormat.parse(str, parseposition);
    }

    /**
     * 字符串转格式化日期long
     *
     * @param format 格式化样式
     * @param str    字符串
     * @return 日期
     */
    public static long getString2Long(String format, String str) {
        Date date = getString2Date(format, str);
        if (date == null) {
            return 0;
        }
        return date.getTime();
    }

    /**
     * 字符串转日期
     *
     * @param str 字符串
     * @return 日期z
     */
    public static Date getString2Date(String str) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
        ParsePosition parseposition = new ParsePosition(0);
        return simpleDateFormat.parse(str, parseposition);
    }

    /**
     * 制定locale的转换方式
     *
     * @param format  格式化样式
     * @param locale  语言环境
     * @param dateStr 字符串
     * @return 日期
     */
    public static Date getString2Date(String format, Locale locale, String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, locale);

        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 当前时间与输入时间的天数差值
     *
     * @param l 时间戳
     * @return 天数
     */
    public static int getOffDays(long l) {
        return getOffDays(l, getCurrentTimeMillis());
    }

    /**
     * 天数差值
     *
     * @param from 开始时间戳
     * @param to   结束时间戳
     * @return 天数
     */
    public static int getOffDays(long from, long to) {
        return getOffMinutes(from, to) / 1440;
    }

    /**
     * 天数差值
     *
     * @param from 开始时间
     * @param to   结束时间
     * @return 天数
     */
    public static int getOffDays(Date from, Date to) {
        return getOffMinutes(from.getTime(), to.getTime()) / 1440;
    }

    /**
     * 月份差值
     *
     * @param from 开始时间戳
     * @param to   结束时间戳
     * @return 月数
     */
    public static int getOffMonths(long from, long to) {
        return getOffDays(from, to) / 30;
    }

    /**
     * 月份差值
     *
     * @param from 开始时间
     * @param to   结束时间
     * @return 月数
     */
    public static int getOffMonths(Date from, Date to) {
        return getOffDays(from.getTime(), to.getTime()) / 30;
    }

    /**
     * 当前时间与输入时间的分钟差值
     *
     * @param l 时间戳
     * @return DOCUMENT ME!
     */
    public static int getOffMinutes(long l) {
        return getOffMinutes(l, getCurrentTimeMillis());
    }

    /**
     * 分钟差值
     *
     * @param from 开始时间戳
     * @param to   结束时间戳
     * @return 分钟数
     */
    public static int getOffMinutes(long from, long to) {
        return (int) ((to - from) / 60000L);
    }

    /**
     * 分钟差值
     *
     * @param from 开始时间
     * @param to   结束时间
     * @return 分钟数
     */
    public static int getOffMinutes(Date from, Date to) {
        return (int) ((to.getTime() - from.getTime()) / 60000L);
    }
}
