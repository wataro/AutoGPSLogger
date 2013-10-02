package taro.service;



import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.Settings;
import android.util.Log;



public class BrightenByHandService extends Service implements
        SensorEventListener {

    private static final String TAG = "BrightenByHandService";

    private static final int SENSOR_NAME = Sensor.TYPE_PROXIMITY;

    private static final int SENSOR_DELAY = SensorManager.SENSOR_DELAY_NORMAL;

    private SensorManager mManager;

    private int mDefaultScreenOffTimeout;

    private WakeLock mWakeLock;


    public BrightenByHandService() {

        // TODO Auto-generated constructor stub
    }


    @Override
    public IBinder onBind(Intent arg0) {

        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public void onCreate() {

        super.onCreate();

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        mWakeLock.acquire();

        mDefaultScreenOffTimeout = Settings.System.getInt(getContentResolver(),
                Settings.System.SCREEN_OFF_TIMEOUT, 60000);
        Settings.System.putInt(getContentResolver(),
                Settings.System.SCREEN_OFF_TIMEOUT, 5000);
        Log.i(TAG,
                Settings.System.SCREEN_OFF_TIMEOUT
                        + Settings.System.getString(getContentResolver(),
                                Settings.System.SCREEN_OFF_TIMEOUT));

        mManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        for (Sensor s : mManager.getSensorList(Sensor.TYPE_ALL)) {
            Log.i(TAG, s.getName());
        }

        Sensor sensor = mManager.getDefaultSensor(SENSOR_NAME);
        if (null != sensor) {
            mManager.registerListener(this, sensor, SENSOR_DELAY);
        }
    }


    @Override
    public void onDestroy() {

        mManager.unregisterListener(this);

        Settings.System.putInt(getContentResolver(),
                Settings.System.SCREEN_OFF_TIMEOUT, mDefaultScreenOffTimeout);

        mWakeLock.release();

        super.onDestroy();
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

        // TODO Auto-generated method stub

    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == SENSOR_NAME) {
            Log.i(TAG, String.valueOf(event.values[0]));
            if (event.values[0] < event.sensor.getMaximumRange() / 2f) {
                // 手が近いので、バックライトを点灯させる
                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                WakeLock lock = pm.newWakeLock(
                        PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                                | PowerManager.ACQUIRE_CAUSES_WAKEUP
                                | PowerManager.ON_AFTER_RELEASE, TAG);
                lock.acquire(1000);
                // //ここの間、画面をONのままにできる
                // lock.release();

            }
        }
    }
}
