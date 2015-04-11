package com.omatt.canicoffee;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CalendarContract;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import static android.provider.CalendarContract.Events;

/**
 * Created by Omatt on 4/10/2015.
 */
public class CanICoffeeFragment extends Fragment {
    private final String TAG = "CanICoffeeFragment";
    private final String KEY_TIME_CURRENT = "time_current";
    private final String KEY_TIME_WAKE = "time_wake";
    private final String KEY_TIME_COFFEE_1 = "time_coffee_1";
    private final String KEY_TIME_COFFEE_2 = "time_coffee_2";
    private TextView mTextViewCurrentTime, mTextViewWakeTimeVal, mTextViewCoffeeTime1, mTextViewCoffeeTime2;
    private int currentHour, currentMinute, currentSecond;
    private int coffeeTime1startHour, coffeeTime2startHour, coffeeTimeMinute;

    private Timer mTimer;
    private Handler mTimerHandler = new Handler();

    public CanICoffeeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mTextViewCurrentTime = (TextView) rootView.findViewById(R.id.tv_time);
        TextView mTextViewWakeTime = (TextView) rootView.findViewById(R.id.tv_wake_time);
        mTextViewWakeTimeVal = (TextView) rootView.findViewById(R.id.tv_wake_time_val);
        mTextViewCoffeeTime1 = (TextView) rootView.findViewById(R.id.tv_coffee_time_1);
        mTextViewCoffeeTime2 = (TextView) rootView.findViewById(R.id.tv_coffee_time_2);

        mTextViewWakeTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getActivity(), TimePickerDialog.THEME_DEVICE_DEFAULT_LIGHT, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mTextViewWakeTimeVal.setText(getAmPm(hourOfDay, minute));
                        coffeeTime1startHour = fixExcessHour(hourOfDay + 3);
                        coffeeTime2startHour = fixExcessHour(hourOfDay + 9);
                        coffeeTimeMinute = minute;

                        mTextViewCoffeeTime1.setText(getString(R.string.txt_coffee_cycle_1)
                                + "\n" + getAmPm(hourOfDay + 3, minute) + " " + getString(R.string.txt_time_till) + " " + getAmPm(hourOfDay + 5, minute));
                        mTextViewCoffeeTime2.setText(getString(R.string.txt_coffee_cycle_2)
                                + "\n" + getAmPm(hourOfDay + 9, minute) + " " + getString(R.string.txt_time_till) + " " + getAmPm(hourOfDay + 11, minute));
                    }
                }, currentHour, currentMinute, false);
                mTimePicker.show();
            }
        });

        FloatingActionButton mFabRemindCoffeeTime1 = (FloatingActionButton) rootView.findViewById(R.id.fab_remind_coffee_time_1);
        FloatingActionButton mFabRemindCoffeeTime2 = (FloatingActionButton) rootView.findViewById(R.id.fab_remind_coffee_time_2);
        mFabRemindCoffeeTime1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCalendarReminder(true);
            }
        });
        mFabRemindCoffeeTime2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCalendarReminder(false);
            }
        });

        if (savedInstanceState != null) {
            mTextViewCurrentTime.setText(savedInstanceState.getString(KEY_TIME_CURRENT));
            mTextViewWakeTimeVal.setText(savedInstanceState.getString(KEY_TIME_WAKE));
            mTextViewCoffeeTime1.setText(savedInstanceState.getString(KEY_TIME_COFFEE_1));
            mTextViewCoffeeTime2.setText(savedInstanceState.getString(KEY_TIME_COFFEE_2));
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(KEY_TIME_CURRENT, mTextViewCurrentTime.getText().toString());
        savedInstanceState.putString(KEY_TIME_WAKE, mTextViewWakeTimeVal.getText().toString());
        savedInstanceState.putString(KEY_TIME_COFFEE_1, mTextViewCoffeeTime1.getText().toString());
        savedInstanceState.putString(KEY_TIME_COFFEE_2, mTextViewCoffeeTime2.getText().toString());
    }

    @Override
    public void onStart() {
        Log.i(TAG, "onStart");
        super.onStart();
        startTimer();
    }

    @Override
    public void onPause() {
        Log.i(TAG, "onPause");
        super.onPause();
        stopTimer();
    }

    private String getAmPm(int hour, int minute) {
        Log.i(TAG, "hh" + hour + "mm" + minute);
        if (hour >= 12 && hour <= 24) {
            // 24H afternoon/evening
            if (hour >= 12 && hour < 24) {
                if (hour != 12) hour -= 12;
                return goodTime(hour) + ":" + goodTime(minute) + " PM";
            } else {
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

    private int fixExcessHour(int hour) {
        if (hour > 24) hour -= 24;
        return hour;
    }

    private String goodTime(int hourMin) {
        if (hourMin < 10) {
            return "0" + hourMin;
        }
        return "" + hourMin;
    }

    private void startTimer() {
        mTimer = new Timer();
        TimerTask mTimerTask = new TimerTask() {
            public void run() {
                mTimerHandler.post(new Runnable() {
                    public void run() {
                        Calendar mCalendar = Calendar.getInstance();
                        currentHour = mCalendar.get(Calendar.HOUR);
                        currentMinute = mCalendar.get(Calendar.MINUTE);
                        currentSecond = mCalendar.get(Calendar.SECOND);
                        mTextViewCurrentTime.setText(getString(R.string.txt_current_time) + " "
                                + goodTime(currentHour) + ":" + goodTime(currentMinute) + ":" + goodTime(currentSecond) + " "
                                + ((mCalendar.get(Calendar.AM_PM) == Calendar.AM) ? "AM" : "PM"));
//                        Log.i(TAG, "Time: " + goodTime(currentHour) + ":" + goodTime(currentMinute) + ":" + goodTime(currentSecond));
                    }
                });
            }
        };
        mTimer.schedule(mTimerTask, 1000, 1000);
    }

    private void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
        }
    }

    private void setCalendarReminder(boolean isFirsCoffeeCycle) {
        if (!mTextViewWakeTimeVal.getText().toString().equals("")) {
            Intent intent = new Intent(Intent.ACTION_EDIT);
            intent.setType("vnd.android.cursor.item/event");
            intent.putExtra(Events.EVENT_TIMEZONE, TimeZone.getDefault());
            if (isFirsCoffeeCycle) {
                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, getCurrentTimeMillis(coffeeTime1startHour, coffeeTimeMinute));
                intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, getCurrentTimeMillis(coffeeTime1startHour, coffeeTimeMinute) + (2 * 60L * 60L * 1000L));
                intent.putExtra(Events.TITLE, getString(R.string.txt_coffee_cycle_1));
            } else {
                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, getCurrentTimeMillis(coffeeTime2startHour, coffeeTimeMinute));
                intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, getCurrentTimeMillis(coffeeTime2startHour, coffeeTimeMinute) + (2 * 60L * 60L * 1000L));
                intent.putExtra(Events.TITLE, getString(R.string.txt_coffee_cycle_2));
            }
            intent.putExtra(Events.ALL_DAY, false);
            startActivity(intent);
        } else {
            Toast.makeText(getActivity(), getString(R.string.txt_toast_empty_time), Toast.LENGTH_SHORT).show();
        }
    }

    private long getCurrentTimeMillis(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        return calendar.getTimeInMillis();
    }
}
