package taro.app.logger.gps.auto;


import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import taro.service.AutoGPSLogService;
import taro.service.AutoGPSLogService.AutoGPSLogBinder;
import taro.service.LocationService;
import taro.service.LocationService.LocationBinder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.Settings;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;

/**
 * GPSのデータを表示する。
 *
 */
public class MonitorActivity extends Activity {

	private static final String TAG = "MonitorActivity";
	
	private TextView mAltitudeView;

	/** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
        	AutoGPSLogBinder binder = (AutoGPSLogBinder) service;
            mService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        	mService = null;
        }
    };

	private TextView mFromTextView = null;
	
	private Handler mHandler = new Handler();
	
	private int mIntervalMillis;
	private TextView mLatitudeView;
	
	private LogInfo mLogInfo;

    private TextView mLongitudeView;

	private AutoGPSLogService mService = null;

	private TextView mToTextView = null;
	
	private TextView mSpeedView = null;
	
	private Location mLocation = null;
	
	private double mDistance = 0;
	
	public MonitorActivity() {
		super();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_monitor);

		mIntervalMillis = getResources().getInteger(R.integer.view_interval_millis);
		
		Switch sw = (Switch) findViewById(R.id.app_switch);
		sw.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (!isChecked) {
					finish();
				}				
			}			
		});

		mLongitudeView = (TextView) findViewById(R.id.longitude_value);
		mLatitudeView = (TextView) findViewById(R.id.latitude_value);
		mAltitudeView = (TextView) findViewById(R.id.altitude_value);
		mFromTextView = (TextView) findViewById(R.id.from_value); 
		mToTextView = (TextView) findViewById(R.id.to_value);
		mSpeedView = (TextView)  findViewById(R.id.speed_value);

		Intent intent = new Intent(MonitorActivity.this, AutoGPSLogService.class);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.monitor, menu);
		return true;
	}

	@Override
	protected void onDestroy() {

//		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		unbindService(mConnection);

		Intent intent = new Intent(MonitorActivity.this, AutoGPSLogService.class);
		stopService(intent);

		super.onDestroy();
	}

	@Override
	protected void onPause() {
		
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();

		mLogInfo = new LogInfo(this);

//		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		mHandler.post(new Runnable() {

			@Override
			public void run() {
				do{
					if (null == mService) { break; }
					
					final Location location = mService.getLocation();
					
					if (null == location) { break; }

					final NumberFormat df = NumberFormat.getInstance();
					df.setMaximumFractionDigits(7);

					if (null == mLongitudeView) { break; }
					mLongitudeView.setText(df.format(location.getLongitude()));
					
					if (null == mLatitudeView) { break; }
					mLatitudeView.setText(df.format(location.getLatitude()));
					
					if (null == mAltitudeView) { break; }
					mAltitudeView.setText(df.format(location.getAltitude()));
					
					if (null == mFromTextView) { break; }
					Date date = new Date(location.getTime());
					SimpleDateFormat sdf = new SimpleDateFormat("HH-mm-ss", Locale.JAPAN);
					mFromTextView.setText(sdf.format(date));
					
					if (null == mToTextView) { break; }
					mToTextView.setText(df.format(mService.getDistance()));
					
					if (null == mSpeedView) { break; }
					mSpeedView.setText(df.format(location.getSpeed()));

				}while(false);
				// TODO Auto-generated method stub
				mHandler.postDelayed(this, mIntervalMillis);
			}
		});
	}
}
