package com.ysxsoft.deliverylocker_big.utils;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    private static Calendar calendar;

    private final static long TIME_MINUTE = 60 * 1000;// 1分钟
    private final static long TIME_HOUR = 60 * TIME_MINUTE;// 1小时
    private final static long TIME_DAY = 24 * TIME_HOUR;// 1天

    private static final String DAYTIME = "06:00:00";
    private static final String UNDAYTIME = "20:00:00";

    private static Calendar getCalendar() {
        if (calendar == null) {
            calendar = Calendar.getInstance();
        }
        return calendar;
    }

    /**
     * 是否是白天
     *
     * @return true 是白天 false 晚上
     */
    public static boolean isDayTime() {
        long dayTime = getDayTime();
        long unDayTime = getUnDayTime();
        long time = System.currentTimeMillis();
        if (time > dayTime && time < unDayTime) {//白天
            return true;
        }
        return false;
    }

    /**
     * 当天早上的时间
     * @return
     */
    public static long getDayTime() {
        String time = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()));
        String getlong = time + " " + DAYTIME;
        Log.e("getDayTime", getlong);
        return DateTimeUtil.parseLong(getlong)*1000;
    }
    /**
     * 获取第二天早上的时间
     * @return
     */
    public static long getTomorrowDayTime() {
        String time = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()));
        String getlong = time + " " + DAYTIME;
        Log.e("getDayTime", getlong);
        return DateTimeUtil.parseLong(getlong)*1000+ TIME_DAY;
    }
    public static long getUnDayTime() {
        String time = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()));
        String getlong = time + " " + UNDAYTIME;
        Log.e("getUnDayTime", getlong);
        return DateTimeUtil.parseLong(getlong)*1000;
    }

    /**
     * 获取
     */
    public static String getTimer(int hour){
        return DateTimeUtil.formatDateTime(System.currentTimeMillis()+ hour*TIME_HOUR, "yyyy-MM-dd HH:mm");
    }
    /**
     * 获取
     */
    public static long getTimerLong(int hour){
        return System.currentTimeMillis()+ hour*TIME_HOUR;
    }
    /**
     * 获取
     */
    public static String getDay(int day){
        return DateTimeUtil.formatDateTime(System.currentTimeMillis()+ day*TIME_DAY, "yyyy-MM-dd HH:mm");
    }
    /**
     * 获取
     */
    public static long getDayLong(int day){
        return System.currentTimeMillis()+ day*TIME_DAY;
    }
}
