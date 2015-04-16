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
import com.omatt.canicoffee.utils.TimeWorker;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

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
    private int coffeeTimeHour, coffeeTimeMinute;

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
                        TimeWorker mTimeWorker = new TimeWorker();
                        mTextViewWakeTimeVal.setText(mTimeWorker.getAmPm(hourOfDay, minute));
                        coffeeTimeHour = mTimeWorker.fixExcessHour(hourOfDay);
                        coffeeTimeMinute = minute;

                        String coffeeTime1start = TimeWorker.getHourMin(hourOfDay + 3, minute + 30);
                        String coffeeTime1end = TimeWorker.getHourMin(hourOfDay + 5, minute + 30);
                        String coffeeTime2start = TimeWorker.getHourMin(hourOfDay + 7, minute + 30);
                        String coffeeTime2end = TimeWorker.getHourMin(hourOfDay + 11, minute);

                        mTextViewCoffeeTime1.setText(getString(R.string.txt_coffee_cycle_1)
                                + "\n" + mTimeWorker.getAmPm(TimeWorker.getHour(coffeeTime1start, true), TimeWorker.getHour(coffeeTime1start, false))
                                + " " + getString(R.string.txt_time_till)
                                + " " + mTimeWorker.getAmPm(TimeWorker.getHour(coffeeTime1end, true), TimeWorker.getHour(coffeeTime1end, false)));
                        mTextViewCoffeeTime2.setText(getString(R.string.txt_coffee_cycle_2)
                                + "\n" + mTimeWorker.getAmPm(TimeWorker.getHour(coffeeTime2start, true), TimeWorker.getHour(coffeeTime2start, false))
                                + " " + getString(R.string.txt_time_till)
                                + " " + mTimeWorker.getAmPm(TimeWorker.getHour(coffeeTime2end, true), TimeWorker.getHour(coffeeTime2end, false)));
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
    public void onStop() {
        Log.i(TAG, "onStop");
        super.onStop();
        stopTimer();
    }

    // Start Timer thread
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
                        if(currentHour == 0) currentHour = 12;
                        mTextViewCurrentTime.setText(getString(R.string.txt_current_time) + " "
                                + TimeWorker.goodTime(currentHour) + ":" + TimeWorker.goodTime(currentMinute) + ":" + TimeWorker.goodTime(currentSecond) + " "
                                + ((mCalendar.get(Calendar.AM_PM) == Calendar.AM) ? "AM" : "PM"));
//                        Log.i(TAG, "Time: " + goodTime(currentHour) + ":" + goodTime(currentMinute) + ":" + goodTime(currentSecond));
                    }
                });
            }
        };
        mTimer.schedule(mTimerTask, 1000, 1000);
    }

    // Stop Timer thread
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
                // First coffee cycle
                // wake hour + 3H (2H time frame)
                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, TimeWorker.getCurrentTimeMillis(coffeeTimeHour, coffeeTimeMinute)+ (3 * 60L * 60L * 1000L) + (30 * 60L * 1000L));
                intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, TimeWorker.getCurrentTimeMillis(coffeeTimeHour, coffeeTimeMinute) + (5 * 60L * 60L * 1000L) + (30 * 60L * 1000L));
                intent.putExtra(Events.TITLE, getString(R.string.txt_remind_coffee_time_1));
            } else {
                // wake hour + 9H (2H time frame)
                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, TimeWorker.getCurrentTimeMillis(coffeeTimeHour, coffeeTimeMinute) + (7 * 60L * 60L * 1000L) + (30 * 60L * 1000L));
                intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, TimeWorker.getCurrentTimeMillis(coffeeTimeHour, coffeeTimeMinute) + (11 * 60L * 60L * 1000L));
                intent.putExtra(Events.TITLE, getString(R.string.txt_remind_coffee_time_2));
            }
            intent.putExtra(Events.ALL_DAY, false);
            startActivity(intent);
        } else {
            Toast.makeText(getActivity(), getString(R.string.txt_toast_empty_time), Toast.LENGTH_SHORT).show();
        }
    }
}
