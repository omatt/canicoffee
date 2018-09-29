package com.omatt.canicoffee.modules.coffeetime;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.provider.AlarmClock;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.appinvite.FirebaseAppInvite;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.omatt.canicoffee.R;
import com.omatt.canicoffee.modules.MainActivity;
import com.omatt.canicoffee.utils.GlobalValues;
import com.omatt.canicoffee.utils.TimeWorker;
import com.omatt.canicoffee.utils.models.Time;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by omarmatthew on 9/10/2017.
 * Can I Coffee business logic
 */

public class CanICoffeePresenter implements CanICoffeeContract.Presenter {
    private final String TAG = "CanICoffeePresenter";

    private int currentHour, currentMinute, currentSecond;

    private CanICoffeeContract.View canICoffeeView;
    private TimeWorker mTimeWorker = new TimeWorker();

    private Timer mTimer;

    @Override
    public void takeView(CanICoffeeContract.View view) {
        canICoffeeView = view;
    }

    @Override
    public void dropView() {
        canICoffeeView = null;
    }

    @Override
    public void startTimer(Context context) {
        Handler mTimerHandler = new Handler();
        mTimer = new Timer();
        TimerTask mTimerTask = new TimerTask() {
            public void run() {
                mTimerHandler.post(() -> {
                    Calendar mCalendar = Calendar.getInstance();
                    currentHour = mCalendar.get(Calendar.HOUR);
                    currentMinute = mCalendar.get(Calendar.MINUTE);
                    currentSecond = mCalendar.get(Calendar.SECOND);
                    if (currentHour == 0) currentHour = 12;
                    String strCurrentTime = mTimeWorker.timeNow(context, mCalendar, currentHour, currentMinute, currentSecond);
                    canICoffeeView.updateTimeCurrent(strCurrentTime);
//                        Log.i(TAG, "Time: " + goodTime(currentHour) + ":" + goodTime(currentMinute) + ":" + goodTime(currentSecond));
                });
            }
        };
        mTimer.schedule(mTimerTask, 1000, 1000);
    }

