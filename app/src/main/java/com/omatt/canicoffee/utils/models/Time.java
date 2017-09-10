package com.omatt.canicoffee.utils.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by omarmatthew on 9/10/2017.
 */

public class Time implements Parcelable{
    private long hour;
    private long minutes;
    private long seconds;

    public Time() {
    }

    public long getHour() {
        return hour;
    }

    public void setHour(long hour) {
        this.hour = hour;
    }

    public long getMinutes() {
        return minutes;
    }

    public void setMinutes(long minutes) {
        this.minutes = minutes;
    }

    public long getSeconds() {
        return seconds;
    }

    public void setSeconds(long seconds) {
        this.seconds = seconds;
    }

    private Time(Parcel read) {
        this.hour = read.readLong();
        this.minutes = read.readLong();
        this.seconds = read.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(this.hour);
        parcel.writeLong(this.minutes);
        parcel.writeLong(this.seconds);
    }

    public static final Parcelable.Creator<Time> CREATOR =
            new Parcelable.Creator<Time>() {
                @Override
                public Time createFromParcel(Parcel source) {
                    return new Time(source);
                }

                @Override
                public Time[] newArray(int size) {
                    return new Time[size];
                }
            };
}
