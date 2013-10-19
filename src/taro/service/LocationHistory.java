package taro.service;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;



import taro.app.logger.gps.auto.R;
import android.content.Context;
import android.content.res.Resources;
import android.location.Location;
import android.util.Log;


public class LocationHistory {

    private static final String TAG = "LocationHistory";

    /**
     * 位置情報の履歴
     * 
     * サイズは mHistorySize
     * 
     * 最初は全要素が null
     * 
     * update が要素数の回数呼ばれるまでは、updateの引数である location そのままの値が入る。
     * 
     * それ以上になると、 location そのままではなくて、緯度・経度・高度に指数平滑化を施す。
     */
    private final Location[] mHistory;
    private final int mHistorySize;

    /**
     * 現在の位置情報を参照するための配列添字
     */
    private int mCurrentIndex;

    /**
     * 一期前の位置情報を参照するための配列添字
     */
    private int mPreviousIndex;

    private double mDistance = 0;

    public LocationHistory(int historySize) {

        mHistorySize = historySize;
        mHistory = new Location[mHistorySize];

        mCurrentIndex = 0;
        mPreviousIndex = mHistorySize - 1;
    }


    public void update(Location location) {

        if (null == location) {
            return;
        }
        new Location(location);
        if (null == mHistory[mCurrentIndex]) {
            for (int i = 0; i < mHistory.length; ++i) {
                mHistory[i] = new Location(location);
            }
            return;
        }
        if (location.getTime() == mHistory[mPreviousIndex].getTime()) {
            // 位置情報が更新されていない。
            return;
        }
        setSmoothedLocation(location);
        updateDistance();
        Log.d(TAG, toString());

        updateIndex();
    }


    private void updateDistance() {

        if (1 < mHistory[mCurrentIndex].getSpeed()) {
            /* 1[m/s]より大きい速度のときだけ距離を更新する */
            mDistance += mHistory[mPreviousIndex].distanceTo(mHistory[mCurrentIndex]);
        }
    }


    @Override
    public String toString() {

        NumberFormat df = NumberFormat.getInstance();
        df.setMaximumFractionDigits(7);

        Location loc = mHistory[mCurrentIndex];
        if (null == loc) { return null; }
        Date date = new Date(loc.getTime());
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.JAPAN);

        String text = "lat: " + loc.getLatitude();
        text += "  long: " + loc.getLongitude();
        text += "  alt: " + df.format(loc.getAltitude());
        text += "  spd: " + df.format(loc.getSpeed() * 3.6); // [km/h]
        text += "  time: " + sdf.format(date);
        text += "  dist: " + df.format(mDistance);
        text += "  lat0: " + mHistory[mPreviousIndex].getLatitude();
        text += "  long0: " + mHistory[mPreviousIndex].getLongitude();
        text += "  cur: " + mCurrentIndex;
        text += "  pre: " + mPreviousIndex;
        text += "\n";
        return text;
    }


    /**
     * @return  真: 安定して速度が大きい
     */
    public boolean hasSpeed() {

        for (Location loc: mHistory) {
            if (null == loc) {
                return false;
            }
            if (loc.getSpeed() <= 1) {
                return false;
            }
        }
        return true;
    }


    public Location getLocation() {
        return mHistory[mCurrentIndex];
    }


    /**
     * 配列添字を更新する
     */
    private void updateIndex() {
        mPreviousIndex = mCurrentIndex;
        mCurrentIndex = (mCurrentIndex + 1) % mHistorySize;
    }


    /**
     * 指数平滑化を施した値を履歴として保存する
     * @param location
     */
    private void setSmoothedLocation(Location location) {
        // 各フィールドのデフォルト値は location そのまま
        mHistory[mCurrentIndex].set(location);

        // 緯度・経度・高度だけ、指数平滑化を入れる
        double previousTerm = mHistory[mPreviousIndex].getLongitude() * 0.1;
        double currentTerm = mHistory[mCurrentIndex].getLongitude() * 0.9;
        mHistory[mCurrentIndex].setLongitude(previousTerm + currentTerm);
        previousTerm = mHistory[mPreviousIndex].getLatitude() * 0.1;
        currentTerm = mHistory[mCurrentIndex].getLatitude() * 0.9;
        mHistory[mCurrentIndex].setLatitude(previousTerm + currentTerm);
        previousTerm = mHistory[mPreviousIndex].getAltitude() * 0.1;
        currentTerm = mHistory[mCurrentIndex].getAltitude() * 0.9;
        mHistory[mCurrentIndex].setAltitude(previousTerm + currentTerm);
    }


    public double getDistance() {

        return mDistance;
    }
}
