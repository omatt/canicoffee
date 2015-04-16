package com.omatt.canicoffee.utils;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by Omatt on 4/11/2015.
 */
public class TimeWorker {

    public static int getHour(String time, boolean isHour){
        String splitTime[] = time.split(":");
        if(isHour){
            // Return hour
            return Integer.parseInt(splitTime[0]);
        } else {
            // Return minute
            return Integer.parseInt(splitTime[1]);
        }
    }

    public static String getHourMin(int hour, int minute){
        long millis = TimeWorker.getHourMinMillis(hour, minute);
        long newHour = TimeUnit.MILLISECONDS.toHours(millis);
        long newMinute = TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(newHour);
        return newHour + ":" + newMinute;
    }

    public static long getHourMinMillis(int hour, int minute){
        return hour * 60L * 60L * 1000L + minute * 60L * 1000L;
    }

    public static long getCurrentTimeMillis(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        return calendar.getTimeInMillis();
    }

    public String getAmPm(int hour, int minute) {
        if (hour >= 12 && hour <= 24) {
            // 24H afternoon/evening
            if (hour >= 12 && hour < 24) {
                // 12nn (after)noon
                if (hour != 12) hour -= 12;
                return goodTime(hour) + ":" + goodTime(minute) + " PM";
            } else {
                // 12mn morning
                hour -= 12;
                return goodTime(hour) + ":" + goodTime(minute) + " AM";
            }
        } else if (hour < 12) {
            // 24H morning
            if (hour == 0) hour = 12;
            return goodTime(hour) + ":" + goodTime(minute) + " AM";
        } else {
            // Calculate num exceeding 24H
            hour -= 24;
            return goodTime(hour) + ":" + goodTime(minute) + " AM";
        }
    }

    public int fixExcessHour(int hour) {
        if (hour > 24) hour -= 24;
        return hour;
    }

    public static String goodTime(int hourMin) {
        if (hourMin < 10) {
            return "0" + hourMin;
        }
        return "" + hourMin;
    }
}
