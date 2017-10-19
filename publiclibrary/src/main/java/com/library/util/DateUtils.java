package com.library.util;

import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 项目名:Kxt
 * 类描述:时间工具类
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/7.
 */

public class DateUtils {

    public static final String TYPE_YMD = "yyyy-MM-dd"; //年月日
    public static final String TYPE_YMDHMS = "yyyy-MM-dd HH:mm:ss";//年月日时分秒
    public static final String TYPE_YMDHM = "yyyy-MM-dd HH:mm"; //年月日时分
    public static final String TYPE_MDHM = "MM-dd HH:mm";//月日时分
    public static final String TYPE_MD = "MM-dd"; //月日
    public static final String TYPE_HMS = "HH:mm:ss"; //时分秒
    public static final String TYPE_HM = "HH:mm"; //时分
    public static final String TYPE_MS = "mm:ss"; //分秒
    public static final String TYPE_YMDE = "yyyy-MM-dd EEEE";//年月日 星期几

    public static int oneDayLong = 1000 * 60 * 60 * 24;//一天时间毫秒值
    public static int oneHourLong = 1000 * 60 * 60;//一小时时间毫秒值
    public static int oneMinuteLong = 1000 * 60;//一分钟时间毫秒值
    public static int oneSecondLong = 1000;//一秒时间毫秒值

    /**
     * Date转String
     *
     * @param date
     * @param type
     * @return
     * @throws Exception
     */
    public static String dateToString(Date date, String type) throws Exception {
        SimpleDateFormat df = new SimpleDateFormat(type);
        return df.format(date);
    }

    /**
     * String转Date
     *
     * @param time
     * @param type
     * @return
     * @throws Exception
     */
    public static Date stringToDate(String time, String type) throws Exception {
        SimpleDateFormat df = new SimpleDateFormat(type);
        return df.parse(time);
    }