    @Override
    public void stopTimer(Context context) {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
        }
    }

    @Override
    public void setTimeWake(Context context) {
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(context, TimePickerDialog.THEME_DEVICE_DEFAULT_LIGHT, (view, hourOfDay, minute) -> {
            canICoffeeView.updateTimeWake(mTimeWorker.getAmPm(hourOfDay, minute));

            Time time = new Time();
            time.setHour(hourOfDay);
            time.setMinutes(minute);
            canICoffeeView.setTime(time);

            String coffeeTime1start = mTimeWorker.getHourMin(hourOfDay + 3, minute + 30);
            String coffeeTime1end = mTimeWorker.getHourMin(hourOfDay + 5, minute + 30);
            String coffeeTime2start = mTimeWorker.getHourMin(hourOfDay + 7, minute + 30);
            String coffeeTime2end = mTimeWorker.getHourMin(hourOfDay + 11, minute);

            String strCoffeeTime1 = context.getString(R.string.txt_coffee_cycle_1)
                    + "\n" + mTimeWorker.getAmPm(mTimeWorker.getHour(coffeeTime1start, true), mTimeWorker.getHour(coffeeTime1start, false))
                    + " " + context.getString(R.string.txt_time_till)
                    + " " + mTimeWorker.getAmPm(mTimeWorker.getHour(coffeeTime1end, true), mTimeWorker.getHour(coffeeTime1end, false));
            String strCoffeeTime2 = context.getString(R.string.txt_coffee_cycle_2)
                    + "\n" + mTimeWorker.getAmPm(mTimeWorker.getHour(coffeeTime2start, true), mTimeWorker.getHour(coffeeTime2start, false))
                    + " " + context.getString(R.string.txt_time_till)
                    + " " + mTimeWorker.getAmPm(mTimeWorker.getHour(coffeeTime2end, true), mTimeWorker.getHour(coffeeTime2end, false));
            canICoffeeView.updateCoffeeTime1(strCoffeeTime1);
            canICoffeeView.updateCoffeeTime2(strCoffeeTime2);
        }, currentHour, currentMinute, false);
        mTimePicker.show();
    }

    @Override
    public void setCoffeeReminder(MainActivity mainActivity, boolean isFirstCoffeeCycle) {
        if (!canICoffeeView.getTimeWake().equals("")) {
            Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
//            Log.i(TAG, "setCoffeeReminder isFirstCoffeeCycle: " + isFirstCoffeeCycle);
            int hourOffset, minOffset;
            String message;
            if (isFirstCoffeeCycle) {
                hourOffset = 3;
                minOffset = 30;
                message = mainActivity.getString(R.string.txt_remind_coffee_time_1);
            } else {
                hourOffset = 7;
                minOffset = 30;
                message = mainActivity.getString(R.string.txt_remind_coffee_time_2);
            }
            Time time = canICoffeeView.getTime();
            Time coffeeTime = mTimeWorker.getTimeHourMin(time.getHour() + hourOffset, time.getMinutes() + minOffset);
            Time fixedCoffeeTime = mTimeWorker.getFixedAmPm(coffeeTime.getHour(), coffeeTime.getMinutes());
            int coffeeHour = (int) fixedCoffeeTime.getHour();
            int coffeeMinute = (int) fixedCoffeeTime.getMinutes();
            intent.putExtra(AlarmClock.EXTRA_HOUR, coffeeHour);
            intent.putExtra(AlarmClock.EXTRA_MINUTES, coffeeMinute);
//            Log.i(TAG, "setCoffeeReminder " + coffeeHour + "H" + coffeeMinute + "M");
            intent.putExtra(AlarmClock.EXTRA_MESSAGE, message);
            mainActivity.startActivity(intent);
        } else {
            Toast.makeText(mainActivity, mainActivity.getString(R.string.txt_toast_empty_time), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void processDeepLink(MainActivity mainActivity, Intent intent) {
        FirebaseDynamicLinks.getInstance().getDynamicLink(intent).addOnSuccessListener(data -> {
            if (data == null) {
                Log.d(TAG, "getInvitation: no data");
                return;
            }

            // Get the deep link
            Uri deepLink = data.getLink();
            Log.i(TAG, "Deep Link: " + deepLink);
            if (deepLink != null && deepLink.getBooleanQueryParameter("coffeetime", false)) {
                String coffeeTime = deepLink.getQueryParameter("coffeetime");
                try {
                    Calendar mCalendar = Calendar.getInstance();
                    mCalendar.setTimeInMillis(Long.parseLong(coffeeTime));
                    currentHour = mCalendar.get(Calendar.HOUR);
                    currentMinute = mCalendar.get(Calendar.MINUTE);
                    currentSecond = mCalendar.get(Calendar.SECOND);
                    if (currentHour == 0) currentHour = 12;
                    String strCurrentTime = mTimeWorker.timeNow(mainActivity, mCalendar, currentHour, currentMinute, currentSecond);
                    canICoffeeView.updateTimeCurrent(strCurrentTime);
                } catch (Exception e) {
                    Toast.makeText(mainActivity, mainActivity.getString(R.string.txt_toast_invalid_time), Toast.LENGTH_SHORT).show();
                    Crashlytics.logException(e);
                    Crashlytics.setString(GlobalValues.CRASH_LOG_TIME, coffeeTime);
                }
            }

            // Extract invite
            FirebaseAppInvite invite = FirebaseAppInvite.getInvitation(data);
            if (invite != null) {
                String invitationId = invite.getInvitationId();
                Log.i(TAG, "Invitation ID: " + invitationId);
            }
        }).addOnFailureListener(e -> {
            Log.w(TAG, "getDynamicLink:onFailure", e);
        });
    }
}
