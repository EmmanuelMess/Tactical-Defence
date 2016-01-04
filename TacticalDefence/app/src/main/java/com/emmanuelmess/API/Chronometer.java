package com.emmanuelmess.API;

import android.os.Parcel;
import android.os.Parcelable;

import static android.os.SystemClock.elapsedRealtime;

public class Chronometer implements Parcelable {

	public static final Parcelable.Creator<Chronometer> CREATOR = new Parcelable.Creator<Chronometer>() {
		@Override
		public Chronometer createFromParcel(Parcel source) {
			return new Chronometer(source);  //using parcelable constructor
		}

		@Override
		public Chronometer[] newArray(int size) {
			return new Chronometer[size];
		}
	};
	private long startTime = -1;
	private long pauseTime = -1;

	public Chronometer() {
	}

	//parcel part
	public Chronometer(Parcel in) {
		long[] data = new long[2];
		in.readLongArray(data);
		startTime = data[0];
		pauseTime = data[1];
	}

	public void start() {
		startTime = elapsedRealtime();
		pauseTime = -1;
	}

	public void pause() {
		if (!isPaused()) pauseTime = elapsedRealtime();
	}

	public void resume() {
		startTime += elapsedRealtime() - pauseTime;
		pauseTime = -1;
	}

	public void stop() {
		startTime = -1;
	}

	public long getElapsedTime() {
		if (isPaused())
			return pauseTime - startTime;

		return elapsedRealtime() - startTime;
	}

	public void setElapsedTime(long startTime) {
		this.startTime = startTime;
	}

	public boolean hasStarted() {
		return startTime != -1;
	}

	public boolean isPaused() {
		return pauseTime != -1;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLongArray(new long[]{startTime, pauseTime});
	}
}