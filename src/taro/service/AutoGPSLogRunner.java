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

    private final LogInfo mLogInfo;

    private final CurrentDateFileName mCurrentDateFileName;

    private Location mLocation = null;

    public AutoGPSLogRunner(Context context,
            GPSListener listener,
            Handler handler) {

        mContext = context;
        mGPSListener = listener;
        mHandler = handler;

        Resources res = context.getResources();
        mIntervalMillis = res.getInteger(R.integer.runner_interval_millis);
        res.getInteger(R.integer.runner_history_size);

        mHistory = new LocationHistory(res.getInteger(R.integer.runner_history_size));
        mLogInfo = new LogInfo(mContext);
        mCurrentDateFileName = new CurrentDateFileName(
                mLogInfo.getValuePrefix());
    }


    public double getDistance() {

        return mHistory.getDistance();
    }


    @Override
    public void run() {

        mLocation = mGPSListener.getLocation();
        mHistory.update(mLocation);
        log();

        mHandler.postDelayed(this, mIntervalMillis);
    }


    private void log() {

        if (mHistory.hasSpeed()) {
            String contents = mHistory.toString();
            if (null != contents) {
                // 安定して速度が大きいときに記録する
                Intent intent = new Intent(mContext, LogIntentService.class);
                intent.putExtra(mLogInfo.getKeyFilename(),
                        mCurrentDateFileName.getFileName());
                intent.putExtra(mLogInfo.getKeyContents(), mHistory.toString());
                intent.putExtra(mLogInfo.getKeyAppend(), true);
                mContext.startService(intent);
            }
        }
    }


    public Location getLocation() {

        return mHistory.getLocation();
    }


}
