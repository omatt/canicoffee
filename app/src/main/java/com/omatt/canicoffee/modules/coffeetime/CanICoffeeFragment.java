package com.omatt.canicoffee.modules.coffeetime;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.omatt.canicoffee.modules.MainActivity;
import com.omatt.canicoffee.R;
import com.omatt.canicoffee.utils.GlobalValues;
import com.omatt.canicoffee.utils.models.Time;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Omatt on 4/10/2015.
 * Can I Coffee Fragment
 */
public class CanICoffeeFragment extends Fragment implements CanICoffeeContract.View {
    private final String TAG = "CanICoffeeFragment";

    @BindView(R.id.tv_time)
    TextView mTextViewCurrentTime;
    @BindView(R.id.tv_wake_time_val)
    TextView mTextViewWakeTimeVal;
    @BindView(R.id.tv_coffee_time_1)
    TextView mTextViewCoffeeTime1;
    @BindView(R.id.tv_coffee_time_2)
    TextView mTextViewCoffeeTime2;

    CanICoffeePresenter canICoffeePresenter = new CanICoffeePresenter();
    MainActivity mainActivity;
    Time time;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);
        mainActivity = (MainActivity) getActivity();

        if (savedInstanceState != null) {
            updateTimeCurrent(savedInstanceState.getString(GlobalValues.KEY_TIME_CURRENT));
            updateTimeWake(savedInstanceState.getString(GlobalValues.KEY_TIME_WAKE));
            updateCoffeeTime1(savedInstanceState.getString(GlobalValues.KEY_TIME_COFFEE_1));
            updateCoffeeTime2(savedInstanceState.getString(GlobalValues.KEY_TIME_COFFEE_2));
            if (savedInstanceState.containsKey(GlobalValues.KEY_TIME_WAKE))
                setTime(savedInstanceState.getParcelable(GlobalValues.KEY_TIME));
        }

        return rootView;
    }

    @OnClick(R.id.btn_wake_time)
    void onClickWakeTime() {
        canICoffeePresenter.setTimeWake(getActivity());
    }

    @OnClick(R.id.fab_remind_coffee_time_1)
    void onClickCoffeeTime1() {
        canICoffeePresenter.setCoffeeReminder(mainActivity, true);
    }

    @OnClick(R.id.fab_remind_coffee_time_2)
    void onClickCoffeeTime2() {
        canICoffeePresenter.setCoffeeReminder(mainActivity, false);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(GlobalValues.KEY_TIME_CURRENT, mTextViewCurrentTime.getText().toString());
        savedInstanceState.putString(GlobalValues.KEY_TIME_WAKE, mTextViewWakeTimeVal.getText().toString());
        savedInstanceState.putString(GlobalValues.KEY_TIME_COFFEE_1, mTextViewCoffeeTime1.getText().toString());
        savedInstanceState.putString(GlobalValues.KEY_TIME_COFFEE_2, mTextViewCoffeeTime2.getText().toString());
        savedInstanceState.putParcelable(GlobalValues.KEY_TIME, time);
    }

    @Override
    public void onStart() {
        Log.i(TAG, "onStart");
        super.onStart();
        canICoffeePresenter.startTimer(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        canICoffeePresenter.takeView(this);
    }

    @Override
    public void onDestroy() {
        canICoffeePresenter.dropView();
        super.onDestroy();
    }

    @Override
    public void onStop() {
        Log.i(TAG, "onStop");
        canICoffeePresenter.stopTimer(getActivity());
        super.onStop();
    }

    @Override
    public MainActivity getActivityContent() {
        return mainActivity;
    }

    @Override
    public void updateTimeCurrent(String time) {
        mTextViewCurrentTime.setText(time);
    }

    @Override
    public void updateTimeWake(String time) {
        mTextViewWakeTimeVal.setText(time);
    }

    @Override
    public String getTimeWake() {
        return mTextViewWakeTimeVal.getText().toString();
    }

    @Override
    public void updateCoffeeTime1(String time) {
        mTextViewCoffeeTime1.setText(time);
    }

    @Override
    public void updateCoffeeTime2(String time) {
        mTextViewCoffeeTime2.setText(time);
    }

    @Override
    public void setTime(Time time) {
        this.time = time;
    }

    @Override
    public Time getTime() {
        return time;
    }
}
