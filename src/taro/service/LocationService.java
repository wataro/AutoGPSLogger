package taro.service;



import taro.app.logger.gps.auto.R;
import taro.app.logger.gps.auto.R.integer;
import android.app.Service;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

/**
 * GPSで位置情報を得るサービス
 * 
 * properties->Android Library: google-play-services_lib
 * <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
 */
public class LocationService extends Service implements
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener,
LocationListener {

	/**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocationBinder extends Binder {
    	public LocationService getService() {
            // Return this instance of LocalService so clients can call public methods
            return LocationService.this;
        }
    }

	private static final String TAG = "LocationService";
	private final LocationBinder mBinder = new LocationBinder();
	private Location mLocation = null;
	private LocationClient mLocationClient;

	private LocationRequest mLocationRequest;


	private boolean checkGooglePlayService() {
		return ConnectionResult.SUCCESS
				== GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	}

	private void configureLocation() {

    	Resources resouces = this.getResources();
    	final int intervalMillis = resouces.getInteger(R.integer.location_interval_millis);

    	mLocationRequest = LocationRequest.create()
    			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY) // use GPS
    			.setFastestInterval(intervalMillis) // update interval
    			.setInterval(intervalMillis);
    }

	/**
	 * getter
	 * @return    現在保存している位置情報    
	 */
	public Location getLocation() {
		return mLocation;
	}


	@Override
	public IBinder onBind(Intent arg0) {
		startGPS();
		return mBinder;
	}
	

	@Override
	public void onConnected(Bundle connectionHint) {
		mLocationClient.requestLocationUpdates(mLocationRequest, this);
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Toast.makeText(this, "LocationService.onConnectionFailed", Toast.LENGTH_SHORT).show();		
	}

	
	
	@Override
	public void onDestroy() {
		if (null != mLocationClient) {
			mLocationClient.disconnect();
		}
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	
	@Override
	public void onDisconnected() {
		Toast.makeText(this, "LocationService.onDisconnected", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onLocationChanged(Location location) {
		mLocation = mLocationClient.getLastLocation();
	}
	
    @Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		startGPS();
		
		Toast.makeText(this, "LocationService.onStartCommand", Toast.LENGTH_SHORT).show();

		return super.onStartCommand(intent, flags, startId);
	}

	private void startGPS() {
		if (!checkGooglePlayService()) {
			this.stopSelf();
		}

		configureLocation();

		/*
		 * Create a new location client, 
		 * using the enclosing class to handle callbacks.
		 */
		mLocationClient = new LocationClient(this, this, this);

		mLocationClient.connect();
	}
}
