package taro.service;



import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;



import taro.app.logger.gps.auto.CurrentDateFileName;
import taro.app.logger.gps.auto.LogInfo;
import taro.app.logger.gps.auto.R;
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

    private final LocationHistory mHistory;

    private final int mHistorySize;

    private final Location[] mLocations;

    private final LogInfo mLogInfo;

    private final CurrentDateFileName mCurrentDateFileName;

    private Location mLocation = null;

    private int mCounter = 0;

    private boolean mIsLogging = false;

    private static final String TAG = "AutoGPSLogRunner";


    public AutoGPSLogRunner(Context context,
            GPSListener listener,
            Handler handler) {

        mContext = context;
        mGPSListener = listener;
        mHandler = handler;

        Resources res = context.getResources();
        mIntervalMillis = res.getInteger(R.integer.runner_interval_millis);
        mHistorySize = res.getInteger(R.integer.runner_history_size);
        mHistory = new LocationHistory(context);
        mLocations = new Location[mHistorySize];
        mLogInfo = new LogInfo(mContext);
        mCurrentDateFileName = new CurrentDateFileName(
                mLogInfo.getValuePrefix());
    }


    public double getDistance() {

        return mHistory.getDistance();
    }


    @Override
    public void run() {

        do {
            mLocation = mGPSListener.getLocation();
//            if (null == mLocation) {
//                break;
//            }
//
//            if (!doesHistoryFilled()) {
//                break;
//            }
//            if (mLocation.getTime() == getPrevious().getTime()) {
//                // 更新時間になっても値が更新されていない場合はパス
//                break;
//            }
//            logAutomatically();
//            updateHistory();
            mHistory.update(mLocation);
            log();

        } while (false);
        mHandler.postDelayed(this, mIntervalMillis);
    }


    private void log() {

        if (mHistory.hasSpeed()) {
            // 安定して速度が大きいときに記録する
            Intent intent = new Intent(mContext, LogIntentService.class);
            intent.putExtra(mLogInfo.getKeyFilename(),
                    mCurrentDateFileName.getFileName());
            intent.putExtra(mLogInfo.getKeyContents(), mHistory.toString());
            intent.putExtra(mLogInfo.getKeyAppend(), true);
            mContext.startService(intent);
        }
    }


    public Location getLocation() {

        return mHistory.getLocation();
    }


}
