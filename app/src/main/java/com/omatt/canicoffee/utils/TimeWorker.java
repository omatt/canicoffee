package com.omatt.canicoffee.utils;

import android.util.Log;

import com.omatt.canicoffee.utils.models.Time;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by Omatt on 4/11/2015.
 */
public class TimeWorker {
    private final String TAG = "TimeWorker";

    public int getHour(String time, boolean isHour) {
        String splitTime[] = time.split(":");
        if (isHour) {
            // Return hour
            return Integer.parseInt(splitTime[0]);
        } else {
            // Return minute
            return Integer.parseInt(splitTime[1]);
        }
    }

    /**
     * Get fixed hours and minutes
     *
     * @param hour    int
     * @param minutes int
     * @return Time
     */
    public Time getTimeHourMin(long hour, long minutes) {
        long millis = TimeWorker.getHourMinMillis(hour, minutes);
        long newHour = TimeUnit.MILLISECONDS.toHours(millis);
        long newMinute = TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(newHour);
        Time time = new Time();
        time.setHour(newHour);
        time.setMinutes(newMinute);

        return time;
    }

    public String getHourMin(int hour, int minute) {
        long millis = TimeWorker.getHourMinMillis(hour, minute);
        long newHour = TimeUnit.MILLISECONDS.toHours(millis);
        long newMinute = TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(newHour);
        return newHour + ":" + newMinute;
    }

    private static long getHourMinMillis(int hour, int minute) {
        return hour * 60L * 60L * 1000L + minute * 60L * 1000L;
    }

    private static long getHourMinMillis(long hour, long minute) {
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

    /**
     * Fixes time >24H
     * @param hour int
     * @param minute int
     * @return Time
     */
    public Time getFixedAmPm(long hour, long minute) {
        Log.i(TAG, "getFixedAmPm " + hour + "H" + minute + "M");
        Time time = new Time();
        if (hour >= 12 && hour <= 24) {
            // 24H afternoon/evening
            if (hour >= 12 && hour < 24) {
                // AlarmClock requires 24H time
                time.setHour(hour);
                time.setMinutes(minute);
                return time;
            } else {
                // 12mn morning
                hour -= 12;
                time.setHour(hour);
                time.setMinutes(minute);
                return time;
            }
        } else if (hour < 12) {
            // 24H morning
            if (hour == 0) hour = 12;
            time.setHour(hour);
            time.setMinutes(minute);
            return time;
        } else {
            // Calculate num exceeding 24H
            hour -= 24;
            time.setHour(hour);
            time.setMinutes(minute);
            return time;
        }
    }

    public int fixExcessHour(int hour) {
        if (hour > 24) hour -= 24;
        return hour;
    }

    public String goodTime(int hourMin) {
        if (hourMin < 10) {
            return "0" + hourMin;
        }
        return "" + hourMin;
    }
}
