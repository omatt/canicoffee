package com.omatt.canicoffee;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

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

    private Timer mTimer;
    private TimerTask mTimerTask;
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
                        mTextViewCoffeeTime1.setText(getString(R.string.txt_coffee_cycle_1)
                                + "\n" + getAmPm(hourOfDay + 3, minute) + " " + getString(R.string.txt_time_till) + " " + getAmPm(hourOfDay + 5, minute));
                        mTextViewCoffeeTime2.setText(getString(R.string.txt_coffee_cycle_2)
                                + "\n" + getAmPm(hourOfDay + 9, minute) + " " + getString(R.string.txt_time_till) + " " + getAmPm(hourOfDay + 11, minute));
                    }
                }, currentHour, currentMinute, false);
                mTimePicker.show();
            }
        });

        // Using TimerTask
        startTimer();

        if(savedInstanceState != null){
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
    public void onDestroy() {
        super.onDestroy();
        stopTimer();
    }

    private String getAmPm(int hour, int minute) {
        if (hour >= 12 && hour <= 24) {
            // 24H afternoon/evening
            if(hour != 12) hour -= 12;
            return goodTime(hour) + ":" + goodTime(minute) + " PM";
        } else if (hour < 12) {
            // 24H morning
            return goodTime(hour) + ":" + goodTime(minute) + " AM";
        } else {
            // Calculate num exceeding 24H
            hour -= 24;
            return goodTime(hour) + ":" + goodTime(minute) + " AM";
        }
    }

    private String goodTime(int hourMin) {
        if (hourMin < 10) {
            return "0" + hourMin;
        }
        return "" + hourMin;
    }

    private void startTimer() {
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            public void run() {
                mTimerHandler.post(new Runnable() {
                    public void run() {
                        Calendar mCalendar = Calendar.getInstance();
                        currentHour = mCalendar.get(Calendar.HOUR);
                        currentMinute = mCalendar.get(Calendar.MINUTE);
                        currentSecond = mCalendar.get(Calendar.SECOND);
                        mTextViewCurrentTime.setText(getString(R.string.txt_current_time) + " " + goodTime(currentHour) + ":" + goodTime(currentMinute) + ":" + goodTime(currentSecond));
                        Log.i(TAG, "Time: " + goodTime(currentHour) + ":" + goodTime(currentMinute) + ":" + goodTime(currentSecond));
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
}
