package taro.service;

import taro.app.logger.gps.auto.LogInfo;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.Window;

public class AutoGPSLogService extends Service {

    public class AutoGPSLogBinder extends Binder {
    	public AutoGPSLogService getService() {
            return AutoGPSLogService.this;
        }
    }
    
    private static final String TAG = "AutoGPSLogService";
    private final AutoGPSLogBinder mBinder = new AutoGPSLogBinder();
    private final Handler mHandler = new Handler();
    private AutoGPSLogRunner mRunner;
    private GPSListener mGPSListener;
	
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		enableGPS();
		
		mGPSListener = new GPSListener(this);
		mGPSListener.connect();
		
		mRunner = new AutoGPSLogRunner(this, mGPSListener, mHandler);
		
		mHandler.post(mRunner);
		
		Intent intent = new Intent(AutoGPSLogService.this, BrightenByHandService.class);
		startService(intent);
	}

	private void enableGPS() {
		String providers =
				Secure.getString(getContentResolver(), Secure.LOCATION_PROVIDERS_ALLOWED);
		Log.i(TAG, "providers: " + providers);
		if (providers.indexOf("gps", 0) == -1) {
			// GPS が無効になっているので設定画面を開く
			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}
	}

	@Override
	public void onDestroy() {

		Intent intent = new Intent(AutoGPSLogService.this, BrightenByHandService.class);
		stopService(intent);
		
		mHandler.removeCallbacks(mRunner);
		
		mGPSListener.disconnect();
		super.onDestroy();
	}
	
	public Location getLocation() {
		return mGPSListener.getLocation();
	}

	public double getDistance() {
		return mRunner.getDistance();
	}
}
