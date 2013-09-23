package taro.service;


import taro.app.logger.gps.auto.R;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;

import android.content.Context;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;

/**
 * GPSで位置情報を得る
 * 
 * properties->Android Library: google-play-services_lib
 * <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
 */
public class GPSListener implements 
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener,
LocationListener {

	private static final String TAG = "GPSListener";
	private final Context mContext;
	private final LocationRequest mLocationRequest;
	private final LocationClient mLocationClient;
	private Location mLocation = null;


	/**
	 * コンストラクタ
	 * @param context    リソースを得るため
	 */
	public GPSListener(Context context) {
		mContext = context;
		if (this.checkGooglePlayService()) {
			mLocationRequest = this.createRequest();
			mLocationClient = new LocationClient(mContext, this, this);
		}else{
			mLocationRequest = null;
			mLocationClient = null;
		}
	}
	
	
	/**
	 * GPS衛星と接続する。接続に成功したら、 onConnected() が呼ばれる。
	 * GPSを使用できない環境の場合は何もしない。
	 */
	public void connect() {
		if (null != mLocationClient) {
			mLocationClient.connect();
		}
	}
	
	
	/** 
	 * GPS衛星との接続を切る。GPSを使用できない環境の場合は何もしない。
	 *  */
	public void disconnect() {
		if (null != mLocationClient) {
			mLocationClient.disconnect();
		}
	}
	
	/**
	 * getter
	 * @return    最新の位置情報
	 */
	public Location getLocation() {
		return mLocation;
	}

	/* **************** 以下はインタフェースの実装。直接呼ぶ必要はない。 **************** */
	
	@Override
	public void onLocationChanged(Location location) {
		mLocation = mLocationClient.getLastLocation();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		mLocationClient.requestLocationUpdates(mLocationRequest, this);
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}


	private boolean checkGooglePlayService() {
		return ConnectionResult.SUCCESS
				== GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);
	}

	private LocationRequest createRequest() {
    	Resources resouces = mContext.getResources();
    	final int intervalMillis = resouces.getInteger(R.integer.location_interval_millis);

    	return LocationRequest.create()
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY) // use GPS
			.setFastestInterval(intervalMillis) // update interval
			.setInterval(intervalMillis);
	}
}
