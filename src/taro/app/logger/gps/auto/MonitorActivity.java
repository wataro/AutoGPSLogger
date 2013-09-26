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

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {

            // We've bound to LocalService, cast the IBinder and get
            // LocalService instance
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

    private LogInfo mLogInfo;

    private AutoGPSLogService mService = null;

    private TextView mToTextView = null;

    private TextView mSpeedView = null;

    private Location mLocation = null;

    private double mDistance = 0;

    private String mDateFormat;


    public MonitorActivity() {

        super();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

        mIntervalMillis = getResources().getInteger(
                R.integer.view_interval_millis);

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

        mFromTextView = (TextView) findViewById(R.id.from_value);
        mToTextView = (TextView) findViewById(R.id.distance_value);
        mSpeedView = (TextView) findViewById(R.id.speed_value);

        mDateFormat = getResources().getString(R.string.date_format);

        Intent intent = new Intent(MonitorActivity.this,
                AutoGPSLogService.class);
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

        // getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        unbindService(mConnection);

        Intent intent = new Intent(MonitorActivity.this,
                AutoGPSLogService.class);
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

        // getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // Lock解除画面より手前に表示させる
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        mHandler.post(new Runnable() {

            private final SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(
                    mDateFormat, Locale.JAPAN);

            private final NumberFormat mDistanceFormat = NumberFormat
                    .getInstance();

            private final NumberFormat mSpeedFormat = NumberFormat
                    .getInstance();


            @Override
            public void run() {

                do {
                    if (null == mService) {
                        break;
                    }

                    final Location location = mService.getLocation();

                    if (null == location) {
                        break;
                    }

                    if (null == mFromTextView) {
                        break;
                    }
                    Date date = new Date(location.getTime());
                    mFromTextView.setText(mSimpleDateFormat.format(date));

                    if (null == mToTextView) {
                        break;
                    }
                    mDistanceFormat.setMaximumFractionDigits(3);
                    mDistanceFormat.setMinimumFractionDigits(3);
                    mDistanceFormat.setMinimumIntegerDigits(3);
                    mToTextView.setText(mDistanceFormat.format(mService
                            .getDistance()));

                    if (null == mSpeedView) {
                        break;
                    }
                    mSpeedFormat.setMaximumFractionDigits(1);
                    mSpeedFormat.setMinimumFractionDigits(1);
                    mSpeedFormat.setMinimumIntegerDigits(2);
                    mSpeedView.setText(mSpeedFormat.format(location.getSpeed()));

                } while (false);
                // TODO Auto-generated method stub
                mHandler.postDelayed(this, mIntervalMillis);
            }
        });
    }
}
