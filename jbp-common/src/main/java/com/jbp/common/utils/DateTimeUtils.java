package com.jbp.common.utils;



import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 日期工具类
 *
 * @author zoro
 */
public class DateTimeUtils extends DateUtils {
    public static void main(String[] args) {
        try {
            DateTimeUtils.parseDate("20240929004453", DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN2);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    //Mysql支持的时间戳限制
    static long minTime = Timestamp.valueOf("1970-01-01 09:00:00").getTime();
    static long maxTime = Timestamp.valueOf("2038-01-19 11:00:00").getTime();
    /**
     * yyyy-MM-dd HH:mm:ss 格式
     */
    public static final String DEFAULT_DATE_TIME_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    /**
     * yyyyMMddHHmmss
     */
    public static final String DEFAULT_DATE_TIME_FORMAT_PATTERN2 = "yyyyMMddHHmmss";
    /**
     * yyyy-MM-dd HH:mm 格式
     */
    public static final String DEFAULT_DATE_TIME_HHmm_FORMAT_PATTERN = "yyyy-MM-dd HH:mm";
    /**
     * yyyy-MM-dd HH 格式
     */
    public static final String DEFAULT_DATE_TIME_HH_FORMAT_PATTERN = "yyyy-MM-dd HH";
    /**
     * yyyy-MM-dd 格式
     */
    public static final String DEFAULT_DATE_FORMAT_PATTERN = "yyyy-MM-dd";
    /**
     * yyyyMMdd 格式
     */
    public static final String DEFAULT_DATE_FORMAT_PATTERN2 = "yyyyMMdd";
    /**
     * HH:mm:ss 格式
     */
    public static final String DEFAULT_TIME_FORMAT_PATTERN = "HH:mm:ss";
    /**

     */
    public static final String DEFAULT_TIME_HHmm_FORMAT_PATTERN = "HH:mm";
    /**
     * yyyy年MM月
     */
    public final static String DEFAULT_YYYY_MM_FORMAT_PATTERN = "yyyy年MM月";
    /**
     * MM月dd号
     */
    public final static String DEFAULT_MM_dd_FORMAT_PATTERN = "MM月dd号";
    /**
     * yyyy年
     */
    public final static String DEFAULT_YYYY_FORMAT_PATTERN = "yyyy年";

    public static Date getNow() {
        return new Date();
    }

    public static int getDaysBetweenDate(Date begin, Date end) {
        Long time = end.getTime() - begin.getTime();
        return (int) (time / (1000 * 60 * 60 * 24));
    }

    public static Date parseDate(String str) {
        return parseDate(str, 1);
    }

    public static String getYearStr(Date date, Integer num) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.YEAR, num);
        return format(cal.getTime(), DEFAULT_YYYY_FORMAT_PATTERN);
    }

    /**
     * <ul>
     * <li>日期转换3中模式</li>
     * <li>_1: 转换失败异常直接抛出
     * <li>
     * <li>_2: 当传入值为空是返回 null 否则转换失败抛出异常
     * <li>
     * <li>_3: 当传入值为空是返回 null或者转换失败都返回null
     * <li>
     * </ul>
     *
     * @return
     */
    @Deprecated
    public static Date parseDate(String arg0, Integer modeNum) {
        if (StringUtils.isBlank(arg0)) {
            if (modeNum == 2 || modeNum == 3) {
                return null;
            }
            throw new IllegalArgumentException("Date and Patterns must not be null");
        }
        try {
            return parseDate(arg0,
                    new String[]{DEFAULT_DATE_TIME_FORMAT_PATTERN, DEFAULT_DATE_TIME_HHmm_FORMAT_PATTERN,
                            DEFAULT_DATE_TIME_HH_FORMAT_PATTERN, DEFAULT_DATE_FORMAT_PATTERN,
                            DEFAULT_TIME_FORMAT_PATTERN, DEFAULT_TIME_HHmm_FORMAT_PATTERN, DEFAULT_YYYY_FORMAT_PATTERN,
                            DEFAULT_YYYY_MM_FORMAT_PATTERN, DEFAULT_DATE_FORMAT_PATTERN2, DEFAULT_DATE_TIME_FORMAT_PATTERN2});
        } catch (ParseException e) {
            if (modeNum == 1) {
                throw new IllegalArgumentException(arg0, e);
            }
        }
        return null;
    }

    public static String getWeekClearing(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.setTime(date);
        int week = cal.get(Calendar.WEEK_OF_YEAR);
        return format(date, DEFAULT_YYYY_FORMAT_PATTERN) + week + "周";
    }

