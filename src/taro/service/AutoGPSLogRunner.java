package taro.service;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import taro.app.logger.gps.auto.CurrentDateFileName;
import taro.app.logger.gps.auto.LogInfo;
import taro.app.logger.gps.auto.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.os.Handler;
import android.util.Log;

public class AutoGPSLogRunner implements Runnable {
	
	private final Context mContext;
	private final GPSListener mGPSListener;
	private final Handler mHandler;
	private final long mIntervalMillis;
	private final int mHistorySize;
	private final Location[] mLocations;
	private final LogInfo mLogInfo;
	private final CurrentDateFileName mCurrentDateFileName;
	private Location mLocation = null;
	private double mDistance = 0;
	private double mSpeed = 0;
	private int mCounter = 0;
	private boolean mIsLogging = false;
	private static final String TAG = "AutoGPSLogRunner";

	public AutoGPSLogRunner(Context context, GPSListener listener, Handler handler) {
		mContext = context;
		mGPSListener = listener;
		mHandler = handler;
		
		Resources res = context.getResources();
		mIntervalMillis = res.getInteger(R.integer.runner_interval_millis);
		mHistorySize = res.getInteger(R.integer.runner_history_size);
		mLocations = new Location[mHistorySize];
		mLogInfo = new LogInfo(mContext);
		mCurrentDateFileName = new CurrentDateFileName(mLogInfo.getValuePrefix());
	}

	public double getDistance() {
		return mDistance;
	}
	
	public double getSpeed() {
		return mSpeed;
	}

	@Override
	public void run() {
		do {
			mLocation = mGPSListener.getLocation();
			if (null == mLocation) { break; }

			logAutomatically();
			updateHistory();
			
		}while(false);
		mHandler.postDelayed(this, mIntervalMillis);
	}

	private void updateHistory() {
		Location dst = mLocations[mCounter];
		if (null == dst) {
			mLocations[mCounter] = new Location(mLocation);
		}else{
			dst.set(mLocation);
		}
		mCounter++;
		if (mCounter == mHistorySize) {
			mCounter = 0;
		}
	}

	private void logAutomatically() {

		for (Location location : this.mLocations) {
			if (null == location) { return; }
		}
		
		int countHasSpeed = 0;
		for (Location location : this.mLocations) {
			if (1 < location.getSpeed()) {
				// 1 m/s 以上
				countHasSpeed++;
			}
		}
		Location previous = getPrevious();
		mSpeed = mLocation.distanceTo(previous) * 3600.0 / (Float.MIN_VALUE + mLocation.getTime() - previous.getTime());
		Log.i(TAG , "countHasSpeed: " + String.valueOf(countHasSpeed));
		Log.i(TAG , "mSpeed: " + String.valueOf(mSpeed));
		if (!mIsLogging) {
			if (mLocations.length == countHasSpeed) {
				mIsLogging = true;
				for (int i = 0; (i + 1) < mLocations.length; ++i) {
					mDistance += mLocations[i].distanceTo(mLocations[i + 1]);
				}
			}			
		}else{
			if (0 == countHasSpeed) {
				mIsLogging = false;
				mDistance = 0;
				mSpeed = 0;
			}else{
				mDistance += previous.distanceTo(mLocations[mCounter]);
			}
		}
		
		if (mIsLogging) {
			Intent intent = new Intent(mContext, LogIntentService.class);
			intent.putExtra(mLogInfo.getKeyFilename(), mCurrentDateFileName.getFileName());
			intent.putExtra(mLogInfo.getKeyContents(), makeText());
			intent.putExtra(mLogInfo.getKeyAppend(), true);
			mContext.startService(intent);
		}		
	}

	private Location getPrevious() {
		return mLocations[(mLocations.length + mCounter - 1) % mLocations.length];
	}
	

	private String makeText() {
		NumberFormat df = NumberFormat.getInstance();
		df.setMaximumFractionDigits(7);

		Date date = new Date(mLocation.getTime());
		SimpleDateFormat sdf = new SimpleDateFormat("HH-mm-ss", Locale.JAPAN);

		String text = "lat: " + mLocation.getLatitude();
		text += "  long: " + mLocation.getLongitude();
		text += "  alt: " + df.format(mLocation.getAltitude());
		text += "  spd: " + df.format(mLocation.getSpeed() * 3.6);
//		text += "  spd2[km/h]: " + df.format(mSpeed);
		text += "  time: " + sdf.format(date);
		text += "  dist: " + df.format(mDistance);
		text += "\n";
		return text;
	}
	
}
