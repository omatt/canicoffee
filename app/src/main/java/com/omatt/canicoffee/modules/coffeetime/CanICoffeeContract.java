package com.omatt.canicoffee.modules.coffeetime;

import android.content.Context;
import android.content.Intent;

import com.omatt.canicoffee.BasePresenter;
import com.omatt.canicoffee.BaseView;
import com.omatt.canicoffee.modules.MainActivity;
import com.omatt.canicoffee.utils.models.Time;

/**
 * Created by omarmatthew on 9/10/2017.
 */

class CanICoffeeContract {
    interface View extends BaseView<Presenter> {

        void updateTimeCurrent(String time);

        void updateTimeWake(String time);

        String getTimeWake();

        void updateCoffeeTime1(String time);

        void updateCoffeeTime2(String time);

        void setTime(Time time);

        Time getTime();
    }

    interface Presenter extends BasePresenter<View> {
        void startTimer(Context context);

        void stopTimer(Context context);

        void setTimeWake(Context context);

        void setCoffeeReminder(MainActivity mainActivity, boolean isFirstCoffeeCycle);

        void processDeepLink(MainActivity mainActivity, Intent intent);
    }
}