    public static Date getYearStart(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_YEAR, 1);
        return getStartDate(cal.getTime());
    }

    public static Date getYearEnd(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MONTH, 11);
        return getMonthEnd(cal.getTime());
    }

    public static Date getMonthStart(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return getStartDate(cal.getTime());
    }

    public static Date getMonthEnd(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getSpecficMonthStart(date, 1));
        cal.add(Calendar.DAY_OF_YEAR, -1);
        return getFinallyDate(cal.getTime());
    }

    public static Date getYearClearingStart(Date date) {
        date = getMonthClearingEnd(date);
        date = addYears(date, -1);
        date = setMonths(date, 11);
        date = setDays(date, 26);
        return getStartDate(date);
    }

    public static Date getYearClearingEnd(Date date) {
        date = getMonthClearingEnd(date);
        date = setMonths(date, 11);
        date = setDays(date, 25);
        return getFinallyDate(date);
    }

    public static Date getMonthClearingStart(Date date) {
        if (getDateField(date, Calendar.DATE) > 25) {
            date = getSpecficMonthStart(date, 0, 26);
        } else {
            date = getSpecficMonthStart(date, -1, 26);
        }
        return date;
    }

    public static Date getMonthClearingEnd(Date date) {
        if (getDateField(date, Calendar.DATE) > 25) {
            date = getSpecficMonthStart(date, 1, 25);
        } else {
            date = getSpecficMonthStart(date, 0, 25);
        }
        return getFinallyDate(date);
    }

    /**
     * 获取date月后的amount月的第一天的开始时间
     *
     * @param amount 可正、可负
     * @return
     */
    public static Date getSpecficMonthStart(Date date, int amount) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, amount);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return getStartDate(cal.getTime());
    }

    /**
     * 获取date月后的amount月的第amount2天的开始时间
     *
     * @param amount1 可正、可负 amount2 只能是正
     * @return
     */
    public static Date getSpecficMonthStart(Date date, int amount1, int amount2) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, amount1);
        cal.set(Calendar.DAY_OF_MONTH, amount2);
        return getStartDate(cal.getTime());
    }

    /**
     * 得到指定日期的一天的的最后时刻23:59:59
     *
     * @param date
     * @return
     */
    public static Date getFinallyDate(Date date) {
        String temp = format(DEFAULT_DATE_FORMAT_PATTERN).format(date);
        temp += " 23:59:59";

        try {
            return format(DEFAULT_DATE_TIME_FORMAT_PATTERN).parse(temp);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 得到指定日期的一天的开始时刻00:00:00
     *
     * @param date
     * @return
     */
    public static Date getStartDate(Date date) {
        String temp = format(DEFAULT_DATE_FORMAT_PATTERN).format(date);
        temp += " 00:00:00";

        try {
            return format(DEFAULT_DATE_TIME_FORMAT_PATTERN).parse(temp);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 根据类型获取年度月度信息
     *
     * @param type
     * @return
     */
    public static String getNowFormatForType(String type) {
        if (StringUtils.equals(type, Type.年度.toString())) {
            return format(getMonthClearingEnd(getNow()), DEFAULT_YYYY_FORMAT_PATTERN);
        }
        if (StringUtils.equals(type, Type.月度.toString())) {
            return format(getMonthClearingEnd(getNow()), DEFAULT_YYYY_MM_FORMAT_PATTERN);
        }
        throw new IllegalArgumentException("type is not exists");
    }

    /**
     * 根据类型获取 年度 | 月度信息
     *
     * @param type
     * @return
     * @throws ParseException
     */
    public static String getTimeFormatForType(String type, Date time) {

        if (StringUtils.equals(type, Type.年度.toString())) {
            return format(getYearClearingEnd(time), DEFAULT_YYYY_FORMAT_PATTERN);
        }
        if (StringUtils.equals(type, Type.月度.toString())) {
            return format(getMonthClearingEnd(time), DEFAULT_YYYY_MM_FORMAT_PATTERN);
        }
        throw new IllegalArgumentException("type is not exists");
    }

    /**
     * 根据类型获取上一年度 | 月度信息
     */
    public static String getNowAboveForType(String type) {
        if (StringUtils.equals(type, Type.年度.toString())) {
            return format(addYears(getYearClearingEnd(getNow()), -1), DEFAULT_YYYY_FORMAT_PATTERN);
        }
        if (StringUtils.equals(type, Type.月度.toString())) {
            return format(addMonths(getMonthClearingEnd(getNow()), -1), DEFAULT_YYYY_MM_FORMAT_PATTERN);
        }
        throw new IllegalArgumentException("type is not exists");
    }

    /**
     * 根据类型获取当前时间往前或则往后num 年度 | 月度信息
     */
    public static String getNowAboveForType(String type, int num) {
        if (StringUtils.equals(type, Type.年度.toString())) {
            return format(addYears(getYearClearingEnd(getNow()), num), DEFAULT_YYYY_FORMAT_PATTERN);
        }
        if (StringUtils.equals(type, Type.月度.toString())) {
            return format(addMonths(getMonthClearingEnd(getNow()), num), DEFAULT_YYYY_MM_FORMAT_PATTERN);
        }
        throw new IllegalArgumentException("type is not exists");
    }


    /**
     * 根据类型获取传入时间往前或则往后num 年度 | 月度信息
     */
    public static String AboveMonths(String source,String type, int num) {
        Date date =getNow();
        try {
            if (StringUtils.equals(type, Type.年度.toString())) {
                date = new SimpleDateFormat(DEFAULT_YYYY_FORMAT_PATTERN).parse(source);
                return format(addYears(getYearClearingEnd(date), num), DEFAULT_YYYY_FORMAT_PATTERN);
            }
            if (StringUtils.equals(type, Type.月度.toString())) {
                date = new SimpleDateFormat(DEFAULT_YYYY_MM_FORMAT_PATTERN).parse(source);
                return format(addMonths(getMonthClearingEnd(date), num), DEFAULT_YYYY_MM_FORMAT_PATTERN);
            }
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException("type is not exists");
    }


    /**
     * 根据类型 和 日期获取开始时间
     */
    public static Date getTimeStartForType(String time, String type) {
        SimpleDateFormat format = null;
        try {
            if (StringUtils.equals(type, Type.年度.toString())) {
                format = format(DEFAULT_YYYY_FORMAT_PATTERN);
                return getYearClearingStart(format.parse(time));
            }
            if (StringUtils.equals(type, Type.月度.toString())) {
                format = format(DEFAULT_YYYY_MM_FORMAT_PATTERN);
                return getMonthClearingStart(format.parse(time));
            }
        } catch (Exception e) {
        }
        throw new IllegalArgumentException("type is not exists");
    }

    /**
     * 根据类型 和 日期获取结束时间
     */
    public static Date getTimeEndForType(String time, String type) {
        SimpleDateFormat format = null;
        try {
            if (StringUtils.equals(type, Type.年度.toString())) {
                format = format(DEFAULT_YYYY_FORMAT_PATTERN);
                return getYearClearingEnd(format.parse(time));
            }
            if (StringUtils.equals(type, Type.月度.toString())) {
                format = format(DEFAULT_YYYY_MM_FORMAT_PATTERN);
                return getMonthClearingEnd(format.parse(time));
            }
        } catch (Exception e) {
        }
        throw new IllegalArgumentException("type is not exists");
    }

    public static String format(Date date, String pattern) {
        if (date == null) {
            throw new IllegalArgumentException("date is null");
        }
        if (pattern == null) {
            throw new IllegalArgumentException("pattern is null");
        }
        return format(pattern).format(date);
    }

    protected static SimpleDateFormat format(String pattern) {
        return new SimpleDateFormat(pattern);
    }

    private static int getDateField(Date date, int field) {
        Calendar c = getCalendar();
        c.setTime(date);
        return c.get(field);
    }

    private static Calendar getCalendar() {
        Calendar calender = Calendar.getInstance();
        return calender;
    }

    public static enum Type {
        年度, 月度
    }

    public static int getAge(Date birthDay) {
        Calendar cal = Calendar.getInstance();
        if (cal.before(birthDay)) {
            return 18;
        }
        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH);
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
        cal.setTime(birthDay);

        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

        int age = yearNow - yearBirth;

        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth) age--;
            } else {
                age--;
            }
        }
        return age;
    }

    //判断 并转换时间格式 ditNumber = 43607.4166666667
    public static String getTimeStrFromDecimalStr(String ditNumber) {
        //如果不是数字
        if(!isNumeric(ditNumber)){
            return null;
        }
        //如果是数字 小于0则 返回
        BigDecimal bd = new BigDecimal(ditNumber);
        int days = bd.intValue();//天数
        int mills = (int) Math.round(bd.subtract(new BigDecimal(days)).doubleValue() * 24 * 3600);

        //获取时间
        Calendar c = Calendar.getInstance();
        c.set(1900, 0, 1);
        c.add(Calendar.DATE, days - 2);
        int hour = mills / 3600;
        int minute = (mills - hour * 3600) / 60;
        int second = mills - hour * 3600 - minute * 60;
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, second);

        Date d = c.getTime();//Date

        Timestamp t = new Timestamp(d.getTime());
        try {
            //时间戳区间判断
            if(minTime <= d.getTime() && d.getTime() <= maxTime){
                return  format(d,DEFAULT_DATE_TIME_FORMAT_PATTERN);
            }else{
                return "outOfRange";
            }
        } catch (Exception e) {
            System.out.println("传入日期错误" + c.getTime());
        }
        return "Error";
    }


    //校验是否数据含小数点
    private static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]+\\.*[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    public static String timestamp() {
        return String.valueOf(System.currentTimeMillis() / 1000);
    }
}