    /**
     * Calendar转String
     *
     * @param calendar
     * @param type
     * @return
     */
    public static String calendarToString(Calendar calendar, String type) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat(type);
        return format.format(calendar);
    }

    /**
     * String转Calendar
     *
     * @param time
     * @param type
     * @return
     * @throws Exception
     */
    public static Calendar stringToCalendar(String time, String type) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat(type);
        Date date = format.parse(time);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    /**
     * 时间戳转string
     *
     * @param timestamp
     * @param type
     * @return
     */
    public static String transformTime(long timestamp, String type) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat(type);
        return format.format(timestamp);
    }

    /**
     * string转时间戳
     *
     * @param time
     * @param type
     * @return
     */
    public static String transfromTime(String time, String type) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat(type);
        Date date = format.parse(time);
        return date.getTime() + "";
    }

    /**
     * 将时间戳转换为String,并根据时间判断显示样式
     *
     * @param selTimeMillis
     * @return
     * @throws Exception
     */
    public static String transformTime(long dateTimeLong) throws Exception {
       /* long currentTimeMillis = System.currentTimeMillis();
        long differenceTime = currentTimeMillis - selTimeMillis;

        String timeStr;
        if (differenceTime < oneMinuteLong) {
            //一分内
            timeStr = differenceTime / oneSecondLong + "秒前";
        } else if (differenceTime < oneHourLong) {
            //一小时内
            timeStr = differenceTime / oneMinuteLong + "分钟前";
        } else if (dateIsWithinDay(selTimeMillis, 1)) {
            //一天内
            timeStr = differenceTime / oneHourLong + "小时前";
        } else if (dateIsWithinDay(selTimeMillis, 2)) {
            //两天内
            timeStr = "昨天 " + transformTime(selTimeMillis, TYPE_HM);
        } else if (dateInWithinYear(selTimeMillis)) {
            //一年内
            timeStr = transformTime(selTimeMillis, TYPE_MDHM);
        } else {
            //一年前
            timeStr = transformTime(selTimeMillis, TYPE_YMDHM);
        }*/
        long currentTimeMillis = System.currentTimeMillis();
        long compareDifference = (currentTimeMillis - dateTimeLong) / 1000;

        String dateTransform;
        if (compareDifference >= 0 && compareDifference < 60) {
            dateTransform = "刚刚";
        } else if (compareDifference >= (60) && compareDifference < (60 * 60)) { //相差小时
            dateTransform = (compareDifference / 60) + "分钟前";
        } else if (differenceDay(currentTimeMillis, dateTimeLong) == 0) {
            //相差小时compareDifference >= (60 * 60) && compareDifference < (60 * 60 * 24)
            dateTransform = "今天 " + transformTime(dateTimeLong, TYPE_HM);
        } else if (differenceDay(currentTimeMillis, dateTimeLong) == 1) {//相差天
            dateTransform = "昨天 " + transformTime(dateTimeLong, TYPE_HM);
        } else {
            dateTransform = transformTime(dateTimeLong, TYPE_MDHM);
        }

        return dateTransform;
    }

    /**
     * 选择的时间是否在当前时间的正负天数内
     * parameter1 传入的比对值比 parameter2多几天或者少几天
     *
     * @return
     */
    public static int differenceDay(long parameter1, long parameter2) throws Exception {
        String parameterYmd1 = DateFormat.format(TYPE_YMD, parameter1).toString();
        String parameterYmd2 = DateFormat.format(TYPE_YMD, parameter2).toString();

        SimpleDateFormat format = new SimpleDateFormat(TYPE_YMD);
        long time1 = format.parse(parameterYmd1).getTime();
        long time2 = format.parse(parameterYmd2).getTime();

        return (int) ((time1 - time2) / (60 * 60 * 24 * 1000));
    }

    /**
     * 选择的时间是否在当前时间的正负天数内
     *
     * @param selTimeMillis 要比较的时间
     * @param daycount      正负天数
     * @return
     */
    public static boolean dateIsWithinDay(long selTimeMillis, int daycount) throws Exception {
        long currentTimeMillis = System.currentTimeMillis();
        return selTimeMillis >= currentTimeMillis - oneDayLong * daycount &&
                selTimeMillis <= currentTimeMillis + oneDayLong * daycount;
    }

    /**
     * 选择的时间是否在本周内
     *
     * @param selTimeMillis 要比较的时间
     * @return
     */
    public static boolean dateInWithinWeek(long selTimeMillis) throws Exception {
        int dayOfWeek = getWeekOfDate(new Date(selTimeMillis));//当前时间为本周的第几天
        long mondayMillis = getMondayTimeMillis(selTimeMillis, oneDayLong, dayOfWeek);
        long weekdayMillis = getWeekdayTimeMillis(selTimeMillis, oneDayLong, dayOfWeek);
        return mondayMillis <= selTimeMillis && selTimeMillis <= weekdayMillis;
    }

    /**
     * 选中时间是否在本年中
     *
     * @param selTimeMillis
     * @return
     */
    public static boolean dateInWithinYear(long selTimeMillis) throws Exception {
        return selTimeMillis > getYearStart(selTimeMillis);
    }

    /**
     * 当前周开始时间
     *
     * @param selTimeMillis
     * @return
     */
    public static long getMondayTimeMillis(long selTimeMillis, long oneDayLong, int dayOfWeek) throws Exception {
        return selTimeMillis - getDayStartTimeMillis(selTimeMillis, 0) - oneDayLong * dayOfWeek;
    }

    /**
     * 当前周结束时间
     *
     * @param selTimeMillis
     * @return
     */
    public static long getWeekdayTimeMillis(long selTimeMillis, long oneDayLong, int dayOfWeek) throws Exception {
        return selTimeMillis + (7 - dayOfWeek) * oneDayLong + getDayStartTimeMillis(selTimeMillis, 1);
    }

    /**
     * 获取凌晨时间
     *
     * @param selTimeMillis
     * @param type          0 00:00:00
     *                      1 23:59:59
     * @return
     */
    public static long getDayStartTimeMillis(long selTimeMillis, int type) throws Exception {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(selTimeMillis));
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        //时分秒（毫秒数）
        long millisecond = hour * oneHourLong + minute * oneMinuteLong + second * oneSecondLong;
        //凌晨00:00:00
        cal.setTimeInMillis(cal.getTimeInMillis() - millisecond);

        if (type == 0) {
            return cal.getTime().getTime();
        } else if (type == 1) {
            //凌晨23:59:59
            cal.setTimeInMillis(cal.getTimeInMillis() + 23 * oneHourLong + 59 * oneMinuteLong + 59 * oneSecondLong);
        }
        return cal.getTime().getTime();
    }

    /**
     * 获取当前日期是为当前周第几天
     *
     * @param dt
     * @return 本周的第几天
     */
    public static int getWeekOfDate(Date dt) throws Exception {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);

        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;//周日为第一天，故减一,使周一为第一天
        if (w < 0) {
            w = 0;
        }
        return w;
    }

    /**
     * 当前年份开始时间戳
     *
     * @param selTimeMillis
     * @return
     */
    public static long getYearStart(long selTimeMillis) throws Exception {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(selTimeMillis));
        int year = cal.get(Calendar.YEAR);
        cal.set(year, 0, 1);
        return cal.getTime().getTime();
    }

    /**
     * 日期的相差天数
     *
     * @param selTimeMillis
     * @param currentTimeMillis
     * @return
     */
    public static int dateDifferenceCount(long selTimeMillis, long currentTimeMillis) throws Exception {
        long selStartTimeMillis = getDayStartTimeMillis(selTimeMillis, 0);
        long currentStartTimeMillis = getDayStartTimeMillis(currentTimeMillis, 0);
        return (int) (Math.abs(currentStartTimeMillis - selStartTimeMillis) / oneDayLong);
    }

    /**
     * 获取当前时间字符串
     *
     * @param type
     * @return
     */
    public static String getTodayString(String type) throws Exception {
        long currentTimeMillis = System.currentTimeMillis();
        return transformTime(currentTimeMillis, type);
    }


    public static String getYMDWeek(String date) {

        SimpleDateFormat dateLongTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String formatWeek = "";
        try {
            Date parseTime = dateLongTime.parse(date);

            SimpleDateFormat dateFm = new SimpleDateFormat("yyyy-MM-dd EEEE");
            formatWeek = dateFm.format(parseTime);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formatWeek;
    }
}
