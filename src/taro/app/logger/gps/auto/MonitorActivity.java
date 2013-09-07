package taro.app.logger.gps.auto;


import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

/**
 * GPSのデータを表示する。
 *
 */
public class MonitorActivity extends Activity {

	private static final String TAG = "MonitorActivity";

	private LogInfo mLogInfo;

	public MonitorActivity() {
		super();

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_monitor);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();

		mLogInfo = new LogInfo(this);

		Intent intent = new Intent(MonitorActivity.this, LogIntentService.class);
		intent.putExtra(mLogInfo.getKeyFilename(), new CurrentDateFileName().getFileName());
		intent.putExtra(mLogInfo.getKeyContents(), "test");
		intent.putExtra(mLogInfo.getKeyAppend(), true);
		startService(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.monitor, menu);
		return true;
	}

}
